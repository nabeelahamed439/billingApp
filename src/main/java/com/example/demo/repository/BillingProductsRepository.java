package com.example.demo.repository;

import com.example.demo.entity.BillingProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingProductsRepository extends JpaRepository<BillingProducts,Long> {
    boolean existsByBillId(String billId);
    List<BillingProducts> findByCashierIdAndBillingStatus(String cashierId, String status);
    BillingProducts findByProductCodeAndCashierIdAndBillingStatus(String productCode, String cashierId, String status);
    List<BillingProducts> findByBillId(String billId);
}
