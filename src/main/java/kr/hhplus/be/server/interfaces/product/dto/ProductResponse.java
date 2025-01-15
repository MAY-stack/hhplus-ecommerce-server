package kr.hhplus.be.server.interfaces.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "제품 정보 응답 데이터 구조")
@Getter
@Setter
@AllArgsConstructor
public class ProductResponse {
    @Schema(description = "제품 ID", example = "1")
    private Long id;

    @Schema(description = "제품 이름", example = "Smartphone")
    private String name;

    @Schema(description = "제품 가격", example = "100000")
    private Long price;

    @Schema(description = "재고 수량", example = "50")
    private Integer stock;

    public static ProductResponse fromEntity(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock()
        );
    }

}
