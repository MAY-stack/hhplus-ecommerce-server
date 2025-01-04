package kr.hhplus.be.server.interfaces.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "주문 생성 응답 데이터 구조")
public class OrderResponse {
    @Schema(description = "생성 처리 상태", example = "success")
    private String status;

    @Schema(description = "주문 ID", example = "123")
    private Long orderId;

    @Schema(description = "주문한 총 금액", example = "50000")
    private Long totalAmount;
}
