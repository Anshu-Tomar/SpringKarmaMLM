package com.example.mlm.service;

import com.example.mlm.model.User;
import com.example.mlm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchedulerService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void deactivateInactiveUsers() {
        List<User> inactiveUsers = userRepository.findByActiveFalse();
        for (User user : inactiveUsers) {
            if (user.isInactiveFor30Days()) {
                userService.deactivateUser(user.getId());
                // Handle the reassignment of their recruits
                reassignRecruits(user);
            }
        }
    }

    private void reassignRecruits(User inactiveUser) {
        // Implement logic to reassign recruits
    }
}
