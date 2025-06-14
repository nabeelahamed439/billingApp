package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "billing_products")
public class BillingProducts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "bill_id", length = 8, updatable = false)
    private String billId;

    @Column(name ="product_code")
    private String productCode;

    @Column(name ="product_name")
    private String productName;

    @Column(name ="quantity")
    private Double quantity;

    @Column(name = "each_price")
    private Double eachPrice;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "cashier_id")
    private String cashierId;

    @Column(name = "billing_status")
    private String billingStatus;

}
