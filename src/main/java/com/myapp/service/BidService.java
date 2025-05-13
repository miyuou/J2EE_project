package com.myapp.service;

import com.myapp.dto.BidDTO;
import com.myapp.entity.Bid;
import com.myapp.entity.enums.BidStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface BidService {
    BidDTO createBid(BidDTO bidDTO);
    BidDTO updateBid(Long id, BidDTO bidDTO);
    void deleteBid(Long id);
    Optional<BidDTO> getBidById(Long id);
    Page<BidDTO> getBidsByUserId(Long userId, Pageable pageable);
    Page<BidDTO> getBidsByAuctionId(Long auctionId, Pageable pageable);
    List<BidDTO> getTopBidsForAuction(Long auctionId);
    Optional<BidDTO> getWinningBidForAuction(Long auctionId);
    long countBidsForAuction(Long auctionId);
    Optional<Double> getHighestBidAmountForAuction(Long auctionId);
    void updateBidStatus(Long id, BidStatus status);
    boolean isBidValid(BidDTO bidDTO);
    BidDTO convertToDTO(Bid bid);
    Bid convertToEntity(BidDTO bidDTO);
} 