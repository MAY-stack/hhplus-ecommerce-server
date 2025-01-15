package kr.hhplus.be.server.interfaces.order.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.application.order.dto.OrderDto;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "주문 생성 요청 데이터 구조")
@NoArgsConstructor
public class OrderRequest {

    @NotBlank
    @Schema(description = "주문하는 사용자 ID", example = "user123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @NotNull
    @Schema(description = "주문 상품 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OrderItem> orderItemList;

    @Schema(description = "적용할 쿠폰 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String appliedCouponId;

    // OrderRequest를 OrderDto로 변환하는 메서드
    public OrderDto toOrderDto() {
        List<kr.hhplus.be.server.application.order.dto.OrderItemDto> orderItemDtoList = orderItemList.stream()
                .map(item -> kr.hhplus.be.server.application.order.dto.OrderItemDto.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return OrderDto.builder()
                .userId(userId)
                .orderItemDtoList(orderItemDtoList)
                .couponIssuanceId(appliedCouponId)
                .build();
    }

}
