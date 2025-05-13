package com.myapp.controller;

import com.myapp.dto.PaymentDTO;
import com.myapp.entity.enums.PaymentStatus;
import com.myapp.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public String listPayments(Model model,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) PaymentStatus status) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (status != null) {
            List<PaymentDTO> payments = paymentService.getPaymentsByStatus(status);
            model.addAttribute("payments", payments);
        } else {
            Page<PaymentDTO> payments = paymentService.getPaymentsByUserId(getCurrentUserId(), pageRequest);
            model.addAttribute("payments", payments);
        }
        return "payment/list";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public String viewPayment(@PathVariable Long id, Model model) {
        model.addAttribute("payment", paymentService.getPaymentById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found")));
        return "payment/view";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public String showCreateForm(Model model) {
        model.addAttribute("payment", new PaymentDTO());
        return "payment/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public String createPayment(@Valid @ModelAttribute("payment") PaymentDTO paymentDTO,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "payment/create";
        }

        try {
            paymentService.createPayment(paymentDTO);
            redirectAttributes.addFlashAttribute("success", "Payment created successfully!");
            return "redirect:/payments";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/payments/create";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('USER')")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("payment", paymentService.getPaymentById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found")));
        return "payment/edit";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('USER')")
    public String updatePayment(@PathVariable Long id,
                              @Valid @ModelAttribute("payment") PaymentDTO paymentDTO,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "payment/edit";
        }

        try {
            paymentService.updatePayment(id, paymentDTO);
            redirectAttributes.addFlashAttribute("success", "Payment updated successfully!");
            return "redirect:/payments/" + id;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/payments/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('USER')")
    public String deletePayment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            paymentService.deletePayment(id);
            redirectAttributes.addFlashAttribute("success", "Payment deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/payments";
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('USER')")
    public String updatePaymentStatus(@PathVariable Long id,
                                    @RequestParam PaymentStatus status,
                                    RedirectAttributes redirectAttributes) {
        try {
            paymentService.updatePaymentStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Payment status updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/payments/" + id;
    }

    // Helper method to get current user ID (to be implemented with Spring Security)
    private Long getCurrentUserId() {
        // TODO: Implement with Spring Security
        return 1L;
    }
} 