package com.example.mlm.controller;

import com.example.mlm.model.User;
import com.example.mlm.service.UserService;
import com.example.mlm.service.WithdrawalRequestService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private WithdrawalRequestService withdrawalRequestService;
    
    @GetMapping("/user_dashboard")
    public String user_dashboard() {
        return "user_dashboard";
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody User user, @RequestParam Long sponsorId) {
        logger.debug("Request to register user: {} with sponsor ID: {}", user.getUsername(), sponsorId);
        return userService.registerUser(user, sponsorId);
    }

    @GetMapping("/{username}")
    public User getUser(@PathVariable String username) {
        logger.debug("Fetching user by username: {}", username);
        return userService.getUserByUsername(username);
    }

    @GetMapping("/{userId}/referrals")
    public Set<User> getUserReferrals(@PathVariable Long userId) {
        logger.debug("Fetching referrals for user ID: {}", userId);
        return userService.getUserReferrals(userId);
    }

    @GetMapping("/{userId}/commission")
    public Double getUserCommission(@PathVariable Long userId) {
        logger.debug("Fetching commission for user ID: {}", userId);
        User user = userService.getUserById(userId).orElse(null);
        return user != null ? userService.calculateCommission(user) : 0.0;
    }
    
    @GetMapping("/user/dashboard/{id}")
    public String userDashboard(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id).get();
        if (user == null) {
            return "error"; // Handle error accordingly
        }

        // Calculate commissions
        var commissions = userService.calculateCommissions(user);

        model.addAttribute("user", user);
        model.addAttribute("commissions", commissions);
        return "user_dashboard";
    }


//    @GetMapping("/user/withdrawal-requests")
//    public String viewWithdrawalRequests(@RequestParam Long userId, Model model) {
//        User user = userService.getUserById(userId).orElseThrow();
//        Double totalEarnings = user.getCommission();
//        Double totalWithdrawn = withdrawalRequestService.calculateTotalWithdrawn(userId);
//        Double remainingAmount = totalEarnings - totalWithdrawn;
//
//        model.addAttribute("totalEarnings", totalEarnings);
//        model.addAttribute("totalWithdrawn", totalWithdrawn);
//        model.addAttribute("remainingAmount", remainingAmount);
//
//        return "withdrawal-requests";
//    }
}
