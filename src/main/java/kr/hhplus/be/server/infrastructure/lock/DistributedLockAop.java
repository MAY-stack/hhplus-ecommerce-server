package kr.hhplus.be.server.infrastructure.lock;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Order(1)
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAop {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";
    private final RedissonClient redissonClient; // Redisson Client 객체 주입
//    private final AopForTransaction aopForTransaction; // 트랜잭션 관리를 위한 AOP

    @Around("@annotation(kr.hhplus.be.server.infrastructure.lock.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 실행할 메서드 정보 가져오기
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        // 2. Redis 락 키 생성
        String key = REDISSON_LOCK_PREFIX + distributedLock.key();
        log.info("lock on [method:{}] [key:{}]", method, key);

        RLock rLock = redissonClient.getLock(key);
        String lockName = rLock.getName();

        try {
            // 3. 락을 시도해서 획득
            boolean available =
                    rLock.tryLock(
                            distributedLock.waitTime(),
                            distributedLock.leaseTime(),
                            distributedLock.timeUnit());
            if (!available) {
                throw new IllegalStateException(ErrorMessage.LOCK_NOT_AVAILABLE.getMessage());
            }
            log.info("get lock");

            // 4. 락을 획득한 경우, 트랜잭션과 함께 메서드 실행 -> 락과 트랜잭션 생성 분리
//            return aopForTransaction.proceed(joinPoint);
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            // 5. 락을 기다리는 동안 인터럽트 발생 시 예외 처리
            throw new IllegalStateException(ErrorMessage.LOCK_NOT_AVAILABLE.getMessage());
        } finally {
            try {
                // 6. 락 해제
                rLock.unlock();
                log.info("unlock complete [Lock:{}] ", lockName);
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already Unlocked");
            }
        }
    }
}
