package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PointServiceRaceConditionTest {

    @Autowired
    private PointService pointService;

    @DisplayName("10개의 스레드를 생성하여 100원의 충전금액을 100번 수행할 경우 최종 충전 금액으로 10,000원이 충전된다.")
    @Test
    void race_condition_test() throws InterruptedException {
        long chargeAmount = 100L; long id = 1L;

        int threadCount = 10;
        int taskCount = 100;
        // ExecutorService 생성 (고정된 스레드 풀 사용)
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        // CountDownLatch 생성 (작업 완료를 기다림)
        CountDownLatch latch = new CountDownLatch(taskCount);

        // 작업 실행
        for (int i = 0; i < taskCount; i++) {
            executorService.submit(() -> {
                //100원의 충전금액을 100번 충전
                pointService.charge(id, chargeAmount);
                // 작업 완료 알림
                latch.countDown();
            });
        }

        // 모든 작업이 완료될 때까지 대기
        latch.await();

        // ExecutorService 종료
        executorService.shutdown();

        // 결과 확인
        UserPoint userPoint = pointService.selectById(id);
        assertEquals(chargeAmount * taskCount, userPoint.point());
        // 예상 결과: 100 (taskCount)

    }
}
