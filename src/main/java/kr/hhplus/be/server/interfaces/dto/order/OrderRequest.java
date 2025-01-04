package kr.hhplus.be.server.interfaces.dto.order;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "주문 생성 요청 데이터 구조")
@Getter
@Setter
@NoArgsConstructor
public class OrderRequest {

    @NotBlank
    @Schema(description = "주문하는 사용자 ID", example = "user123", required = true)
    private String userId;

    @NotNull
    @Schema(description = "주문 상품 리스트", required = true)
    private List<OrderProduct> orderProductList;

    @Schema(description = "적용할 쿠폰 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String appliedCouponId;

}
