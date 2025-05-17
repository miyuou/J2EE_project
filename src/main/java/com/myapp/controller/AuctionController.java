package com.myapp.controller;

import com.myapp.dto.AuctionObjectDTO;
import com.myapp.entity.User;
import com.myapp.entity.enums.AuctionStatus;
import com.myapp.exception.AuctionException;
import com.myapp.service.AuctionService;
import com.myapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final UserService userService;

    @Autowired
    public AuctionController(AuctionService auctionService, UserService userService) {
        this.auctionService = auctionService;
        this.userService = userService;
    }


    @GetMapping
    public String listAuctions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "endDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<AuctionObjectDTO> auctions = auctionService.getAllAuctions(pageRequest);
        model.addAttribute("auctions", auctions);
        return "auctions/list";
    }

    @GetMapping("/active")
    public String listActiveAuctions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "currentPrice") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<AuctionObjectDTO> auctions = auctionService.getAuctionsByStatus(AuctionStatus.ACTIVE, pageRequest);
        model.addAttribute("auctions", auctions);
        return "auctions/active";
    }

    @GetMapping("/my")
    public String listMyAuctions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<AuctionObjectDTO> auctions = auctionService.getAuctionsByOwner(user.getId(), pageRequest);
        model.addAttribute("auctions", auctions);
        return "auctions/my";
    }

    @GetMapping("/bids")
    public String listMyBids(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<AuctionObjectDTO> auctions = auctionService.getAuctionsWithUserBids(user.getId(), pageRequest);
        model.addAttribute("auctions", auctions);
        return "auctions/bids";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("auction", new AuctionObjectDTO());
        return "auctions/create";
    }

    @PostMapping("/create")
    public String createAuction(
            @Valid @ModelAttribute("auction") AuctionObjectDTO auctionDTO,
            BindingResult result,
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auctions/create";
        }

        try {
            User user = userService.findByUsername(userDetails.getUsername());
            auctionService.createAuction(auctionDTO, user.getId(), image);
            redirectAttributes.addFlashAttribute("success", "Auction created successfully");
            return "redirect:/auctions/my";
        } catch (AuctionException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auctions/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            AuctionObjectDTO auction = auctionService.getAuction(id);
            model.addAttribute("auction", auction);
            return "auctions/edit";
        } catch (AuctionException e) {
            return "redirect:/auctions/my";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateAuction(
            @PathVariable Long id,
            @Valid @ModelAttribute("auction") AuctionObjectDTO auctionDTO,
            BindingResult result,
            @RequestParam("image") MultipartFile image,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auctions/edit";
        }

        try {
            auctionService.updateAuction(id, auctionDTO, image);
            redirectAttributes.addFlashAttribute("success", "Auction updated successfully");
            return "redirect:/auctions/my";
        } catch (AuctionException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auctions/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteAuction(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            auctionService.deleteAuction(id);
            redirectAttributes.addFlashAttribute("success", "Auction deleted successfully");
        } catch (AuctionException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/auctions/my";
    }

    @GetMapping("/{id}")
    public String viewAuction(@PathVariable Long id, Model model) {
        try {
            AuctionObjectDTO auction = auctionService.getAuction(id);
            model.addAttribute("auction", auction);
            return "auctions/view";
        } catch (AuctionException e) {
            return "redirect:/auctions";
        }
    }

    @PostMapping("/{id}/bid")
    public String placeBid(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            auctionService.placeBid(id, user.getId(), amount);
            redirectAttributes.addFlashAttribute("success", "Bid placed successfully");
        } catch (AuctionException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/auctions/" + id;
    }
} 