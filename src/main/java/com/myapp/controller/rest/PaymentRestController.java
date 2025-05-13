package com.myapp.controller.rest;

import com.myapp.dto.PaymentDTO;
import com.myapp.entity.enums.PaymentStatus;
import com.myapp.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentRestController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentRestController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<PaymentDTO>> getPaymentsByUserId(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId, pageRequest));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByStatus(
            @PathVariable PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status));
    }

    @GetMapping("/bid/{bidId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentDTO> getPaymentByBidId(@PathVariable Long bidId) {
        return paymentService.getPaymentByBidId(bidId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/auction/{auctionId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentDTO> getPaymentByAuctionId(@PathVariable Long auctionId) {
        return paymentService.getPaymentByAuctionId(auctionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/total")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BigDecimal> getTotalPaidAmountByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getTotalPaidAmountByUserId(userId));
    }

    @GetMapping("/user/{userId}/count")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Integer> countPaidPaymentsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.countPaidPaymentsByUserId(userId));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        try {
            if (!paymentService.isPaymentValid(paymentDTO)) {
                return ResponseEntity.badRequest().build();
            }
            PaymentDTO createdPayment = paymentService.createPayment(paymentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentDTO> updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentDTO paymentDTO) {
        try {
            PaymentDTO updatedPayment = paymentService.updatePayment(id, paymentDTO);
            return ResponseEntity.ok(updatedPayment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        try {
            paymentService.deletePayment(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status) {
        try {
            paymentService.updatePaymentStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 