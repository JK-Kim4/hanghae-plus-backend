package io.hhplus.tdd.point;

import io.hhplus.tdd.ErrorResponse;

/**
 * 포인트 충전 / 사용에 대한 정책을 정의하고 검증합니다
 * - 포인트 충전 정책
 *   - [정책 1] 충전 금액은 반드시 100원 이상이어야합니다.
 *   - [정책 2] 한번에 충전할 수 있는 금액은 3,000,000원을 초과할 수 없습니다.
 *   - [정책 3] 최대로 보유할 수 있는 잔고액은 100,000,000원을 초과할 수 없습니다.
 *
 * - 포인트 사용 정책
 *   - [정책 1] 보유 잔고가 부족할 경우 결제를 진행할 수 없습니다.
 *   - [정책 1] 최소 주문 금액은 100원 이상이어야합니다.
 *
 *
 * */
public class PointPolicy {

    private static final long MINIMUM_CHARGE_LIMIT = 100L;
    private static final long MINIMUM_USE_LIMIT = 100L;
    private static final long MAXIMUM_CHARGE_LIMIT = 3_000_000L;
    private static final long MAX_BALANCE = 100_000_000L;

    public static void chargePointValidate(long amount, long remainAmount) {

        if (amount < MINIMUM_CHARGE_LIMIT) throw new IllegalArgumentException(ErrorResponse.ERROR_MESSAGE_OUT_OF_RANGE);
        if (amount > MAXIMUM_CHARGE_LIMIT) throw new IllegalArgumentException(ErrorResponse.ERROR_MESSAGE_OUT_OF_RANGE);
        if ((amount + remainAmount) > MAX_BALANCE) throw new IllegalArgumentException(ErrorResponse.ERROR_MESSAGE_OVER_MAX_BALANCE);

    }

    public static void usePointValidate(long amount, long remainAmount) {
        if (amount < MINIMUM_USE_LIMIT) throw new IllegalArgumentException(ErrorResponse.ERROR_MESSAGE_OUT_OF_RANGE);
        if (amount > remainAmount) throw new IllegalArgumentException(ErrorResponse.ERROR_MESSAGE_NOT_ENOUGH_BALANCE);
    }

}
