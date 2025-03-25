package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserPointTest {

    @DisplayName("잔고가 0원인 default UserPoint 객체를 생성합니다.")
    @Test
    void return_default_user_point(){
        long noExistUserPointId = 99L;
        UserPoint userPoint = UserPoint.empty(noExistUserPointId);

        assertAll(
                () -> assertNotNull(userPoint),
                () -> assertEquals(0L, userPoint.point())
        );
    }
}
