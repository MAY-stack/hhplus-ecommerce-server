package kr.hhplus.be.server.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.dto.point.PointRechargeRequest;
import kr.hhplus.be.server.interfaces.dto.point.PointResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "point", description = "포인트 API")
@RestController
@RequestMapping("/api/v1/points")
public class PointController {

    @Operation(summary = "포인트 조회 API")
    @GetMapping
    public ResponseEntity<PointResponse> getPoints(@RequestParam String userId) {
        PointResponse pointResponse = new PointResponse(userId, 40000L, LocalDateTime.of(2023, 12, 25, 15, 30, 45));
        return ResponseEntity.ok(pointResponse);
    }

    @Operation(summary = "포인트 충전 API")
    @PatchMapping("/recharge")
    public ResponseEntity<PointResponse> rechargePoints(@RequestBody PointRechargeRequest request) {
        PointResponse pointResponse = new PointResponse(request.getUserId(), 40000 + request.getAmount(), LocalDateTime.now());
        return ResponseEntity.ok(pointResponse);
    }
}
