package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public UserPoint charge(long chargeAmount) {
        PointPolicy.chargePointValidate(chargeAmount, this.point);
        return new UserPoint(this.id, (this.point + chargeAmount), System.currentTimeMillis());
    }

    public UserPoint use(long useAmount){
        PointPolicy.usePointValidate(useAmount, this.point);
        return new UserPoint(this.id, (this.point - useAmount), System.currentTimeMillis());
    }
}
