package kr.hhplus.be.server.interfaces.dto.point;

import io.swagger.v3.oas.annotations.media.Schema;
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
}
