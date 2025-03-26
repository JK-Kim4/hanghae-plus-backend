package io.hhplus.tdd.point.service;

import io.hhplus.tdd.ErrorResponse;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PointServiceIntegrationTest {

    private long id = 1L;
    private long defaultAmount = 5000L;

    private long maxAmountTestUserPoint = 10L;

    @Autowired
    PointService pointService;

    @Autowired
    UserPointTable userPointTable;

    @BeforeEach
    void setUp() {
        userPointTable.insertOrUpdate(this.id, this.defaultAmount);
        userPointTable.insertOrUpdate(this.maxAmountTestUserPoint, 97_000_001L);
    }


    @DisplayName("등록된 사용자의 포인트 충전 요청이 성공할경우 잔고와 충전 포인트가 합산된 UserPoint 객체를 반환한다.")
    @Test
    void test() {
        long chargeAmount = 100_000L;

        UserPoint result = pointService.charge(this.id, chargeAmount);

        assertNotNull(result);
        assertEquals(defaultAmount + chargeAmount, result.point());
    }

    @DisplayName("등록되지 않은 사용자의 포인트 충전 요청이 성공할 경우 충전 포인트가 반영된 UserPoint 객체를 반환한다.")
    @Test
    void test2() {
        long noExistUserPointId = 99L; long chargeAmount = 100_000L;

        UserPoint result = pointService.charge(noExistUserPointId, chargeAmount);

        assertNotNull(result);
        assertEquals(chargeAmount, result.point());
    }

    @DisplayName("충전 요청 금액이 최소 충전 한도(100L) 미만일 경우 IllegalArgumentException(ErrorResponse.ERROR_MESSAGE_OUT_OF_RANGE)를 반환한다.")
    @Test
    void test3() {
        long underMinimumAmount = 99L;

        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class,
                        () -> pointService.charge(this.id, underMinimumAmount));

        assertEquals(ErrorResponse.ERROR_MESSAGE_OUT_OF_RANGE, illegalArgumentException.getMessage());
    }

    @DisplayName("충전 요청 금액이 최대 충전 한도를 초과(3_000_000L)할 경우 IllegalArgumentException(ErrorResponse.ERROR_MESSAGE_OUT_OF_RANGE)를 반환한다.")
    @Test
    void test4() {
        long overMaximumAmount = 3_000_001L;

        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class,
                        () -> pointService.charge(this.id, overMaximumAmount));

        assertEquals(ErrorResponse.ERROR_MESSAGE_OUT_OF_RANGE, illegalArgumentException.getMessage());
    }

    @DisplayName("충전 요청 금액과 사용자의 보유 잔고의 합이 최대 한도(100_000_000L)를 초과할 경우 IllegalArgumentException(ErrorResponse.ERROR_MESSAGE_OVER_MAX_BALANCE)를 반환한다.")
    @Test
    void test5() {
         long chargeAmount = 3_000_000L;

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> pointService.charge(this.maxAmountTestUserPoint, chargeAmount));

        assertEquals(ErrorResponse.ERROR_MESSAGE_OVER_MAX_BALANCE, illegalArgumentException.getMessage());
    }

    @DisplayName("M번의 정상적인 충전 요청이 수행되면 TransactionType.CHARGE의 M개의 포인트 충전 이력이 저장된다.")
    @Test
    void test6() {
        long chargeMemberId = 20L; long chargeAmount = 100_000L;
        userPointTable.insertOrUpdate(this.id, 0L);
        int m = 5;

        for(int i = 0; i < 5; i++){
            pointService.charge(chargeMemberId,  chargeAmount);
        }

        List<PointHistory> resultList = pointService.selectPointHistoryAllByUserId(chargeMemberId);

        assertNotNull(resultList);
        assertEquals(m, resultList.size());
        for(PointHistory pointHistory : resultList){
            assertEquals(TransactionType.CHARGE, pointHistory.type());
        }
    }

    @DisplayName("등록된 사용자의 포인트 사용 요청이 성공할 경우 잔고에서 사용 포인트가 차감된 UserPoint 객체를 반환한다.")
    @Test
    void test7() {
        long useAmount = 1_000L;

        UserPoint result = pointService.use(this.id, useAmount);
        assertNotNull(result);
        assertEquals((this.defaultAmount - useAmount), result.point());
    }

    @DisplayName("등록되지 않은 사용자가 포인트 사용할 경우 IllegalArgumentException(ErrorResponse.ERROR_MESSAGE_NOT_ENOUGH_BALANCE)를 반환한다.")
    @Test
    void test8() {
        long noExistUserPointId = 999L; long userAmount = 100_000L;

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> pointService.use(noExistUserPointId, userAmount));

        assertEquals(ErrorResponse.ERROR_MESSAGE_NOT_ENOUGH_BALANCE, illegalArgumentException.getMessage());
    }

    @DisplayName("포인트 사용 요청이 최소 사용 금액(100L) 미만일 경우 IllegalArgumentException(ErrorResponse.ERROR_MESSAGE_OUT_OF_RANGE)를 반환한다.")
    @Test
    void test9() {
        long underMinimumAmount = 99L;

        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class,
                        () -> pointService.use(this.id, underMinimumAmount));

        assertEquals(ErrorResponse.ERROR_MESSAGE_OUT_OF_RANGE, illegalArgumentException.getMessage());
    }

    @DisplayName("포인트 사용 요청이 사용자의 잔고를 초과할 경우 IllegalArgumentException(ErrorResponse.ERROR_MESSAGE_NOT_ENOUGH_BALANCE)를 반환한다.")
    @Test
    void test10() {
        long overCurrentAmount = 100_000L;

        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class,
                        () -> pointService.use(this.id, overCurrentAmount));

        assertEquals(ErrorResponse.ERROR_MESSAGE_NOT_ENOUGH_BALANCE, illegalArgumentException.getMessage());
    }

    @DisplayName("M번의 정상적인 포인트 사용 요청이 수행되면 TransactionType.USE 상태의 M개의 포인트 충전 이력이 저장된다.")
    @Test
    void test11() {
        long useUserPointId = 30L; long amount = 100_000L;
        userPointTable.insertOrUpdate(useUserPointId, amount);
        long useAmount = 500L;
        int m = 5;

        for(int i = 0; i < 5; i++){
            pointService.use(useUserPointId, useAmount);
        }

        List<PointHistory> pointHistoryList = pointService.selectPointHistoryAllByUserId(useUserPointId);

        assertNotNull(pointHistoryList);
        assertEquals(m, pointHistoryList.size());
        for(PointHistory pointHistory : pointHistoryList){
            assertEquals(TransactionType.USE, pointHistory.type());
        }
    }

    @DisplayName("등록된 사용자의 고유 번호를 전달받아 UserPoint를 반환한다.")
    @Test
    void test12() {
        UserPoint userPoint = pointService.selectById(this.id);

        assertNotNull(userPoint);
        assertEquals(this.defaultAmount, userPoint.point());
    }

    @DisplayName("등록되지 않은 사용자의 고유번호를 전달받아 잔고가 0원인 UserPoint를 반환한다.")
    @Test
    void test13() {
        long notExistUserPointId = 99L;

        UserPoint userpoint = pointService.selectById(notExistUserPointId);

        assertNotNull(userpoint);
        assertEquals(0L, userpoint.point());
    }

}
