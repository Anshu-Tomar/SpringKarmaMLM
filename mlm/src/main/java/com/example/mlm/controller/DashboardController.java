package com.example.mlm.controller;

import com.example.mlm.model.User;
import com.example.mlm.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        User admin = userService.getCurrentUser(session);

        // Ensure that the current user is an admin
        if (!"ADMIN".equals(admin.getRole())) {
            throw new RuntimeException("Access denied: Not an admin");
        }

        model.addAttribute("user", admin);
        model.addAttribute("allUsers", userService.getAllUsers());
        return "admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String showUserDashboard(HttpSession session, Model model) {
        User user = userService.getCurrentUser(session);

        // Ensure that the current user is a regular user
        if ("ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Access denied: Admins cannot access user dashboard");
        }

        model.addAttribute("user", user);
        model.addAttribute("downlines", userService.getDownlines(user));
        model.addAttribute("earnings", userService.getTotalEarnings(user));
        return "user/dashboard";
    }
}
