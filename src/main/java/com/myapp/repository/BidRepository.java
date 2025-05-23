package com.myapp.repository;

import com.myapp.entity.Bid;
import com.myapp.entity.enums.BidStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    Page<Bid> findByUserId(Long userId, Pageable pageable);
    Page<Bid> findByAuctionObjectId(Long auctionObjectId, Pageable pageable);

    
    @Query("SELECT b FROM Bid b WHERE b.auctionObject.id = ?1 ORDER BY b.amount DESC")
    List<Bid> findTopBidsForAuction(Long auctionObjectId, Pageable pageable);

    @Query("SELECT b FROM Bid b WHERE b.auctionObject.id = :auctionId ORDER BY b.amount DESC LIMIT 1")
    Optional<Bid> findWinningBidForAuction(@Param("auctionId") Long auctionId);
    
    @Query("SELECT COUNT(b) FROM Bid b WHERE b.auctionObject.id = ?1")
    long countBidsForAuction(Long auctionObjectId);
    
    @Query("SELECT MAX(b.amount) FROM Bid b WHERE b.auctionObject.id = ?1")
    Optional<Double> findHighestBidAmountForAuction(Long auctionObjectId);
} 