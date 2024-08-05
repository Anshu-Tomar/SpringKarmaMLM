package com.example.mlm.repository;

import com.example.mlm.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

	User findByMobileNo(String mobileNo);

	List<User> findBySponsor(User user);

	List<User> findByActiveFalse();
}
