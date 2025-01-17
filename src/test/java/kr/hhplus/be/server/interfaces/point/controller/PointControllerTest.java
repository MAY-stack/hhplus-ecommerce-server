package kr.hhplus.be.server.interfaces.point.controller;

import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.service.PointService;
import kr.hhplus.be.server.interfaces.point.dto.PointRechargeRequest;
import kr.hhplus.be.server.interfaces.point.dto.PointResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PointControllerTest {

    @Mock
    private PointFacade pointFacade;

    @Mock
    private PointService pointService;

    @InjectMocks
    private PointController pointController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(pointController).build();
    }

    @Test
    void 포인트_조회_API_테스트() throws Exception {
        // Given
        String userId = "user123";
        Point mockPoint = new Point(userId, 50000L);
        PointResponse mockResponse = PointResponse.fromEntity(mockPoint);

        when(pointService.getPointByUserId(userId)).thenReturn(mockPoint);

        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/points", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.balanceAmount").value(50000));
    }

    @Test
    void 포인트_충전_API_테스트() throws Exception {
        // Given
        String userId = "user123";
        Long rechargeAmount = 30000L;
        PointRechargeRequest request = new PointRechargeRequest();
        request.setAmount(rechargeAmount);

        Point mockPoint = new Point(userId, 80000L);
        PointResponse mockResponse = PointResponse.fromEntity(mockPoint);

        when(pointFacade.rechargePointWithHistory(eq(userId), eq(rechargeAmount))).thenReturn(mockPoint);

        // When & Then
        mockMvc.perform(patch("/api/v1/users/{userId}/points", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": " + rechargeAmount + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.balanceAmount").value(80000));
    }
}
