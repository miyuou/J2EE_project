package com.myapp.repository;

import com.myapp.entity.AuctionObject;
import com.myapp.entity.enums.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuctionObjectRepository extends JpaRepository<AuctionObject, Long> {
    /**
     * Find auctions by status with pagination
     */
    Page<AuctionObject> findByStatus(AuctionStatus status, Pageable pageable);

    /**
     * Find auctions by owner ID with pagination
     */
    Page<AuctionObject> findByOwnerId(Long ownerId, Pageable pageable);
    
    /**
     * Find active auctions that haven't expired yet
     */
    @Query("SELECT a FROM AuctionObject a WHERE a.status = :status AND a.endDate > :now")
    List<AuctionObject> findActiveAuctions(
        @Param("status") AuctionStatus status,
        @Param("now") LocalDateTime now
    );
    
    /**
     * Find expired auctions
     */
    @Query("SELECT a FROM AuctionObject a WHERE a.status = :status AND a.endDate <= :now")
    List<AuctionObject> findExpiredAuctions(
        @Param("status") AuctionStatus status,
        @Param("now") LocalDateTime now
    );

    /**
     * Find auctions by current bidder
     */
    Page<AuctionObject> findByCurrentBidderId(Long bidderId, Pageable pageable);

    /**
     * Find auctions with bids by a specific user
     */
    @Query("SELECT DISTINCT a FROM AuctionObject a JOIN a.bids b WHERE b.user.id = :userId")
    Page<AuctionObject> findAuctionsWithUserBids(
        @Param("userId") Long userId,
        Pageable pageable
    );
} 