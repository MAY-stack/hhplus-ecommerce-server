package kr.hhplus.be.server.interfaces.copon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import kr.hhplus.be.server.application.coupon.facade.CouponFacade;
import kr.hhplus.be.server.interfaces.copon.dto.CouponInfo;
import kr.hhplus.be.server.interfaces.copon.dto.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.copon.dto.CouponIssueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "coupon", description = "쿠폰 API")
@Validated
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class CouponController {

    private final CouponFacade couponFacade;

    @Operation(summary = "발급된 쿠폰 조회 API", description = "사용자 ID로 발급된 쿠폰 목록을 조회")
    @GetMapping("/{userId}/coupons")
    public ResponseEntity<List<CouponInfo>> getIssuedCoupons(@PathVariable @NotBlank String userId) {
        List<CouponInfo> couponInfoList = couponFacade.getIssuedCouponList(userId).stream()
                .map(CouponInfo::fromCouponIssuanceInfoDto)
                .toList();
        return ResponseEntity.ok(couponInfoList);
    }

    @Operation(summary = "선착순 쿠폰 발급 API", description = "사용자 ID와 쿠폰 ID를 통해 선착순으로 쿠폰을 발급")
    @PostMapping("/{userId}/coupons/issuance")
    public ResponseEntity<CouponIssueResponse> issueCoupon(@PathVariable @NotBlank String userId,
                                                           @Valid @RequestBody CouponIssueRequest request) {
        CouponIssueResponse couponIssueResponse =
                CouponIssueResponse.fromCouponIssuanceInfoDto(couponFacade.issueCoupon(request.getCouponId(), userId));
        return ResponseEntity.ok(couponIssueResponse);
    }
}
