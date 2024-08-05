package com.example.mlm.controller;

import com.example.mlm.model.User;
import com.example.mlm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping
    public String registerUser(User user) {
    	Long sponsorId = user.getSponsor().getId();
//        User sponsor = user.getSponsor() != null ? userService.findById(user.getSponsor().getId()) : null;

        // Ensure that only admins can create users without a sponsor
        if (sponsorId == null || sponsorId==0l){
            throw new RuntimeException("Sponsor ID is required for non-admin users");
        }
        User sponsor = userService.getUserById(sponsorId).get();
        user.setSponsor(sponsor);
        userService.registerUser(user,sponsorId);
        return "redirect:/login";
    }
    
    
    @PostMapping("/users/register")
    public String registerUser(@RequestParam String name,
                               @RequestParam(required = false) String sponsorName,
                               @RequestParam(required = false) String position,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(name, sponsorName, position);
            redirectAttributes.addFlashAttribute("message", "Registration successful!");
            return "redirect:/register";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}
