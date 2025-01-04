package kr.hhplus.be.server.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.dto.copon.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.dto.copon.CouponIssueSuccessResponse;
import kr.hhplus.be.server.interfaces.dto.copon.CouponResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "coupon", description = "쿠폰 API")
@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    @Operation(summary = "발급된 쿠폰 조회 API", description = "사용자 ID로 발급된 쿠폰 목록을 조회")
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getIssuedCoupons(@RequestParam String userId) {
        // Mock 데이터 생성
        List<CouponResponse> coupons = new ArrayList<>();
        coupons.add(new CouponResponse(
                "550e8400-e29b-41d4-a716-446655440000",
                1,
                "1000 Discount",
                1000,
                5000,
                "2025-01-31",
                "issued"));
        coupons.add(new CouponResponse(
                "550e8400-e29b-41d4-a716-446655440001",
                2,
                "Free Shipping",
                2500,
                20000,
                "2025-02-15",
                "used"));

        return ResponseEntity.ok(coupons);
    }

    @Operation(summary = "선착순 쿠폰 발급 API", description = "사용자 ID와 쿠폰 ID를 통해 선착순으로 쿠폰을 발급합니다.")
    @PostMapping("/issue")
    public ResponseEntity<CouponIssueSuccessResponse> issueCoupon(@RequestBody CouponIssueRequest request) {
        CouponIssueSuccessResponse response = new CouponIssueSuccessResponse(
                "success",
                request.getCouponId(),
                request.getUserId(),
                "550e8400-e29b-41d4-a716-446655440000"
        );
        return ResponseEntity.ok(response);
    }

}
