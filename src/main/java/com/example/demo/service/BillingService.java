package com.example.demo.service;

import com.example.demo.auth.AuthenticationService;
import com.example.demo.auth.RegisterRequest;
import com.example.demo.auth.TokenData;
import com.example.demo.config.Constants;
import com.example.demo.entity.*;
import com.example.demo.exception.BadRequestException;
import com.example.demo.model.ProductRequest;
import com.example.demo.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillingProductsRepository billingProductsRepository;
    private final ProductRepository productsRepository;
    private final BillRepository billRepository;
    private final AuthenticationService authenticationService;
    private final CustomerRepository customerRepository;
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @Transactional
    public BillingProducts addProducts(String productCode, Double quantity) {
        Product product = productsRepository.findByProductCode(productCode);
        if (product == null) {
            throw new BadRequestException("Product not found", "Product with code " + productCode + " does not exist");
        }
        List<BillingProducts> billingProductList = billingProductsRepository.findByCashierIdAndBillingStatus(TokenData.getUserId(),Constants.PENDING);

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

    @Transactional
    public Bill billProducts(String billId,String customerId,Double pointClaimed, RegisterRequest registerRequest){
        if (billRepository.existsByBillId(billId)){
            throw new BadRequestException("Already Done Billing","Already Done Billing");
        }
        Bill bill = new Bill();
        List<BillingProducts> billingProducts = billingProductsRepository.findByBillId(billId);
        billingProducts.forEach(billingProduct -> billingProduct.setBillingStatus(Constants.COMPLETED));
        bill.setBillingProducts(billingProducts);
        bill.setBillId(billId);
        bill.setCashierId(TokenData.getUserId());
        Double purchasedAmount = billingProducts.stream().mapToDouble(BillingProducts::getTotalPrice).sum();
        bill.setPurchasedAmount(purchasedAmount);
        bill.setTotalAmount(purchasedAmount);
        bill.setCurrency(Constants.CURRENCY_RUPEES);
        Double addedPoint = bill.getTotalAmount() * 0.01;

        if (customerId != null){
            Customer customer = customerRepository.findByCustomerId(customerId).orElseThrow(()->new BadRequestException("Customer Not Found","Invalid Customer"));
            customer.setPreviousPurchasePoint(customer.getCurrentPurchasePoint());
            if (customer.getBills() == null) {
                customer.setBills(new ArrayList<>());
            }
            customer.getBills().add(bill);
            if(pointClaimed!=null){
                if (pointClaimed <= purchasedAmount
                        && customer.getCurrentPurchasePoint()>=pointClaimed
                        && customer.getCurrentPurchasePoint() >= 100) {
                    bill.setPurchasedAmount(purchasedAmount);
                    bill.setTotalAmount(purchasedAmount-pointClaimed);
                    customer.setPreviousPurchasePoint(customer.getCurrentPurchasePoint());
                    customer.setCurrentPurchasePoint(customer.getCurrentPurchasePoint() - pointClaimed);
                }else
                    throw new BadRequestException("point can't be claimed","point can't be claimed");
            }
            customer.setAddedPurchasePoint(addedPoint);
            customer.setCurrentPurchasePoint(customer.getCurrentPurchasePoint() + addedPoint);
            bill.setCustomer(customer);
        } else if (registerRequest != null) {
            //new user registration
            User user = authenticationService.userTransform(registerRequest);
            Customer customer = objectMapper.convertValue(user, Customer.class);
            customer.setCustomerId(user.getUserId());
            customer.setLastPurchasedAt(LocalDateTime.now());
            customer.setAddedPurchasePoint(addedPoint);
            customer.setCurrentPurchasePoint(addedPoint);
            bill.setCustomer(customer);
        }

        //updating product stock
        List<ProductRequest> productRequestList = billingProducts.stream().map(billingProduct -> {
            ProductRequest request = new ProductRequest();
            request.setProductCode(billingProduct.getProductCode());
            request.setQuantity(billingProduct.getQuantity());
            request.setStockUpdateType(Constants.REMOVE_STOCK);
            return request;
        }).toList();

        List<Product> updatedProducts = productRequestList.stream().map(productService::updateProduct).toList();
        return billRepository.save(bill);
    }

    public Bill getBill(String billId) {
        return billRepository.findByBillId(billId);
    }
}
