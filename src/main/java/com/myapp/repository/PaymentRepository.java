package com.myapp.repository;

import com.myapp.entity.Payment;
import com.myapp.entity.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findByUserId(Long userId, Pageable pageable);
    List<Payment> findByStatus(PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.bid.id = ?1")
    Optional<Payment> findByBidId(Long bidId);
    
    @Query("SELECT p FROM Payment p WHERE p.auctionObject.id = ?1")
    Optional<Payment> findByAuctionObjectId(Long auctionObjectId);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'PAID' AND p.user.id = ?1")
    Optional<Double> findTotalPaidAmountByUserId(Long userId);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'PAID' AND p.user.id = ?1")
    long countPaidPaymentsByUserId(Long userId);
} 