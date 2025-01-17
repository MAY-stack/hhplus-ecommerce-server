package kr.hhplus.be.server.interfaces.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.order.facade.OrderFacade;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.interfaces.product.dto.ProductResponse;
import kr.hhplus.be.server.interfaces.product.dto.TopSellingProductResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "product", description = "상품 API")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final OrderFacade orderFacade;

    @Operation(summary = "상품 목록 조회 API", description = "상품 목록을 조회할 수 있는 API")
    @GetMapping("/api/v1/products")
    public ResponseEntity<List<ProductResponse>> getProductList(
            @Parameter(required = false, description = "카테고리 ID", example = "1")
            @RequestParam(required = false) Long category,
            @ParameterObject Pageable pageable) {
        List<ProductResponse> productResponses = productService.getProductList(category, pageable).stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productResponses);
    }

    @Operation(summary = "인기 상품 정보 조회 API", description = "인기 상품 정보를 조회할 수 있는 API")
    @GetMapping("/popular")
    public ResponseEntity<List<TopSellingProductResponse>> getPopularProductList() {
        List<TopSellingProductResponse> topSellingProductResponse = orderFacade.getTopSellingProducts().stream()
                .map(TopSellingProductResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(topSellingProductResponse);
    }
}
