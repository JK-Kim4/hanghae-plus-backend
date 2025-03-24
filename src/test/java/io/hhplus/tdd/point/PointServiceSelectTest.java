package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointServiceSelectTest {

    @DisplayName("전달받은 고유번호에 해당하는 UserPoint가 table에 존재하지 않을 경우 default 객체를 반환한다.")
    @Test
    void select_default_user_point_object_when_not_exist(
            @Mock UserPointTable userPointTable,
            @Mock PointHistoryTable pointHistoryTable
    ) {
        long notExistUserId = 999L;

        when(userPointTable.selectById(notExistUserId))
                .thenReturn(UserPoint.empty(notExistUserId));

        PointService pointService = new PointServiceImpl(userPointTable,pointHistoryTable);

        UserPoint result = pointService.selectById(notExistUserId);

        Assertions.assertEquals(notExistUserId, result.id());
        Assertions.assertEquals(0L, result.point());
    }

    @DisplayName("전달받은 고유번호에 해당하는 UserPoint객체가 table에 존재할 경우 해당 객체를 반환한다.")
    @Test
    void select_default_user_point_object_when_exist(
            @Mock UserPointTable userPointTable,
            @Mock PointHistoryTable pointHistoryTable
    ){
        UserPoint userPoint = new UserPoint(10L, 1000L, System.currentTimeMillis());

        when(userPointTable.selectById(userPoint.id()))
                .thenReturn(userPoint);

        PointService pointService = new PointServiceImpl(userPointTable,pointHistoryTable);

        UserPoint result = pointService.selectById(userPoint.id());

        Assertions.assertEquals(userPoint.id(), result.id());
        Assertions.assertEquals(userPoint.point(), result.point());
    }
}
