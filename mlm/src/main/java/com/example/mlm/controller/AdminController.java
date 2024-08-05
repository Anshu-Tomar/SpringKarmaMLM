package com.example.mlm.controller;

import com.example.mlm.model.User;
import com.example.mlm.service.UserService;
import com.example.mlm.service.WithdrawalRequestService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/admin")
public class AdminController {

	 private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private WithdrawalRequestService withdrawalRequestService;
    
    

    @GetMapping("/users/{userId}")
    public User getUser(@PathVariable Long userId) {
        return userService.getUserById(userId).orElse(null);
    }
    
    @PostMapping("/registerUserByAdmin")
    public User createUser(@RequestBody User user) {
    	User registerUser = userService.registerUserByAdmin(user);
    	logger.info("User Register By Admin "+registerUser);
    	return registerUser;
    }

    // Other admin management endpoints
    
    
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        List<User> inactiveUsers = userService.getInactiveUsers();
        model.addAttribute("inactiveUsers", inactiveUsers);
        return "admin_dashboard";
    }

    @PostMapping("/users/{id}/activate")
    public String activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return "redirect:/admin/dashboard";
    }
    
//    @PostMapping("/approve-commission")
//    public String approveCommission(@RequestParam Long commissionId) {
//        commissionService.approveCommission(commissionId);
//        return "redirect:/admin/commissions";
//    }

    @PostMapping("/create-withdrawal-request")
    public String createWithdrawalRequest(@RequestParam Long userId, @RequestParam Double amount) {
        withdrawalRequestService.createWithdrawalRequest(userId, amount);
        return "redirect:/user/withdrawal-requests";
    }
}
