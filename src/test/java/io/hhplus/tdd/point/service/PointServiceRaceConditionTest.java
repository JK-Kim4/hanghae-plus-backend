package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.PointServiceImpl;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
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
    PointService pointService;

    @BeforeEach
    void beforeEach() {
        pointService = new PointServiceImpl(new UserPointTable(), new PointHistoryTable());
    }

    @DisplayName("잔고액이 100_000원인 사용자에게 10개의 스레드로 100원 사용요청 50회, 500원 충전 요청 50회 수행 할 경우 최종 금액은 120_000원이다.")
    @Test
    public void race_condition_charge_use_test() throws InterruptedException {
        long id = 1L; long useAmount = 100L; long chargeAmount = 500L; long remainAmount = 100_000L;
        int threadCount = 10; int taskCount = 50;
        pointService.charge(id, remainAmount);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        CountDownLatch latch1 = new CountDownLatch(taskCount);
        CountDownLatch latch2 = new CountDownLatch(taskCount);

        for(int i = 0; i < taskCount; i++) {
            executorService.execute(() -> {
                pointService.charge(id, chargeAmount);
                latch1.countDown();
            });
            executorService.execute(() -> {
                pointService.use(id, useAmount);
                latch2.countDown();
            });
        }

        latch1.await();
        latch2.await();

        executorService.shutdown();

        UserPoint userPoint = pointService.selectById(id);
        assertEquals(remainAmount - (useAmount * taskCount) + (chargeAmount * taskCount), userPoint.point());
    }

    @DisplayName("잔고액이 100_000원인 사용자에게 10개의 스레드로 100원의 사용 요청을 100번 수행 할 경우 최종 금액은 90_000원이다.")
    @Test
    void race_condition_use_test() throws InterruptedException {
        long id = 1L; long useAmount = 100L; long remainAmount = 100_000L;
        int threadCount = 10; int taskCount = 100;
        pointService.charge(id, remainAmount);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        // CountDownLatch 생성 (작업 완료를 기다림)
        CountDownLatch latch = new CountDownLatch(taskCount);

        // 작업 실행
        for (int i = 0; i < taskCount; i++) {
            executorService.execute(() -> {
                //100원의 충전금액을 100번 충전
                pointService.use(id, useAmount);
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
        assertEquals(remainAmount - (useAmount * taskCount), userPoint.point());
    }


    @DisplayName("10개의 스레드를 생성하여 100원의 충전금액을 100번 수행할 경우 최종 충전 금액으로 10,000원이 충전된다.")
    @Test
    void race_condition_charge_test() throws InterruptedException {
        long id = 1L; long chargeAmount = 100L;
        int threadCount = 10; int taskCount = 100;
        // ExecutorService 생성 (고정된 스레드 풀 사용 / 5개)
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        // CountDownLatch 생성 (작업 완료를 기다림)
        CountDownLatch latch = new CountDownLatch(taskCount);

        UserPoint initUserPoint = pointService.selectById(id);

        // 작업 실행
        for (int i = 0; i < taskCount; i++) {
            executorService.execute(() -> {
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
        assertEquals(chargeAmount * taskCount,userPoint.point() - initUserPoint.point());
    }
}
