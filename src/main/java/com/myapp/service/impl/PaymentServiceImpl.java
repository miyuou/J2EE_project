package com.myapp.service.impl;

import com.myapp.dto.PaymentDTO;
import com.myapp.entity.AuctionObject;
import com.myapp.entity.Bid;
import com.myapp.entity.Payment;
import com.myapp.entity.User;
import com.myapp.entity.enums.PaymentStatus;
import com.myapp.repository.AuctionObjectRepository;
import com.myapp.repository.BidRepository;
import com.myapp.repository.PaymentRepository;
import com.myapp.repository.UserRepository;
import com.myapp.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final AuctionObjectRepository auctionRepository;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, UserRepository userRepository,
                            BidRepository bidRepository, AuctionObjectRepository auctionRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
    }

    @Override
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        if (!isPaymentValid(paymentDTO)) {
            throw new RuntimeException("Invalid payment");
        }

        User user = userRepository.findById(paymentDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Payment payment = convertToEntity(paymentDTO);
        payment.setUser(user);
        payment.setStatus(PaymentStatus.UNPAID);

        if (paymentDTO.getBidId() != null) {
            Bid bid = bidRepository.findById(paymentDTO.getBidId())
                    .orElseThrow(() -> new RuntimeException("Bid not found"));
            payment.setBid(bid);
        }

        if (paymentDTO.getAuctionObjectId() != null) {
            AuctionObject auction = auctionRepository.findById(paymentDTO.getAuctionObjectId())
                    .orElseThrow(() -> new RuntimeException("Auction not found"));
            payment.setAuctionObject(auction);
        }

        Payment savedPayment = paymentRepository.save(payment);
        return convertToDTO(savedPayment);
    }

    @Override
    public PaymentDTO updatePayment(Long id, PaymentDTO paymentDTO) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        updatePaymentFromDTO(existingPayment, paymentDTO);
        Payment updatedPayment = paymentRepository.save(existingPayment);
        return convertToDTO(updatedPayment);
    }

    @Override
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDTO> getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDTO> getPaymentsByUserId(Long userId, Pageable pageable) {
        return paymentRepository.findByUserId(userId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDTO> getPaymentByBidId(Long bidId) {
        return paymentRepository.findByBidId(bidId)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDTO> getPaymentByAuctionId(Long auctionId) {
        return paymentRepository.findByAuctionObjectId(auctionId)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPaidAmountByUserId(Long userId) {
        return paymentRepository.findTotalPaidAmountByUserId(userId)
                .map(BigDecimal::valueOf)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public int countPaidPaymentsByUserId(Long userId) {
        return (int) paymentRepository.countPaidPaymentsByUserId(userId);
    }

    @Override
    public void updatePaymentStatus(Long id, PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(status);
        paymentRepository.save(payment);
    }

    @Override
    public boolean isPaymentValid(PaymentDTO paymentDTO) {
        if (paymentDTO == null || paymentDTO.getAmount() == null || paymentDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        return true;
    }

    @Override
    public PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setDate(payment.getDate());
        dto.setStatus(payment.getStatus());
        dto.setMethod(payment.getMethod());
        dto.setUserId(payment.getUser().getId());
        dto.setUsername(payment.getUser().getUsername());
        
        if (payment.getBid() != null) {
            dto.setBidId(payment.getBid().getId());
        }
        
        if (payment.getAuctionObject() != null) {
            dto.setAuctionObjectId(payment.getAuctionObject().getId());
        }
        
        return dto;
    }

    @Override
    public Payment convertToEntity(PaymentDTO paymentDTO) {
        Payment payment = new Payment();
        payment.setId(paymentDTO.getId());
        payment.setAmount(paymentDTO.getAmount());
        payment.setDate(paymentDTO.getDate());
        payment.setStatus(paymentDTO.getStatus());
        payment.setMethod(paymentDTO.getMethod());
        return payment;
    }

    private void updatePaymentFromDTO(Payment payment, PaymentDTO dto) {
        payment.setAmount(dto.getAmount());
        payment.setDate(dto.getDate());
        payment.setStatus(dto.getStatus());
        payment.setMethod(dto.getMethod());
        // Note: User, Bid, and AuctionObject relationships should be updated by the service layer
    }
} 