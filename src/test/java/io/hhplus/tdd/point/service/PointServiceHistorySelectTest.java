package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.PointServiceImpl;
import io.hhplus.tdd.point.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointServiceHistorySelectTest {

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
