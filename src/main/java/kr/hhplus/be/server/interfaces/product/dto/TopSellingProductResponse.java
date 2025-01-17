package kr.hhplus.be.server.interfaces.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.order.dto.ProductSalesDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "판매 상위 상품 응답 데이터 구조")
@Getter
@Setter
@AllArgsConstructor
public class TopSellingProductResponse {
    @Schema(description = "제품 ID", example = "1")
    private Long productId;

    @Schema(description = "제품 이름", example = "Smartphone")
    private String name;

    @Schema(description = "제품 가격", example = "100000")
    private Long price;

    @Schema(description = "재고 수량", example = "50")
    private Integer stock;

    @Schema(description = "최근 3일간의 판매 수량", example = "50")
    private Integer recentSales;

    public static TopSellingProductResponse fromProductSalesDto(ProductSalesDto productSalesDto) {
        return new TopSellingProductResponse(
                productSalesDto.productId(),
                productSalesDto.productName(),
                productSalesDto.price(),
                productSalesDto.stock(),
                productSalesDto.soldQuantity()
        );
    }
}
