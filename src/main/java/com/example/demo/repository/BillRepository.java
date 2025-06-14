package com.example.demo.repository;

import com.example.demo.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository <Bill,Long>{
    Bill findByBillId(String billId);
    boolean existsByBillId(String billId);
}
