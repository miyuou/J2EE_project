package com.myapp.entity;

import com.myapp.entity.enums.PaymentMethod;
import com.myapp.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.UNPAID;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod method;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bid_id")
    private Bid bid;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_object_id")
    private AuctionObject auctionObject;

    // Constructors
    public Payment() {
    }

    public Payment(BigDecimal amount, PaymentMethod method, User user, Bid bid) {
        this.amount = amount;
        this.date = LocalDateTime.now();
        this.method = method;
        this.user = user;
        this.bid = bid;
    }

    public Payment(BigDecimal amount, PaymentMethod method, User user, AuctionObject auctionObject) {
        this.amount = amount;
        this.date = LocalDateTime.now();
        this.method = method;
        this.user = user;
        this.auctionObject = auctionObject;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Bid getBid() {
        return bid;
    }

    public void setBid(Bid bid) {
        this.bid = bid;
    }

    public AuctionObject getAuctionObject() {
        return auctionObject;
    }

    public void setAuctionObject(AuctionObject auctionObject) {
        this.auctionObject = auctionObject;
    }
} 