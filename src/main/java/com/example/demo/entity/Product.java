package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_code", length = 8, updatable = false,unique = true)
    private String productCode;
    
    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "category")
    private String category;

    @Column(name = "sub_category")
    private String subCategory;

    @Column(name = "available_stock")
    private Double availableStock;

    @Column(name = "unit")
    @Enumerated(EnumType.STRING)
    private Unit unit;

    @Column(name = "price")
    private Double price;

    @Column(name = "currency")
    private String currency;

    @Column(name = "merchant_id")
    private String merchantId;

    @Column(name = "last_added_date")
    private LocalDateTime LastAddedDate;

    @Column(name = "last_added_quantity")
    private Double lastAddedQuantity;

    @Column(name = "is_available")
    private Boolean isAvailable = true;


    public enum Unit {
        KG,
        PIECE
    }

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