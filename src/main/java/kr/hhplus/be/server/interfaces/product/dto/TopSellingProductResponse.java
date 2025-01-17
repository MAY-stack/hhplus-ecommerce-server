package kr.hhplus.be.server.interfaces.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.order.dto.ProductSalesDto;
import lombok.Builder;

@Builder
@Schema(description = "판매 상위 상품 응답 데이터 구조")
public record TopSellingProductResponse(
        @Schema(description = "제품 ID", example = "1")
        Long productId,

        @Schema(description = "제품 이름", example = "Smartphone")
        String name,

        @Schema(description = "제품 가격", example = "100000")
        Long price,

        @Schema(description = "재고 수량", example = "50")
        Integer stock,

        @Schema(description = "최근 3일간의 판매 수량", example = "50")
        Integer recentSales
) {
    public static TopSellingProductResponse from(ProductSalesDto productSalesDto) {
        return TopSellingProductResponse.builder()
                .productId(productSalesDto.productId())
                .name(productSalesDto.productName())
                .price(productSalesDto.price())
                .stock(productSalesDto.stock())
                .recentSales(productSalesDto.soldQuantity())
                .build();
    }
}
