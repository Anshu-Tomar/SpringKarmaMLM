package com.example.mlm.service;

import com.example.mlm.model.Commission;
import com.example.mlm.model.User;
import com.example.mlm.repository.CommissionRepository;
import com.example.mlm.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CommissionService {

    @Autowired
    private CommissionRepository commissionRepository;
    
    @Autowired
    private UserRepository userRepository;

    public Commission createCommission(User user, Double amount) {
        Commission commission = new Commission();
        commission.setUser(user);
        commission.setAmount(amount);
        return commissionRepository.save(commission);
    }

    public void distributeCommission(User user, Double totalAmount, int levels) {
        Set<User> levelUsers = new HashSet<>();
        distributeCommissionRecursive(user, totalAmount, levels, 1, levelUsers);
    }

    private void distributeCommissionRecursive(User user, Double totalAmount, int levels, int currentLevel, Set<User> levelUsers) {
        if (currentLevel > levels) return;

        List<User> recruits = userRepository.findBySponsor(user);
        double commissionForLevel = calculateCommissionForLevel(currentLevel, totalAmount);

        for (User recruit : recruits) {
            if (!levelUsers.contains(recruit)) {
                createCommission(recruit, commissionForLevel);
                levelUsers.add(recruit);
                distributeCommissionRecursive(recruit, totalAmount, levels, currentLevel + 1, levelUsers);
            }
        }
    }

    private double calculateCommissionForLevel(int level, double totalAmount) {
        // Example commission distribution logic
        switch (level) {
            case 1: return totalAmount * 0.10; // 10% for level 1
            case 2: return totalAmount * 0.05; // 5% for level 2
            case 3: return totalAmount * 0.03; // 3% for level 3
            case 4: return totalAmount * 0.02; // 2% for level 4
            case 5: return totalAmount * 0.01; // 1% for level 5
            default: return 0;
        }
    }
    
    public List<Commission> getPendingCommissions(Long userId) {
        return commissionRepository.findByUserIdAndApproved(userId, false);
    }

    public void approveCommission(Long commissionId) {
        Commission commission = commissionRepository.findById(commissionId).orElseThrow();
        commission.setApproved(true);
        commissionRepository.save(commission);
    }
}
