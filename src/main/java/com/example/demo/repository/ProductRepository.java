package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.Product;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByProductCode(String productCode);
    List<Product> findByCategory(String mainCategory);
    List<Product> findByMerchantId(String merchantId);
    List<Product> findBySubCategory(String subCategory);
    List<Product> findByProductNameContainingIgnoreCase(String name);
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    List<Product> findByAvailableStockLessThanEqual(Double threshold);

    boolean existsByProductCode(String productCode);

    void deleteByProductCode(String productCode);
} 