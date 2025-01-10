package kr.hhplus.be.server.interfaces.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.point.entity.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "포인트")
@Getter
@Setter
@AllArgsConstructor
public class PointResponse {
    @Schema(description = "사용자 ID", example = "user123")
    private String userId;

    @Schema(description = "포인트 잔액", example = "50000")
    private Long balanceAmount;

    @Schema(description = "포인트 마지막 충전/사용 일시")
    private LocalDateTime updated_at;

    public static PointResponse fromEntity(Point point) {
        return new PointResponse(
                point.getUserId(),
                point.getBalance(),
                point.getUpdatedAt()
        );
    }
}
