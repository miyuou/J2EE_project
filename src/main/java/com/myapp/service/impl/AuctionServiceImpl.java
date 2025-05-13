package com.myapp.service.impl;

import com.myapp.dto.AuctionObjectDTO;
import com.myapp.entity.AuctionObject;
import com.myapp.entity.Bid;
import com.myapp.entity.User;
import com.myapp.entity.enums.AuctionStatus;
import com.myapp.exception.AuctionException;
import com.myapp.repository.AuctionObjectRepository;
import com.myapp.repository.UserRepository;
import com.myapp.service.AuctionService;
import com.myapp.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuctionServiceImpl implements AuctionService {

    private final AuctionObjectRepository auctionRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public AuctionServiceImpl(
            AuctionObjectRepository auctionRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService) {
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public AuctionObjectDTO createAuction(AuctionObjectDTO auctionDTO, Long userId, MultipartFile image) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new AuctionException("User not found"));

        AuctionObject auction = new AuctionObject();
        auction.setTitle(auctionDTO.getTitle());
        auction.setDescription(auctionDTO.getDescription());
        auction.setStartPrice(auctionDTO.getStartPrice());
        auction.setCurrentPrice(auctionDTO.getStartPrice());
        auction.setEndDate(auctionDTO.getEndDate());
        auction.setStatus(AuctionStatus.ACTIVE);
        auction.setOwner(owner);

        if (image != null && !image.isEmpty()) {
            String imagePath = fileStorageService.storeFile(image);
            auction.setImagePath(imagePath);
        }

        AuctionObject savedAuction = auctionRepository.save(auction);
        return convertToDTO(savedAuction);
    }

    @Override
    public AuctionObjectDTO updateAuction(Long id, AuctionObjectDTO auctionDTO, MultipartFile image) {
        AuctionObject auction = auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionException("Auction not found"));

        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new AuctionException("Cannot update non-active auction");
        }

        auction.setTitle(auctionDTO.getTitle());
        auction.setDescription(auctionDTO.getDescription());
        auction.setEndDate(auctionDTO.getEndDate());

        if (image != null && !image.isEmpty()) {
            if (auction.getImagePath() != null) {
                fileStorageService.deleteFile(auction.getImagePath());
            }
            String imagePath = fileStorageService.storeFile(image);
            auction.setImagePath(imagePath);
        }

        AuctionObject updatedAuction = auctionRepository.save(auction);
        return convertToDTO(updatedAuction);
    }

    @Override
    public void deleteAuction(Long id) {
        AuctionObject auction = auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionException("Auction not found"));

        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new AuctionException("Cannot delete non-active auction");
        }

        if (auction.getImagePath() != null) {
            fileStorageService.deleteFile(auction.getImagePath());
        }

        auctionRepository.delete(auction);
    }

    @Override
    public AuctionObjectDTO getAuction(Long id) {
        AuctionObject auction = auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionException("Auction not found"));
        return convertToDTO(auction);
    }

    @Override
    public Page<AuctionObjectDTO> getAllAuctions(Pageable pageable) {
        return auctionRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<AuctionObjectDTO> getAuctionsByStatus(AuctionStatus status, Pageable pageable) {
        return auctionRepository.findByStatus(status, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<AuctionObjectDTO> getAuctionsByOwner(Long ownerId, Pageable pageable) {
        return auctionRepository.findByOwnerId(ownerId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<AuctionObjectDTO> getAuctionsByCurrentBidder(Long bidderId, Pageable pageable) {
        return auctionRepository.findByCurrentBidderId(bidderId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<AuctionObjectDTO> getAuctionsWithUserBids(Long userId, Pageable pageable) {
        return auctionRepository.findAuctionsWithUserBids(userId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public List<AuctionObjectDTO> getFeaturedAuctions() {
        LocalDateTime now = LocalDateTime.now();
        return auctionRepository.findActiveAuctions(AuctionStatus.ACTIVE, now)
                .stream()
                .limit(6)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AuctionObjectDTO placeBid(Long auctionId, Long userId, BigDecimal amount) {
        AuctionObject auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionException("Auction not found"));

        User bidder = userRepository.findById(userId)
                .orElseThrow(() -> new AuctionException("User not found"));

        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new AuctionException("Auction is not active");
        }

        if (auction.getEndDate().isBefore(LocalDateTime.now())) {
            throw new AuctionException("Auction has ended");
        }

        if (amount.compareTo(auction.getCurrentPrice()) <= 0) {
            throw new AuctionException("Bid amount must be higher than current price");
        }

        if (auction.getOwner().getId().equals(userId)) {
            throw new AuctionException("Cannot bid on your own auction");
        }

        Bid bid = new Bid();
        bid.setAmount(amount);
        bid.setUser(bidder);
        bid.setAuctionObject(auction);
        bid.setTimestamp(LocalDateTime.now());

        auction.addBid(bid);
        auction.setCurrentPrice(amount);
        auction.setCurrentBidder(bidder);

        AuctionObject updatedAuction = auctionRepository.save(auction);
        return convertToDTO(updatedAuction);
    }

    @Override
    public void checkExpiredAuctions() {
        LocalDateTime now = LocalDateTime.now();
        List<AuctionObject> expiredAuctions = auctionRepository.findExpiredAuctions(AuctionStatus.ACTIVE, now);

        for (AuctionObject auction : expiredAuctions) {
            auction.setStatus(AuctionStatus.COMPLETED);
            auctionRepository.save(auction);
        }
    }

    @Override
    public void updateAuctionStatus(Long id, AuctionStatus status) {
        AuctionObject auction = auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionException("Auction not found"));
        auction.setStatus(status);
        auctionRepository.save(auction);
    }

    @Override
    public void checkAndUpdateExpiredAuctions() {
        checkExpiredAuctions();
    }

    @Override
    public AuctionObjectDTO convertToDTO(AuctionObject auction) {
        AuctionObjectDTO dto = new AuctionObjectDTO();
        dto.setId(auction.getId());
        dto.setTitle(auction.getTitle());
        dto.setDescription(auction.getDescription());
        dto.setStartPrice(auction.getStartPrice());
        dto.setCurrentPrice(auction.getCurrentPrice());
        dto.setEndDate(auction.getEndDate());
        dto.setStatus(auction.getStatus());
        dto.setImagePath(auction.getImagePath());
        dto.setOwnerId(auction.getOwner().getId());
        dto.setOwnerName(auction.getOwner().getUsername());
        if (auction.getCurrentBidder() != null) {
            dto.setCurrentBidderId(auction.getCurrentBidder().getId());
            dto.setCurrentBidderName(auction.getCurrentBidder().getUsername());
        }
        return dto;
    }

    @Override
    public AuctionObject convertToEntity(AuctionObjectDTO auctionDTO) {
        AuctionObject auction = new AuctionObject();
        auction.setId(auctionDTO.getId());
        auction.setTitle(auctionDTO.getTitle());
        auction.setDescription(auctionDTO.getDescription());
        auction.setStartPrice(auctionDTO.getStartPrice());
        auction.setCurrentPrice(auctionDTO.getCurrentPrice());
        auction.setEndDate(auctionDTO.getEndDate());
        auction.setStatus(auctionDTO.getStatus());
        auction.setImagePath(auctionDTO.getImagePath());
        return auction;
    }

    @Override
    public Object getActiveAuctions() {
        return null;
    }

    private void updateBidStatuses(AuctionObject auction) {
        for (Bid bid : auction.getBids()) {
            bid.setAuctionObject(auction);
        }
    }
} 