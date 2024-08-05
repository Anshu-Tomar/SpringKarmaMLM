package com.example.mlm.service;

import com.example.mlm.model.RightLegSales;
import com.example.mlm.model.User;
import com.example.mlm.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user, Long sponsorId) {
        logger.debug("Registering user: {}", user.getUsername());
        User sponsor = userRepository.findById(sponsorId).orElse(null);
        if (sponsor != null) {
            user.setSponsor(sponsor);
            sponsor.getReferrals().add(user);
        }
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getUsername());
        return savedUser;
    }
    
    public User registerUserByAdmin(User user) {
        logger.debug("Registering user By Admin: {}", user.getUsername());
//        User sponsor = userRepository.findById(user.getId()).orElse(null);
        
        User sponsor = userRepository.findByMobileNo(user.getMobileNo());
        if (sponsor != null) {
//            user.setSponsor(sponsor);
//            sponsor.getReferrals().add(user);
        	return sponsor;
        }
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getUsername());
        return savedUser;
    }

    public User getUserByUsername(String username) {
        logger.debug("Fetching user by username: {}", username);
        return userRepository.findByUsername(username);
    }
    
    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    
    public User getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            throw new RuntimeException("No user is currently logged in");
        }
        return user;
    }

    public Optional<User> getUserById(Long id) {
        logger.debug("Fetching user by ID: {}", id);
        return userRepository.findById(id);
    }

    public Set<User> getUserReferrals(Long userId) {
        logger.debug("Fetching referrals for user ID: {}", userId);
        return userRepository.findById(userId).map(User::getReferrals).orElse(new HashSet<>());
    }

    public Double calculateCommission(User user) {
        logger.debug("Calculating commission for user: {}", user.getUsername());
        double commissionRate = 0.05; // 5% commission rate
        double totalCommission = 0.0;
        
        for (User referral : user.getReferrals()) {
            totalCommission += referral.getCommission() * commissionRate;
        }
        
        logger.info("Total commission calculated for user {}: {}", user.getUsername(), totalCommission);
        return totalCommission;
    }

	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
		List<User> list = userRepository.findAll();
		return list;
	}
	
	
	  public Set<User> getDownlines(User user) {
	        return user.getReferrals();
	    }

	    // Get total earnings of a user
	    public Double getTotalEarnings(User user) {
	        return user.getCommission();
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    public User createUser(String name, String sponsorName, String position) {
	        User sponsor = userRepository.findByUsername(sponsorName);
	        if (sponsor == null) {
	            throw new RuntimeException("Sponsor not found");
	        }

	        User user = new User();
	        user.setUsername(name);
	        user.setCreatedDate(LocalDate.now());
	        user.setActive(false); // New user is inactive by default
	        userRepository.save(user);

	        if (position != null && !position.isEmpty()) {
	            if (!position.matches("leg\\d+")) {
	                throw new IllegalArgumentException("Invalid position");
	            }
	            sponsor.addRecruit(user, position);
	            userRepository.save(sponsor);
	        }

	        return user;
	    }

	    public void activateUser(Long userId) {
	        User user = userRepository.findById(userId).orElse(null);
	        if (user != null) {
	            user.activate();
	            userRepository.save(user);
	        }
	    }

	    public void deactivateUser(Long userId) {
	        User user = userRepository.findById(userId).orElse(null);
	        if (user != null) {
	            user.deactivate();
	            userRepository.save(user);
	        }
	    }

	    public List<User> getInactiveUsers() {
	        return userRepository.findByActiveFalse();
	    }

	    
	    
	    public User getUserWithDetails(String name) {
	        User user = userRepository.findByUsername(name);
	        if (user != null) {
	            user.getLevel(); // Trigger level calculation
	            user.getRank(); // Trigger rank calculation
	        }
	        return user;
	    }
	    
	    
	    public Map<String, Double> calculateCommissions(User user) {
	        // Ensure user object is not null
	        if (user == null) {
	            throw new IllegalArgumentException("User cannot be null");
	        }

	        // Retrieve left leg sales and right leg sales
	        double leftLegSales = user.getLeftLegSales();
	        List<RightLegSales> rightLegSalesList = user.getRightLegSales();

	        // Compute total sales of all right legs
	        double totalRightLegSales = rightLegSalesList.stream()
	            .mapToDouble(RightLegSales::getSalesAmount)
	            .sum();

	        // Find the minimum sales among the right legs
	        OptionalDouble minRightLegSalesOpt = rightLegSalesList.stream()
	            .mapToDouble(RightLegSales::getSalesAmount)
	            .min();
	        double minRightLegSales = minRightLegSalesOpt.orElse(0);

	        // Calculate commission based on the weaker leg (left leg or the smallest right leg sales)
	        double commissionableSales = Math.min(leftLegSales, minRightLegSales);

	        // Commission rate (10% for simplicity)
	        double commissionRate = 0.10;
	        double commission = commissionableSales * commissionRate;

	        // Use a HashMap instead of Map.of for flexibility
	        Map<String, Double> result = new HashMap<>();
	        result.put("leftLegSales", leftLegSales);
	        result.put("totalRightLegSales", totalRightLegSales);
	        result.put("minRightLegSales", minRightLegSales);
	        result.put("commission", commission);

	        return result;
	    }
}
