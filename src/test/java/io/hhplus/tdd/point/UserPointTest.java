package io.hhplus.tdd.point;

import org.junit.jupiter.api.Assertions;
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

    @DisplayName("충전 금액을 전달받아 기존 UserPoint의 잔고에 반영하여 새로운 UserPoint객체를 리턴한다")
    @Test
    void charge_user_point_test(){
        long currentAmount = 1_000L; long chargedAmount = 2_000L;
        UserPoint userPoint = new UserPoint(1L, currentAmount, System.currentTimeMillis());
        UserPoint chargeUserPoint = userPoint.charge(chargedAmount);

        Assertions.assertEquals(currentAmount+chargedAmount, chargeUserPoint.point());
    }

    @DisplayName("사용 금액을 전달받아 기존 UserPoint의 잔고에 반영하여 새로운 UserPoint객체를 리턴한다")
    @Test
    void use_user_point_test(){
        long currentAmount = 3_000L; long useAmount = 2_000L;
        UserPoint userPoint = new UserPoint(1L, currentAmount, System.currentTimeMillis());
        UserPoint chargeUserPoint = userPoint.use(useAmount);

        Assertions.assertEquals(currentAmount-useAmount, chargeUserPoint.point());
    }

}
