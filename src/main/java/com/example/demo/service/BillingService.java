package com.example.demo.service;

import com.example.demo.auth.TokenData;
import com.example.demo.config.Constants;
import com.example.demo.entity.Bill;
import com.example.demo.entity.BillingProducts;
import com.example.demo.entity.Product;
import com.example.demo.exception.BadRequestException;
import com.example.demo.model.ProductRequest;
import com.example.demo.repository.BillRepository;
import com.example.demo.repository.BillingProductsRepository;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillingProductsRepository billingProductsRepository;
    private final ProductRepository productsRepository;
    private final BillRepository billRepository;

    @Transactional
    public BillingProducts addProducts(String productCode, Double quantity) {

        Product product = productsRepository.findByProductCode(productCode);
        if (product == null) {
            throw new BadRequestException("Product not found", "Product with code " + productCode + " does not exist");
        }

        List<BillingProducts> billingProductList = billingProductsRepository.findByCashierIdAndBillingStatus(
            TokenData.getUserId(),
            Constants.PENDING);

        Map<String,BillingProducts> map = billingProductList.stream().collect(Collectors.toMap(BillingProducts::getProductCode, Function.identity()));
        BillingProducts billingProduct = map.get(productCode);

        String generatedBillId= billingProductList.stream().map(BillingProducts::getBillId).filter(Objects::nonNull).findFirst().orElse(null);

        if (generatedBillId==null){
            do {
                generatedBillId = String.format("BL%06d", (int)(Math.random() * 1_000_000));
            } while (billingProductsRepository.existsByBillId(generatedBillId));
        }



        if (billingProduct != null) {
            billingProduct.setQuantity(billingProduct.getQuantity() + quantity);
            billingProduct.setTotalPrice(product.getPrice() * billingProduct.getQuantity());
        } else {
            billingProduct = new BillingProducts();
            billingProduct.setBillId(generatedBillId);
            billingProduct.setProductCode(product.getProductCode());
            billingProduct.setProductName(product.getProductName());
            billingProduct.setQuantity(quantity);
            billingProduct.setEachPrice(product.getPrice());
            billingProduct.setTotalPrice(product.getPrice() * quantity);
            billingProduct.setCashierId(TokenData.getUserId());
            billingProduct.setBillingStatus(Constants.PENDING);
        }

        if (product.getAvailableStock() < billingProduct.getQuantity()) {
            throw new BadRequestException("Product Out of Stock", "Product Out of Stock");
        }
        
        return billingProductsRepository.save(billingProduct);
    }

    public Bill billProducts(String billId){
        if (billRepository.existsByBillId(billId)){
            throw new BadRequestException("Already Done Billing","Already Done Billing");
        }
        Bill bill = new Bill();
        List<BillingProducts> billingProducts = billingProductsRepository.findByBillId(billId);
        billingProducts.forEach(billingProduct -> billingProduct.setBillingStatus(Constants.COMPLETED));
        bill.setBillingProducts(billingProducts);
        bill.setBillId(billId);
        bill.setCashierId(TokenData.getUserId());
        bill.setTotalAmount(billingProducts.stream().mapToDouble(BillingProducts::getTotalPrice).sum());
        bill.setCurrency(Constants.CURRENCY_RUPEES);
        return billRepository.save(bill);
    }

    public Bill getBill(String billId) {
        return billRepository.findByBillId(billId);
    }
}
