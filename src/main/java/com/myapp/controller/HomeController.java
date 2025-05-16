package com.myapp.controller;

import com.myapp.dto.UserDTO;
import com.myapp.service.AuctionService;
import com.myapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final UserService userService;
    private final AuctionService auctionService;

    @Autowired
    public HomeController(UserService userService, AuctionService auctionService) {
        this.userService = userService;
        this.auctionService = auctionService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - Auction System");
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDTO());
        System.out.println(">>> DAKHLI L PAGE REGISTER <<<");
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
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            result.rejectValue(null, null, e.getMessage());
            return "register"; // mashi redirection
        }

    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activeAuctions", auctionService.getActiveAuctions());
        return "dashboard";
    }


} 