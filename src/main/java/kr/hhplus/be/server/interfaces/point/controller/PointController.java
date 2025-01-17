package kr.hhplus.be.server.interfaces.point.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.domain.point.service.PointService;
import kr.hhplus.be.server.interfaces.point.dto.PointRechargeRequest;
import kr.hhplus.be.server.interfaces.point.dto.PointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "point", description = "포인트 API")
@Validated
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class PointController {

    private final PointFacade pointFacade;
    private final PointService pointService;

    @Operation(summary = "포인트 조회 API")
    @GetMapping("/{userId}/points")
    public ResponseEntity<PointResponse> getPoints(
            @Parameter(required = true, description = "사용자 ID", example = "user1")
            @PathVariable @NotBlank String userId) {
        PointResponse pointResponse = PointResponse.fromEntity(pointService.getPointByUserId(userId));
        return ResponseEntity.ok(pointResponse);
    }

    @Operation(summary = "포인트 충전 API")
    @PatchMapping("/{userId}/points")
    public ResponseEntity<PointResponse> rechargePoints(
            @Parameter(required = true, description = "사용자 ID", example = "user1")
            @PathVariable String userId,

            @Valid @RequestBody PointRechargeRequest request) {
        PointResponse pointResponse = PointResponse.fromEntity(pointFacade.rechargePointWithHistory(userId, request.getAmount()));
        return ResponseEntity.ok(pointResponse);
    }
}
