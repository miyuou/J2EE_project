package com.myapp.dto;

import com.myapp.entity.enums.BidStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BidDTO {
    private Long id;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private LocalDateTime timestamp;
    private BidStatus status;
    private Long userId;
    private String username;
    private Long auctionId;
    private Long auctionObjectId;
    private String auctionTitle;

    // Constructors
    public BidDTO() {
    }

    public BidDTO(BigDecimal amount, Long userId, Long auctionId) {
        this.amount = amount;
        this.userId = userId;
        this.auctionId = auctionId;
        this.timestamp = LocalDateTime.now();
        this.status = BidStatus.PENDING;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public BidStatus getStatus() {
        return status;
    }

    public void setStatus(BidStatus status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public Long getAuctionObjectId() {
        return auctionObjectId;
    }

    public void setAuctionObjectId(Long auctionObjectId) {
        this.auctionObjectId = auctionObjectId;
    }

    public String getAuctionTitle() {
        return auctionTitle;
    }

    public void setAuctionTitle(String auctionTitle) {
        this.auctionTitle = auctionTitle;
    }
} 