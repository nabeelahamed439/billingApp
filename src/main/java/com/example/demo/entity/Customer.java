package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customer")
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //extra
    @Column(name = "customer_id", nullable = false, unique = true)
    private String customerId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", nullable = false)
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
    private String email;

    @Column(name = "phone_number")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be between 10-15 digits")
    private String phoneNumber;

    @Column(name = "creator_role")
    private String creatorRole;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_purchased_at")
    private LocalDateTime lastPurchasedAt;

    @Column(name = "previous_purchase_point")
    private Double previousPurchasePoint;

    @Column(name = "added_purchase_point")
    private Double addedPurchasePoint;

    @Column(name = "current_purchase_point")
    private Double currentPurchasePoint;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "customer",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Bill> bills;

    @Column(name = "created_at", updatable = false)
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    private LocalDateTime updatedAt;
}
