package com.myapp.service.impl;

import com.myapp.dto.BidDTO;
import com.myapp.entity.AuctionObject;
import com.myapp.entity.Bid;
import com.myapp.entity.User;
import com.myapp.entity.enums.AuctionStatus;
import com.myapp.entity.enums.BidStatus;
import com.myapp.repository.AuctionObjectRepository;
import com.myapp.repository.BidRepository;
import com.myapp.repository.UserRepository;
import com.myapp.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final AuctionObjectRepository auctionRepository;

    @Autowired
    public BidServiceImpl(BidRepository bidRepository, UserRepository userRepository,
                         AuctionObjectRepository auctionRepository) {
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
    }

    @Override
    public BidDTO createBid(BidDTO bidDTO) {
        if (!isBidValid(bidDTO)) {
            throw new RuntimeException("Invalid bid");
        }

        User user = userRepository.findById(bidDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        AuctionObject auction = auctionRepository.findById(bidDTO.getAuctionObjectId())
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        Bid bid = convertToEntity(bidDTO);
        bid.setUser(user);
        bid.setAuctionObject(auction);
        bid.setStatus(BidStatus.PENDING);

        Bid savedBid = bidRepository.save(bid);
        return convertToDTO(savedBid);
    }

    @Override
    public BidDTO updateBid(Long id, BidDTO bidDTO) {
        Bid existingBid = bidRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        existingBid.setAmount(bidDTO.getAmount());
        existingBid.setStatus(bidDTO.getStatus());

        Bid updatedBid = bidRepository.save(existingBid);
        return convertToDTO(updatedBid);
    }

    @Override
    public void deleteBid(Long id) {
        bidRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BidDTO> getBidById(Long id) {
        return bidRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BidDTO> getBidsByUserId(Long userId, Pageable pageable) {
        return bidRepository.findByUserId(userId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BidDTO> getBidsByAuctionId(Long auctionId, Pageable pageable) {
        return bidRepository.findByAuctionObjectId(auctionId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BidDTO> getTopBidsForAuction(Long auctionId) {
        return bidRepository.findTopBidsForAuction(auctionId, PageRequest.of(0, 5))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BidDTO> getWinningBidForAuction(Long auctionId) {
        return bidRepository.findWinningBidForAuction(auctionId)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public long countBidsForAuction(Long auctionId) {
        return bidRepository.countBidsForAuction(auctionId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Double> getHighestBidAmountForAuction(Long auctionId) {
        return bidRepository.findHighestBidAmountForAuction(auctionId);
    }

    @Override
    public void updateBidStatus(Long id, BidStatus status) {
        Bid bid = bidRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bid not found"));
        bid.setStatus(status);
        bidRepository.save(bid);
    }

    @Override
    public boolean isBidValid(BidDTO bidDTO) {
        AuctionObject auction = auctionRepository.findById(bidDTO.getAuctionObjectId())
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            return false;
        }

        Optional<Double> highestBid = getHighestBidAmountForAuction(auction.getId());
        if (highestBid.isPresent() && bidDTO.getAmount().doubleValue() <= highestBid.get()) {
            return false;
        }

        return bidDTO.getAmount().compareTo(auction.getStartPrice()) >= 0;
    }

    @Override
    public BidDTO convertToDTO(Bid bid) {
        BidDTO dto = new BidDTO();
        dto.setId(bid.getId());
        dto.setAmount(bid.getAmount());
        dto.setTimestamp(bid.getTimestamp());
        dto.setStatus(bid.getStatus());
        dto.setUserId(bid.getUser().getId());
        dto.setUsername(bid.getUser().getUsername());
        dto.setAuctionObjectId(bid.getAuctionObject().getId());
        dto.setAuctionTitle(bid.getAuctionObject().getTitle());
        return dto;
    }

    @Override
    public Bid convertToEntity(BidDTO bidDTO) {
        Bid bid = new Bid();
        bid.setId(bidDTO.getId());
        bid.setAmount(bidDTO.getAmount());
        bid.setTimestamp(bidDTO.getTimestamp());
        bid.setStatus(bidDTO.getStatus());
        return bid;
    }
} 