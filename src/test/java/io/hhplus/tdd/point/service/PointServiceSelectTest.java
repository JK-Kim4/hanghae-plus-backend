package io.hhplus.tdd.point.service;

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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointServiceSelectTest {

    /*
    * @deprecated
    * 등록되지 않은 UserPoint 조회 요청 시  UserPoint.empty(id)를 실행하여 default 객체를 반환합니다. table.getOrDefault(id, UserPoint.empty(id))
    * UserPoint.empty(id) 기능에 대한 테스트는 UserPointTest.java에서 진행하므로 해당 테스트는 검증하지않습니다.
    * */
    @Disabled @Deprecated
    @DisplayName("UserPointTable에 등록되어있지 않은 사용자를 조회할 경우 amount가 0원인 default객체를 반환합니다.")
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
