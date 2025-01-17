package kr.hhplus.be.server.interfaces.copon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.coupon.facade.CouponFacade;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCouponStatus;
import kr.hhplus.be.server.interfaces.copon.dto.CouponInfo;
import kr.hhplus.be.server.interfaces.copon.dto.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.copon.dto.CouponIssueResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CouponControllerTest {

    @Mock
    private CouponFacade couponFacade;

    @InjectMocks
    private CouponController couponController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(couponController).build();
        objectMapper = new ObjectMapper(); // Initialize ObjectMapper
    }

    @Test
    void 발급된_쿠폰_조회_API_테스트() throws Exception {
        // Given
        String userId = "user123";
        List<CouponInfo> mockCouponList = List.of(
                new CouponInfo(UUID.randomUUID().toString(), 1L, "할인 쿠폰", 1000L, 3000L, LocalDateTime.now().plusDays(7).toString(), IssuedCouponStatus.ISSUED.toString()),
                new CouponInfo(UUID.randomUUID().toString(), 2L, "무료 배송 쿠폰", 0L, 2000L, LocalDateTime.now().plusDays(7).toString(), IssuedCouponStatus.ISSUED.toString())
        );

        when(couponFacade.getIssuedCouponList(userId)).thenReturn(mockCouponList);

        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/coupons", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockCouponList.size()))
                .andExpect(jsonPath("$[0].couponId").exists())
                .andExpect(jsonPath("$[0].couponTitle").value("할인 쿠폰"))
                .andExpect(jsonPath("$[0].discountAmount").value(1000));
    }

    @Test
    void 선착순_쿠폰_발급_API_테스트() throws Exception {
        // Given
        String userId = "user123";
        Long couponId = 1L;
        CouponIssueRequest request = new CouponIssueRequest();

        CouponIssueResponse mockResponse = new CouponIssueResponse(IssuedCouponStatus.ISSUED.name(), couponId, userId, UUID.randomUUID().toString());

        when(couponFacade.issueCoupon(eq(couponId), eq(userId))).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/users/{userId}/coupons/issuance", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // Use ObjectMapper here
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponId").exists())
                .andExpect(jsonPath("$.couponTitle").value("할인 쿠폰"))
                .andExpect(jsonPath("$.discountAmount").value(10000));
    }
}
