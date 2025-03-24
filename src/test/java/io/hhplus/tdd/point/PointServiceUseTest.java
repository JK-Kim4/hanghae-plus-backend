package io.hhplus.tdd.point;

import io.hhplus.tdd.ErrorResponse;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointServiceUseTest {

    @DisplayName("[정책-사용 1]보유 잔고가 부족할 경우 결제를 진행할 수 없습니다. (throw IllegalArgumentException)")
    @Test
    void throw_IllegalArgumentException_when_balance_amount_is_out_of_range(
            @Mock UserPointTable userPointTable,
            @Mock PointHistoryTable pointHistoryTable
    ){
        long amount = 10_000L; long requestAmount = 20_000L;
        UserPoint userPoint = new UserPoint(10L, amount, System.currentTimeMillis());

        when(userPointTable.selectById(10L)).thenReturn(userPoint);
        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        UserPoint targetUserPoint = pointService.selectById(10L);

        IllegalArgumentException illegalArgumentException
                = assertThrows(IllegalArgumentException.class,
                () -> pointService.use(targetUserPoint.id(), requestAmount));

        assertEquals(ErrorResponse.ERROR_MESSAGE_NOT_ENOUGH_BALANCE, illegalArgumentException.getMessage());

    }

    @DisplayName("보유 잔고가 유효(보유잔고 > 결제잔고)할 경우 포인트를 차감하고 UserPoint를 반환한다")
    @Test
    void use_amount_request_and_return_user_point(
            @Mock UserPointTable userPointTable,
            @Mock PointHistoryTable pointHistoryTable
    ){
        long amount = 3_000_000L; long requestAmount = 10_000L;
        UserPoint userPoint = new UserPoint(10L, amount, System.currentTimeMillis());

        when(userPointTable.selectById(10L)).thenReturn(userPoint);
        when(userPointTable.insertOrUpdate(10L, (amount - requestAmount)))
                .thenReturn(new UserPoint(10L, (amount - requestAmount), System.currentTimeMillis()));

        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        UserPoint result = pointService.use(10L, requestAmount);

        assertEquals(amount - requestAmount, result.point());
    }

}
