package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @JsonProperty("product_code")
    private String productCode;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("category")
    private String category;

    @JsonProperty("sub_category")
    private String subCategory;

    @JsonProperty("available_stock")
    private Double availableStock;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("merchant_id")
    private String merchantId;

    @JsonProperty("is_available")
    private Boolean isAvailable;

    @JsonProperty("quantity")
    private Double quantity;
}