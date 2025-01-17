package kr.hhplus.be.server.interfaces.order.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.application.order.dto.OrderDto;
import kr.hhplus.be.server.application.order.dto.OrderItemDto;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "주문 생성 요청 데이터 구조")
public record OrderRequest(
        @NotBlank(message = "사용자 아이디는 필수입니다.")
        @Schema(description = "주문하는 사용자 ID", example = "user1", requiredMode = Schema.RequiredMode.REQUIRED)
        String userId,

        @NotNull(message = "주문 상품 리스트는 필수입니다.")
        @Valid
        @Schema(description = "주문 상품 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
        List<OrderItem> orderItemList,

        @Nullable
        @Schema(description = "적용할 쿠폰 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        String appliedCouponId
) {
    // OrderRequest를 OrderDto로 변환하는 메서드
    public OrderDto toDto() {
        List<OrderItemDto> orderItemDtoList = orderItemList.stream()
                .map(item -> kr.hhplus.be.server.application.order.dto.OrderItemDto.builder()
                        .productId(item.productId())
                        .quantity(item.quantity())
                        .build())
                .collect(Collectors.toList());

        return OrderDto.builder()
                .userId(userId)
                .orderItemDtoList(orderItemDtoList)
                .couponIssuanceId(appliedCouponId)
                .build();
    }
}
