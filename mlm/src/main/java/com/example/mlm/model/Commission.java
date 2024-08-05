package com.example.mlm.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Commission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Double amount;
    
    private Boolean approved;

    // Getters and setters
}
