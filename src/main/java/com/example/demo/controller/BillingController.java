package com.example.demo.controller;

import com.example.demo.auth.RegisterRequest;
import com.example.demo.entity.Bill;
import com.example.demo.entity.BillingProducts;
import com.example.demo.model.ProductRequest;
import com.example.demo.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/add")
    public BillingProducts addProducts(@RequestParam String productCode, @RequestParam Double quantity) {
        return billingService.addProducts(productCode,quantity);
    }

    @PostMapping("/bill/{billId}")
    public Bill billProducts(@PathVariable String billId,
                             @RequestParam(required = false) String customerId ,
                             @RequestParam (required = false) Double pointClaimed,
                             @RequestBody(required = false) RegisterRequest registerRequest) {
        return billingService.billProducts(billId,customerId,pointClaimed,registerRequest);
    }

    @GetMapping("get-bill/{billId}")
    public Bill getBill(@PathVariable String billId){
        return billingService.getBill(billId);
    }

}
