package com.myapp.service;

import com.myapp.dto.AuctionObjectDTO;
import com.myapp.entity.AuctionObject;
import com.myapp.entity.enums.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AuctionService {
    /**
     * Create a new auction
     */
    AuctionObjectDTO createAuction(AuctionObjectDTO auctionDTO, Long userId, MultipartFile image);

    /**
     * Update an existing auction
     */
    AuctionObjectDTO updateAuction(Long id, AuctionObjectDTO auctionDTO, MultipartFile image);

    /**
     * Delete an auction
     */
    void deleteAuction(Long id);

    /**
     * Get a single auction by ID
     */
    AuctionObjectDTO getAuction(Long id);

    /**
     * Get all auctions with pagination
     */
    Page<AuctionObjectDTO> getAllAuctions(Pageable pageable);

    /**
     * Get auctions by status with pagination
     */
    Page<AuctionObjectDTO> getAuctionsByStatus(AuctionStatus status, Pageable pageable);

    /**
     * Get auctions by owner with pagination
     */
    Page<AuctionObjectDTO> getAuctionsByOwner(Long ownerId, Pageable pageable);

    /**
     * Get auctions by current bidder with pagination
     */
    Page<AuctionObjectDTO> getAuctionsByCurrentBidder(Long bidderId, Pageable pageable);

    /**
     * Get auctions with bids by a specific user
     */
    Page<AuctionObjectDTO> getAuctionsWithUserBids(Long userId, Pageable pageable);

    /**
     * Get featured auctions (limited to 6 active auctions)
     */
    List<AuctionObjectDTO> getFeaturedAuctions();

    /**
     * Place a bid on an auction
     */
    AuctionObjectDTO placeBid(Long auctionId, Long userId, BigDecimal amount);

    /**
     * Check and update expired auctions
     */
    void checkExpiredAuctions();

    // Business operations
    void updateAuctionStatus(Long id, AuctionStatus status);
    void checkAndUpdateExpiredAuctions();
    
    // Utility methods
    AuctionObjectDTO convertToDTO(AuctionObject auction);
    AuctionObject convertToEntity(AuctionObjectDTO auctionDTO);

    Object getActiveAuctions();
} 