package com.example.mlm.repository;

import com.example.mlm.model.Commission;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommissionRepository extends JpaRepository<Commission, Long> {
	List<Commission> findByUserIdAndApproved(Long userId, Boolean approved);
}
