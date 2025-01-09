//package kr.hhplus.be.server.domain.service;
//
//import kr.hhplus.be.server.backup.CouponQueue;
//import kr.hhplus.be.server.backup.CouponQueueRepository;
//import kr.hhplus.be.server.backup.CouponQueueServiceImpl;
//import kr.hhplus.be.server.backup.DuplicateCouponRequestException;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CouponQueueServiceImplTest {
//
//    @Mock
//    private CouponQueueRepository couponQueueRepository;
//
//    @InjectMocks
//    private CouponQueueServiceImpl couponQueueService;
//
//    @Test
//    void 중복되지_않는_요청은_대기열에_추가된다() {
//        // Given
//        Long couponId = 1L;
//        String userId = "user123";
//        when(couponQueueRepository.existsByCouponIdAndUserIdAndProcessedFalse(couponId, userId)).thenReturn(false);
//
//        // When
//        couponQueueService.addToQueue(couponId, userId);
//
//        // Then
//        ArgumentCaptor<CouponQueue> captor = ArgumentCaptor.forClass(CouponQueue.class);
//        verify(couponQueueRepository).save(captor.capture());
//        CouponQueue savedQueue = captor.getValue();
//
//        assertEquals(couponId, savedQueue.getCouponId());
//        assertEquals(userId, savedQueue.getUserId());
//        assertFalse(savedQueue.isProcessed());
//    }
//
//    @Test
//    void 중복된_요청은_예외를_발생시킨다() {
//        // Given
//        Long couponId = 1L;
//        String userId = "user123";
//        when(couponQueueRepository.existsByCouponIdAndUserIdAndProcessedFalse(couponId, userId)).thenReturn(true);
//
//        // When & Then
//        DuplicateCouponRequestException exception = assertThrows(DuplicateCouponRequestException.class,
//                () -> couponQueueService.addToQueue(couponId, userId));
//        assertEquals("이미 요청된 쿠폰입니다.", exception.getMessage());
//        verify(couponQueueRepository, never()).save(any());
//    }
//
//    @Test
//    void 처리되지_않은_다음_대기열_요청을_반환한다() {
//        // Given
//        Long couponId = 1L;
//        CouponQueue queue = new CouponQueue(couponId, "user123");
//        when(couponQueueRepository.findNextUnprocessedByCouponId(couponId)).thenReturn(Optional.of(queue));
//
//        // When
//        CouponQueue result = couponQueueService.findNextUnprocessed(couponId);
//
//        // Then
//        assertEquals(queue, result);
//    }
//
//    @Test
//    void 처리되지_않은_대기열이_없으면_예외를_발생시킨다() {
//        // Given
//        Long couponId = 1L;
//        when(couponQueueRepository.findNextUnprocessedByCouponId(couponId)).thenReturn(Optional.empty());
//
//        // When & Then
//        IllegalStateException exception = assertThrows(IllegalStateException.class,
//                () -> couponQueueService.findNextUnprocessed(couponId));
//        assertEquals("대기열에 처리되지 않은 요청이 없습니다.", exception.getMessage());
//    }
//
//    @Test
//    void 대기열을_처리완료로_표시하고_저장한다() {
//        // Given
//        CouponQueue queue = new CouponQueue(1L, "user123");
//        when(couponQueueRepository.save(any(CouponQueue.class))).thenReturn(queue);
//
//        // When
//        CouponQueue result = couponQueueService.processQueue(queue);
//
//        // Then
//        assertTrue(queue.isProcessed());
//        assertEquals(queue, result);
//        verify(couponQueueRepository).save(queue);
//    }
//}