package com.example.mlm.repository;

import com.example.mlm.model.WithdrawalRequest;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, Long> {
}
