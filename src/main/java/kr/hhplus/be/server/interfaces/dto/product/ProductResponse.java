package kr.hhplus.be.server.interfaces.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "제품 정보 응답 데이터 구조")
@Getter
@Setter
@AllArgsConstructor
public class ProductResponse {

    @Schema(description = "제품 카테고리", example = "electronics")
    private String category;

    @Schema(description = "제품 ID", example = "1")
    private Long id;

    @Schema(description = "제품 이름", example = "Smartphone")
    private String name;

    @Schema(description = "옵션 ID", example = "1001")
    private Integer option_id;

    @Schema(description = "옵션 이름", example = "128GB Black")
    private String option_name;

    @Schema(description = "제품 가격", example = "100000")
    private Long price;

    @Schema(description = "재고 수량", example = "50")
    private Integer stock;

}
