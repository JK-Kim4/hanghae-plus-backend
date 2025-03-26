package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.PointServiceImpl;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointServiceUnitTest {

    @DisplayName("PointService.charge() 호출 시 PointHistoryTable.insert()과 userPointTable.insertOrUpdate()가 한번 실행된다.")
    @Test
    void create_point_history_when_charge_success(
            @Mock UserPointTable userPointTable,
            @Mock PointHistoryTable pointHistoryTable
    ){
        long id = 1L; long chargeAmount = 500L;
        UserPoint userPoint = UserPoint.empty(id);
        when(userPointTable.selectById(id)).thenReturn(userPoint);

        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        UserPoint result = pointService.charge(id, chargeAmount);

        verify(pointHistoryTable, times(1))
                .insert(userPoint.id(), chargeAmount, TransactionType.CHARGE, result.updateMillis());

        verify(userPointTable, times(1)).
                insertOrUpdate(id, chargeAmount);
    }

    @DisplayName("PointService.use() 호출 시 PointHistoryTable.insert()과 userPointTable.insertOrUpdate()가 한번 실행된다.")
    @Test
    void use_point_history_when_charge_success(
            @Mock UserPointTable userPointTable,
            @Mock PointHistoryTable pointHistoryTable
    ){
        long id = 1L; long useAmount = 500L; long currentAmount = 300_000L;
        UserPoint userPoint = new UserPoint(id, currentAmount, System.currentTimeMillis());
        when(userPointTable.selectById(id)).thenReturn(userPoint);

        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        UserPoint result = pointService.use(id, useAmount);

        verify(pointHistoryTable, times(1))
                .insert(userPoint.id(), useAmount, TransactionType.USE, result.updateMillis());

        verify(userPointTable, times(1)).
                insertOrUpdate(id, useAmount);
    }


}
