package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PointPolicyTest {


    private final long UNDER_MINIMUM_CHARGE_LIMIT = 99L;
    private final long ALLOWED_CHARGE_LIMIT = 1_000_000L;
    private final long OVER_MAXIMUM_CHARGE_LIMIT = 3_000_001L;
    private final long OVER_MAX_BALANCE = 200_000_000L;
    private final long TEST_CURRENT_BALANCE = 3_000_000L;
    private final long OVER_CURRENT_BALANCE_USAGE = 3_100_000L;
    private final long ZERO = 0L;
    private final long NEGATIVE = -10L;



    @DisplayName("정상적인 충전금액의 경우 Exception이 발생하지 않습니다.")
    @Test
    void chargePointValidate_chargePointValidate_noException(){
        assertDoesNotThrow(()
                -> PointPolicy.chargePointValidate(ALLOWED_CHARGE_LIMIT, TEST_CURRENT_BALANCE));
    }

    @DisplayName("최소 충전금액(100원) 미만의 금액을 충전할 경우 IllegalArgumentException이 발생합니다.")
    @Test
    void chargePointValidate_chargePointValidate_Exception_when_under_minimum_charge_limit(){
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () ->
                        PointPolicy.chargePointValidate(UNDER_MINIMUM_CHARGE_LIMIT, TEST_CURRENT_BALANCE)),
                () -> assertThrows(IllegalArgumentException.class, () ->
                        PointPolicy.chargePointValidate(ZERO, TEST_CURRENT_BALANCE)),
                () -> assertThrows(IllegalArgumentException.class, () ->
                        PointPolicy.chargePointValidate(NEGATIVE, TEST_CURRENT_BALANCE))
        );
        ;
    }

    @DisplayName("최대 충전금액(3,000,000원)을 초과하는 금액을 충전할 경우 IllegalArgumentException이 발생합니다.")
    @Test
    void chargePointValidate_chargePointValidate_Exception_when_over_maximum_charge_limit(){
        assertThrows(IllegalArgumentException.class, () ->
                PointPolicy.chargePointValidate(OVER_MAXIMUM_CHARGE_LIMIT, TEST_CURRENT_BALANCE));
    }

    @DisplayName("충전 후 금액이 최대 한도(100_000_000원)를 초과하는 경우 IllegalArgumentException이 발생합니다.")
    @Test
    void chargePointValidate_chargePointValidate_Exception_when_over_maximum_balance(){
        assertThrows(IllegalArgumentException.class, () ->
                PointPolicy.chargePointValidate(OVER_MAX_BALANCE, TEST_CURRENT_BALANCE));
    }

    @DisplayName("현재 잔고가 결제 금액 이상일 경우 Exception이 발생하지 않습니다.")
    @Test
    void chargePointValidate_usePointValidate_noException(){
        assertAll(
                () -> assertDoesNotThrow(()
                        -> PointPolicy.usePointValidate(ALLOWED_CHARGE_LIMIT, TEST_CURRENT_BALANCE)),
                () -> assertDoesNotThrow(()
                        -> PointPolicy.usePointValidate(TEST_CURRENT_BALANCE, TEST_CURRENT_BALANCE))
        );
    }

    @DisplayName("현재 잔고가 결제 금액 미만일 경우 IllegalArgumentException이 발생합니다.")
    @Test
    void chargePointValidate_userPointValidate_Exception_when_balance_under_use_amount(){
        assertThrows(IllegalArgumentException.class, () ->
                PointPolicy.usePointValidate(OVER_CURRENT_BALANCE_USAGE, TEST_CURRENT_BALANCE));
    }


}
