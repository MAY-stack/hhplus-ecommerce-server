package kr.hhplus.be.server.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.dto.product.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "product", description = "상품 API")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Operation(summary = "상품 정보 조회 API", description = "상품 정보를 조회할 수 있는 API")
    @GetMapping("/api/v1/products")
    public ResponseEntity<List<ProductResponse>> getProductList(@RequestParam(required = false) String category) {
        // Mock 데이터 생성
        List<ProductResponse> products = new ArrayList<>();
        products.add(new ProductResponse("electronics", 1L, "Smartphone", 1001, "128GB Black", 100000L, 50));
        products.add(new ProductResponse("electronics", 2L, "Laptop", 2001, "16GB RAM", 150000L, 30));
        products.add(new ProductResponse("home-appliances", 3L, "Air Conditioner", 3001, "1.5 Ton", 50000L, 20));

        return ResponseEntity.ok(products);
    }

    @Operation(summary = "인기 상품 정보 조회 API", description = "인기 상품 정보를 조회할 수 있는 API")
    @GetMapping("/api/v1/products/popular")
    public ResponseEntity<List<ProductResponse>> getPopularProductList(
            @Parameter(description = "제품 카테고리") @RequestParam(required = false) String category) {
        // Mock 데이터 생성
        List<ProductResponse> popularProducts = new ArrayList<>();
        popularProducts.add(new ProductResponse("electronics", 1L, "Smartphone", 1001, "128GB Black", 100000L, 150));
        popularProducts.add(new ProductResponse("electronics", 2L, "Laptop", 2001, "16GB RAM", 150000L, 80));
        popularProducts.add(new ProductResponse("home-appliances", 3L, "Air Conditioner", 3001, "1.5 Ton", 50000L, 60));
        popularProducts.add(new ProductResponse("home-appliances", 4L, "Vacuum Cleaner", 4001, "Cordless", 30000L, 90));
        
        return ResponseEntity.ok(popularProducts);
    }
}
