package kr.hhplus.be.server.interfaces.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.Builder;

@Builder
@Schema(description = "제품 정보 응답 데이터 구조")
public record ProductResponse(
        @Schema(description = "제품 ID", example = "1")
        Long productId,

        @Schema(description = "제품 이름", example = "Smartphone")
        String name,

        @Schema(description = "제품 가격", example = "100000")
        Long price,

        @Schema(description = "재고 수량", example = "50")
        Integer stock
) {
    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

}
