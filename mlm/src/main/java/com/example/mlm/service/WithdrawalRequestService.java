package com.example.mlm.service;

import com.example.mlm.model.User;
import com.example.mlm.model.WithdrawalRequest;
import com.example.mlm.repository.UserRepository;
import com.example.mlm.repository.WithdrawalRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WithdrawalRequestService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WithdrawalRequestRepository withdrawalRequestRepository;

    public void createWithdrawalRequest(Long userId, Double requestedAmount) {
        User user = userRepository.findById(userId).orElseThrow();
        Double maintenanceCharge = requestedAmount * 0.10;
        Double processedAmount = requestedAmount - maintenanceCharge;

        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setUserId(userId);
        withdrawalRequest.setRequestedAmount(requestedAmount);
        withdrawalRequest.setProcessedAmount(processedAmount);
        withdrawalRequestRepository.save(withdrawalRequest);
    }
}
