package com.myapp.controller.rest;

import com.myapp.dto.AuctionObjectDTO;
import com.myapp.entity.enums.AuctionStatus;
import com.myapp.exception.AuctionException;
import com.myapp.service.AuctionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionRestController {

    private final AuctionService auctionService;

    @Autowired
    public AuctionRestController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @GetMapping
    public ResponseEntity<Page<AuctionObjectDTO>> getAllAuctions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) AuctionStatus status) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("endDate").ascending());
            Page<AuctionObjectDTO> auctions;
            if (status != null) {
                auctions = auctionService.getAuctionsByStatus(status, pageRequest);
            } else {
                auctions = auctionService.getAllAuctions(pageRequest);
            }
            return ResponseEntity.ok(auctions);
        } catch (AuctionException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/featured")
    public ResponseEntity<List<AuctionObjectDTO>> getFeaturedAuctions() {
        try {
            return ResponseEntity.ok(auctionService.getFeaturedAuctions());
        } catch (AuctionException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionObjectDTO> getAuctionById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(auctionService.getAuction(id));
        } catch (AuctionException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AuctionObjectDTO> createAuction(
            @Valid @RequestBody AuctionObjectDTO auctionDTO,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam Long userId) {
        try {
            AuctionObjectDTO createdAuction = auctionService.createAuction(auctionDTO, userId, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAuction);
        } catch (AuctionException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AuctionObjectDTO> updateAuction(
            @PathVariable Long id,
            @Valid @RequestBody AuctionObjectDTO auctionDTO,
            @RequestParam(required = false) MultipartFile image) {
        try {
            AuctionObjectDTO updatedAuction = auctionService.updateAuction(id, auctionDTO, image);
            return ResponseEntity.ok(updatedAuction);
        } catch (AuctionException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteAuction(@PathVariable Long id) {
        try {
            auctionService.deleteAuction(id);
            return ResponseEntity.noContent().build();
        } catch (AuctionException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Page<AuctionObjectDTO>> getAuctionsByOwner(
            @PathVariable Long ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("endDate").ascending());
            return ResponseEntity.ok(auctionService.getAuctionsByOwner(ownerId, pageRequest));
        } catch (AuctionException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/bidder/{bidderId}")
    public ResponseEntity<Page<AuctionObjectDTO>> getAuctionsByCurrentBidder(
            @PathVariable Long bidderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("endDate").ascending());
            return ResponseEntity.ok(auctionService.getAuctionsByCurrentBidder(bidderId, pageRequest));
        } catch (AuctionException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}/bids")
    public ResponseEntity<Page<AuctionObjectDTO>> getAuctionsWithUserBids(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("endDate").ascending());
            return ResponseEntity.ok(auctionService.getAuctionsWithUserBids(userId, pageRequest));
        } catch (AuctionException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/bid")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AuctionObjectDTO> placeBid(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam BigDecimal amount) {
        try {
            AuctionObjectDTO updatedAuction = auctionService.placeBid(id, userId, amount);
            return ResponseEntity.ok(updatedAuction);
        } catch (AuctionException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateAuctionStatus(
            @PathVariable Long id,
            @RequestParam AuctionStatus status) {
        try {
            auctionService.updateAuctionStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (AuctionException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 