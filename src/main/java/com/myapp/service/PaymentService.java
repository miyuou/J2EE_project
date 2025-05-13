package com.myapp.service;

import com.myapp.dto.PaymentDTO;
import com.myapp.entity.Payment;
import com.myapp.entity.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    PaymentDTO createPayment(PaymentDTO paymentDTO);
    PaymentDTO updatePayment(Long id, PaymentDTO paymentDTO);
    void deletePayment(Long id);
    Optional<PaymentDTO> getPaymentById(Long id);
    Page<PaymentDTO> getPaymentsByUserId(Long userId, Pageable pageable);
    List<PaymentDTO> getPaymentsByStatus(PaymentStatus status);
    Optional<PaymentDTO> getPaymentByBidId(Long bidId);
    Optional<PaymentDTO> getPaymentByAuctionId(Long auctionId);
    BigDecimal getTotalPaidAmountByUserId(Long userId);
    int countPaidPaymentsByUserId(Long userId);
    void updatePaymentStatus(Long id, PaymentStatus status);
    boolean isPaymentValid(PaymentDTO paymentDTO);
    PaymentDTO convertToDTO(Payment payment);
    Payment convertToEntity(PaymentDTO paymentDTO);
} 