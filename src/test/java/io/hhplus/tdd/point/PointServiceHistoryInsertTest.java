package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointServiceHistoryInsertTest {

    @DisplayName("TransactionType가 누락된 요청은 등록할 수 없습니다. (throw IllegalArgumentException")
    @Test
    void throw_IllegalArgumentException_when_TransactionType_is_null(
            @Mock UserPointTable userPointTable,
            @Mock PointHistoryTable pointHistoryTable
    ){
        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);
        assertThrows(IllegalArgumentException.class, () -> {
            pointService.insertPointHistory(0L, 100L, null, System.currentTimeMillis());
        });
    }

    @DisplayName("userpoint id와 amount를 전달받아 PointHistory를 추가합니다.")
    @Test
    void insert_point_history_with_parameters_test(
            @Mock UserPointTable userPointTable,
            @Mock PointHistoryTable pointHistoryTable){
        long userId = 10L; long amount = 100L;
        long updateMillis = System.currentTimeMillis();

        PointHistory pointHistory = new PointHistory(1L, userId, amount, TransactionType.CHARGE, updateMillis);

        when(pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, updateMillis)).thenReturn(pointHistory);

        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        PointHistory result = pointService.insertPointHistory(userId, amount, TransactionType.CHARGE,updateMillis);

        assertEquals(pointHistory, result);

    }

    @DisplayName("UserPoint와 TransactionType를 전달받아 PointHistory를 추가합니다.")
    @Disabled
    @Test
    void insert_point_history_with_parameters_testP_disabled(
            @Mock UserPointTable userPointTable,
            @Mock PointHistoryTable pointHistoryTable
            ){
        long updateMillis = System.currentTimeMillis();
        UserPoint userPoint = UserPoint.empty(10L);
        PointHistory pointHistory = new PointHistory(1L, userPoint.id(), userPoint.point(), TransactionType.CHARGE, updateMillis);

        when(pointHistoryTable.insert(userPoint.id(), userPoint.point(), TransactionType.CHARGE, updateMillis)).thenReturn(pointHistory);

        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        PointHistory result = pointService.insertPointHistory(10L, userPoint.point(), TransactionType.CHARGE,updateMillis);

        assertEquals(pointHistory, result);
    }
}
