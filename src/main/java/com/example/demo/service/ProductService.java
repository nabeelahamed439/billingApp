package com.example.demo.service;

import com.example.demo.auth.TokenData;
import com.example.demo.config.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.demo.repository.ProductRepository;
import com.example.demo.model.ProductRequest;
import com.example.demo.entity.Product;
import com.example.demo.entity.Product.Unit;
import com.example.demo.exception.BadRequestException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(ProductRequest request) {
        Product product = new Product();
        product.setProductCode(String.format("PR%06d", (int)(Math.random() * 1000000)));
        product.setProductName(request.getProductName());
        product.setCategory(request.getCategory());
        product.setSubCategory(request.getSubCategory());
        product.setAvailableStock(request.getAvailableStock());
        try {
            product.setUnit(Unit.valueOf(request.getUnit().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid unit." ,"Allowed values are: " + Arrays.toString(Unit.values()));
        }
        product.setPrice(request.getPrice());
        product.setCurrency(Constants.CURRENCY_RUPEES);
        product.setMerchantId(TokenData.getUserId());
        product.setLastAddedDate(LocalDateTime.now());
        product.setLastAddedQuantity(request.getQuantity());
        product.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);
        
        return productRepository.save(product);
    }

    public Product getProductByCode(String productCode) {
        return productRepository.findByProductCode(productCode);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByMerchantId(String merchantId) {
        return productRepository.findByMerchantId(merchantId);
    }

    public List<Product> getProductsByCategory(String Category) {
        return productRepository.findByCategory(Category);
    }

    public List<Product> getProductsBySubCategory(String subCategory) {
        return productRepository.findBySubCategory(subCategory);
    }

    public Product updateProduct(String productCode, ProductRequest request) {
        Product product = getProductByCode(productCode);
        if (request.getProductName() != null) product.setProductName(request.getProductName());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getSubCategory() != null) product.setSubCategory(request.getSubCategory());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getQuantity() != null){
            product.setLastAddedQuantity(request.getQuantity());
            product.setLastAddedDate(LocalDateTime.now());
            product.setAvailableStock(product.getAvailableStock()+request.getQuantity());
        }
        if (request.getIsAvailable() != null) product.setIsAvailable(request.getIsAvailable());
        return productRepository.save(product);
    }

    public void deleteProduct(String productCode) {
        if (!productRepository.existsByProductCode(productCode)) {
            throw new BadRequestException("Product not found","No product with code: " + productCode);
        }
        productRepository.deleteByProductCode(productCode);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByProductNameContainingIgnoreCase(name);
    }

    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public List<Product> getLowStockProducts(Double threshold) {
        return productRepository.findByAvailableStockLessThanEqual(threshold);
    }
} 