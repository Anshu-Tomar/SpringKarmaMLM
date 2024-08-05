package com.example.mlm.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String mobileNo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "sponsor_id")
    private User sponsor;

    @OneToMany(mappedBy = "sponsor")
    private Set<User> referrals;

    @Column
    private Double commission;
    
    // Sales data for the left leg
    @Column(name = "left_leg_sales")
    private double leftLegSales;

    // Sales data for right legs, assuming you have a list of RightLegSales
    @OneToMany(mappedBy = "user")
    private List<RightLegSales> rightLegSales;

    @Transient
    private int level;

    @Transient
    private String rank;


    @ElementCollection
    @CollectionTable(name = "user_positions", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "position")
    @Column(name = "recruit_id")
    private Map<String, Long> positions = new HashMap<>();

    private boolean active;
    private LocalDate createdDate;

    @Transient
    private Set<User> recruits = new HashSet<>();
    
    public enum Role {
        USER,
        SPONSOR,
        ADMIN
    }
    
    

    // Getters and setters

    public void addRecruit(User recruit, String position) {
        if (positions.size() >= 25) {
            throw new IllegalArgumentException("Cannot add more than 25 recruits");
        }
        if (positions.containsKey(position)) {
            throw new IllegalArgumentException("Position already occupied");
        }
        positions.put(position, recruit.getId());
        recruit.setSponsor(this);
    }

//    public User getRecruitByPosition(String position) {
//        Long recruitId = positions.get(position);
//        return recruitId != null ? userRepository.findById(recruitId).orElse(null) : null;
//    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isInactiveFor30Days() {
        return LocalDate.now().isAfter(createdDate.plusDays(30));
    }
    
   
    // Getters and setters

    public void addRecruit(User recruit) {
        recruits.add(recruit);
        recruit.setSponsor(this);
    }

    public int getLevel() {
        return calculateLevel(this, 0);
    }

    private int calculateLevel(User user, int currentLevel) {
        if (user.sponsor == null) {
            return currentLevel;
        }
        return calculateLevel(user.sponsor, currentLevel + 1);
    }

    public String getRank() {
        // Example ranking logic
        int numberOfRecruits = recruits.size();
        if(numberOfRecruits >= 300) return "Crown"; //10L
        if(numberOfRecruits >= 200) return "Diamond";//7L
        if (numberOfRecruits >= 100) return "Platinum";//5L
        if (numberOfRecruits >= 50) return "Gold";//2L
        if (numberOfRecruits >= 15) return "Silver";//1L
        return "Bronze";//1100
    }
    
   

    // Getters and setters

    public double getLeftLegSales() {
        return leftLegSales;
    }

    public void setLeftLegSales(double leftLegSales) {
        this.leftLegSales = leftLegSales;
    }

    public List<RightLegSales> getRightLegSales() {
        return rightLegSales;
    }

    public void setRightLegSales(List<RightLegSales> rightLegSales) {
        this.rightLegSales = rightLegSales;
    }
}
