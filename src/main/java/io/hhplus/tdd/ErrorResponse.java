package io.hhplus.tdd;

public record ErrorResponse(
        String code,
        String message
) {

    public static final String ERROR_MESSAGE_NOT_ALLOWED_PARAMETER = "올바르지 않은 형식의 요청입니다. 확인 후 다시 시도해주세요.";
    public static final String ERROR_MESSAGE_SYSTEM_FAIL = "에러가 발생했습니다.";
    public static final String ERROR_MESSAGE_OUT_OF_RANGE = "유효하지 않은 충전금액입니다. (충전 금액 범위: 100원 이상, 3,000,000원 이하)";
    public static final String ERROR_MESSAGE_OVER_MAX_BALANCE = "보유할 수 있는 최대 잔고를 초과하였습니다. (최대 잔고 100,000,000원)";
    public static final String ERROR_MESSAGE_NOT_ENOUGH_BALANCE = "잔고가 부족합니다. 충전 후 시도해주세요.";
}
