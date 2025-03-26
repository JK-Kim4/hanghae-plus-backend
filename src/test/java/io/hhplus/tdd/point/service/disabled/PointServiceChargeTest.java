package io.hhplus.tdd.point.service.disabled;

import io.hhplus.tdd.ErrorResponse;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.PointServiceImpl;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/*
 * @deprecated
 * PointService의 포인트 충전 관련 단위 테스트입니다. 의존 객체인 UserPointTable, PointHistoryTable를 대역(Stub)객체로 구현하여 파라미터로 전달받은 금액의 검증 관련 기능을 테스트합니다.
 * 정책 검증 책임의 별도 객체(PointPolicy.java)를 생성하여 해당 테스트는 더이상 진행하지 않으며 PointPolicyTest.java에서 정책 관련 단위 테스트를 수행합니다.
 */
@Disabled @Deprecated
@ExtendWith(MockitoExtension.class)
public class PointServiceChargeTest {

    @DisplayName("[정책-충전 1] 충전 금액은 반드시 100원 이상이어야합니다. (throw IllegalArgumentException)")
    @Test
    void throw_IllegalArgumentException_when_charging_amount_is_not_acceptable(
            @Mock UserPointTable userPointTable,
            @Mock PointHistoryTable pointHistoryTable
    ) {
        long zeroAmount = 0L; long minusAmount = -1L; long unacceptableAmount = 99L;
        UserPoint userPoint = UserPoint.empty(10L);

        when(userPointTable.selectById(10L)).thenReturn(userPoint);

        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        UserPoint targetUserPoint = pointService.selectById(10L);

        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> pointService.charge(targetUserPoint.id(), zeroAmount)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> pointService.charge(targetUserPoint.id(), unacceptableAmount)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> pointService.charge(targetUserPoint.id(), minusAmount)),
                () -> assertEquals(ErrorResponse.ERROR_MESSAGE_OUT_OF_RANGE,
                        assertThrows(IllegalArgumentException.class,
                                () -> pointService.charge(targetUserPoint.id(), zeroAmount))
                                .getMessage())
        );
    }

    @DisplayName("[정책-충전 2] 한번에 충전할 수 있는 금액은 3,000,000원을 초과할 수 없습니다. (throw IllegalArgumentException)")
    @Test
    void throw_IllegalArgumentException_when_charging_amount_is_out_of_range(
            @Mock UserPointTable userPointTable,
            @Mock PointHistoryTable pointHistoryTable
    ){
        long overAmount = 3_000_001L;
        UserPoint userPoint = UserPoint.empty(10L);

        when(userPointTable.selectById(10L)).thenReturn(userPoint);

        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        UserPoint targetUserPoint = pointService.selectById(10L);

        IllegalArgumentException illegalArgumentException
                = assertThrows(IllegalArgumentException.class,
                    () -> pointService.charge(targetUserPoint.id(), overAmount));

        assertEquals(ErrorResponse.ERROR_MESSAGE_OUT_OF_RANGE, illegalArgumentException.getMessage());
    }

    @DisplayName("[정책-충전 3] 최대로 보유할 수 있는 잔고액은 100,000,000원을 초과할 수 없습니다. (throw IllegalArgumentException)")
    @Test
    void throw_IllegalArgumentException_when_balance_amount_is_out_of_range(
            @Mock UserPointTable userPointTable,
            @Mock PointHistoryTable pointHistoryTable
    ){
        long maxAmount = 3_000_000L;
        UserPoint userPoint = new UserPoint(10L, 97_000_001, System.currentTimeMillis());
        when(userPointTable.selectById(10L)).thenReturn(userPoint);

        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        UserPoint targetUserPoint = pointService.selectById(10L);

        IllegalArgumentException illegalArgumentException
                = assertThrows(IllegalArgumentException.class,
                () -> pointService.charge(targetUserPoint.id(), maxAmount));

        assertEquals(ErrorResponse.ERROR_MESSAGE_OVER_MAX_BALANCE, illegalArgumentException.getMessage());
    }

    @DisplayName("정상적인 포인트 충전의 경우 요청 금액을 추가하고 UserPoint객체를 반환한다.")
    @Test
    void charge_amount_request_and_return_user_point(
            @Mock UserPointTable userPointTable,
            @Mock PointHistoryTable pointHistoryTable
    ){
        long plusAmount = 10000L;
        UserPoint userPoint = UserPoint.empty(10L);
        when(userPointTable.selectById(10L)).thenReturn(userPoint);

        when(userPointTable.insertOrUpdate(userPoint.id(), (userPoint.point()+ plusAmount) ))
                .thenReturn(new UserPoint(userPoint.id(), (userPoint.point() + plusAmount), System.currentTimeMillis()));

        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        UserPoint result = pointService.charge(userPoint.id(), plusAmount);

        Assertions.assertEquals(10L, result.id());
        Assertions.assertEquals(plusAmount, result.point());
    }


}
