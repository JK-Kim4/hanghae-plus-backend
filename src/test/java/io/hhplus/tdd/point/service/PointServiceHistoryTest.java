package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointServiceHistoryTest {


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

    @DisplayName("userId를 파라미터로 등록되어있는 PointHistory를 모두 조회한다.")
    @Test
    void select_point_history_by_user_id_test(
            @Mock UserPointTable userPointTable,
            @Mock PointHistoryTable pointHistoryTable
    ){
        PointHistory pointHistory1 = new PointHistory(1L, 10L, 100L, TransactionType.CHARGE, System.currentTimeMillis());
        PointHistory pointHistory2 = new PointHistory(2L, 10L, 200L, TransactionType.CHARGE, System.currentTimeMillis());
        PointHistory pointHistory3 = new PointHistory(3L, 10L, 300L, TransactionType.CHARGE, System.currentTimeMillis());

        List<PointHistory> pointHistories = List.of(pointHistory1, pointHistory2, pointHistory3);

        when(pointHistoryTable.selectAllByUserId(10L)).thenReturn(pointHistories);

        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        List<PointHistory> resultList = pointService.selectPointHistoryAllByUserId(10L);

        assertEquals(3, resultList.size());
    }


}
