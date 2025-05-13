package com.myapp.controller.rest;

import com.myapp.dto.BidDTO;
import com.myapp.entity.enums.BidStatus;
import com.myapp.service.BidService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
public class BidRestController {

    private final BidService bidService;

    @Autowired
    public BidRestController(BidService bidService) {
        this.bidService = bidService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<BidDTO>> getBidsByUserId(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return ResponseEntity.ok(bidService.getBidsByUserId(userId, pageRequest));
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<Page<BidDTO>> getBidsByAuctionId(
            @PathVariable Long auctionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("amount").descending());
        return ResponseEntity.ok(bidService.getBidsByAuctionId(auctionId, pageRequest));
    }

    @GetMapping("/auction/{auctionId}/top")
    public ResponseEntity<List<BidDTO>> getTopBidsForAuction(@PathVariable Long auctionId) {
        return ResponseEntity.ok(bidService.getTopBidsForAuction(auctionId));
    }

    @GetMapping("/auction/{auctionId}/winning")
    public ResponseEntity<BidDTO> getWinningBidForAuction(@PathVariable Long auctionId) {
        return bidService.getWinningBidForAuction(auctionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/auction/{auctionId}/count")
    public ResponseEntity<Long> countBidsForAuction(@PathVariable Long auctionId) {
        return ResponseEntity.ok(bidService.countBidsForAuction(auctionId));
    }

    @GetMapping("/auction/{auctionId}/highest")
    public ResponseEntity<Double> getHighestBidAmountForAuction(@PathVariable Long auctionId) {
        return bidService.getHighestBidAmountForAuction(auctionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BidDTO> createBid(@Valid @RequestBody BidDTO bidDTO) {
        try {
            if (!bidService.isBidValid(bidDTO)) {
                return ResponseEntity.badRequest().build();
            }
            BidDTO createdBid = bidService.createBid(bidDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBid);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BidDTO> updateBid(
            @PathVariable Long id,
            @Valid @RequestBody BidDTO bidDTO) {
        try {
            BidDTO updatedBid = bidService.updateBid(id, bidDTO);
            return ResponseEntity.ok(updatedBid);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteBid(@PathVariable Long id) {
        try {
            bidService.deleteBid(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> updateBidStatus(
            @PathVariable Long id,
            @RequestParam BidStatus status) {
        try {
            bidService.updateBidStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 