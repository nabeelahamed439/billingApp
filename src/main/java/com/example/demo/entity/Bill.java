package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bill")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "bill_id", length = 8, updatable = false,unique = true)
    private String billId;

    @Column(name = "cashier_id")
    private String cashierId;

    @Column(name = "purchased_amount")
    private Double purchasedAmount;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "currency")
    private String currency;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<BillingProducts> billingProducts;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name = "customer_fk")
    private Customer customer;

    @Column(name = "billing_date", updatable = false)
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    private LocalDateTime createdAt;

}
