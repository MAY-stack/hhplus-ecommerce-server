package kr.hhplus.be.server.interfaces.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.point.entity.Point;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "포인트")
@Builder
public record PointResponse(
        @Schema(description = "사용자 ID", example = "user123")
        String userId,

        @Schema(description = "포인트 잔액", example = "50000")
        Long balanceAmount,

        @Schema(description = "포인트 마지막 충전/사용 일시")
        LocalDateTime updated_at
) {
    public static PointResponse from(Point point) {
        return PointResponse.builder()
                .userId(point.getUserId())
                .balanceAmount(point.getBalance())
                .updated_at(point.getUpdatedAt())
                .build();
    }
}
