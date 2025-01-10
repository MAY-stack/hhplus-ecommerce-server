package kr.hhplus.be.server.interfaces.point.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.domain.point.service.PointService;
import kr.hhplus.be.server.interfaces.point.dto.PointRechargeRequest;
import kr.hhplus.be.server.interfaces.point.dto.PointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "point", description = "포인트 API")
@RestController
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
public class PointController {

    private final PointFacade pointFacade;
    private final PointService pointService;

    @Operation(summary = "포인트 조회 API")
    @GetMapping
    public ResponseEntity<PointResponse> getPoints(@RequestParam String userId) {
        PointResponse pointResponse = PointResponse.fromEntity(pointService.getPointByUserId(userId));
        return ResponseEntity.ok(pointResponse);
    }

    @Operation(summary = "포인트 충전 API")
    @PatchMapping("/recharge")
    public ResponseEntity<PointResponse> rechargePoints(@RequestBody PointRechargeRequest request) {
        PointResponse pointResponse =
                PointResponse
                        .fromEntity(pointFacade.rechargePointWithHistory(request.getUserId(), request.getAmount()));
        return ResponseEntity.ok(pointResponse);
    }
}
