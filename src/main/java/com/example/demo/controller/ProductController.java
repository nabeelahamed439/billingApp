package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.model.ProductRequest;
import com.example.demo.service.ProductService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    

    private final ProductService productService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('MERCHANT')")
    public Product createProduct(@RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    @PutMapping("update/{productCode}")
    @PreAuthorize("hasRole('MERCHANT')")
    public Product updateProduct(@PathVariable String productCode, @RequestBody ProductRequest request) {
        return productService.updateProduct(request);
    }

    @DeleteMapping("delete/{productCode}")
    @PreAuthorize("hasRole('MERCHANT')")
    public void deleteProduct(@PathVariable String productCode) {
        productService.deleteProduct(productCode);
    }

    @PostMapping("/bulk-create")
    @PreAuthorize("hasRole('MERCHANT')")
    public List<Product> createMultipleProducts(@RequestBody List<ProductRequest> requests) {
        return requests.stream()
                .map(productService::createProduct)
                .toList();
    }

    @PutMapping("/bulk-update")
    @PreAuthorize("hasRole('MERCHANT')")
    public List<Product> bulkUpdateProducts(@RequestBody List<ProductRequest> requests) {
        return requests.stream()
                .map(productService::updateProduct)
                .toList();
    }

    @DeleteMapping("/bulk-delete")
    @PreAuthorize("hasRole('MERCHANT')")
    public void bulkDeleteProducts(@RequestBody List<String> productCodes) {
        productCodes.forEach(productService::deleteProduct);
    }

    @GetMapping("product-code/{productCode}")
    public Product getProductByCode(@PathVariable String productCode) {
        return productService.getProductByCode(productCode);
    }

    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category);
    }

    @GetMapping("/sub-category/{subCategory}")
    public List<Product> getProductsBySubCategory(@PathVariable String subCategory) {
        return productService.getProductsBySubCategory(subCategory);
    }

    @GetMapping("/merchant-id/{merchantId}")
    public List<Product> getProductsByMerchantId(@PathVariable String merchantId) {
        return productService.getProductsByMerchantId(merchantId);
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String name) {
        return productService.searchProductsByName(name);
    }

    @GetMapping("/price-range")
    public List<Product> getProductsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        return productService.getProductsByPriceRange(minPrice, maxPrice);
    }

    @GetMapping("/low-stock")
    public List<Product> getLowStockProducts(@RequestParam(defaultValue = "10") Double threshold) {
        return productService.getLowStockProducts(threshold);
    }
}
