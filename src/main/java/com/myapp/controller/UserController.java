package com.myapp.controller;

import com.myapp.dto.UserDTO;
import com.myapp.entity.User;
import com.myapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("username").ascending());
        Page<UserDTO> users = userService.getAllUsers(pageRequest);
        model.addAttribute("users", users);
        return "user/list";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public String viewUser(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found")));
        return "user/view";
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String showProfile(Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByUsername(currentUsername);
        model.addAttribute("user", userService.convertToDTO(currentUser));
        return "user/profile";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found")));
        return "user/edit";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("user") UserDTO userDTO,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user/edit";
        }

        try {
            userService.updateUser(id, userDTO);
            redirectAttributes.addFlashAttribute("success", "User updated successfully!");
            return "redirect:/users/" + id;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserDTO userDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.createUser(userDTO);
            redirectAttributes.addFlashAttribute("message", "Registration successful! Please login.");
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Registration failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/users/register";
        }
    }
}
