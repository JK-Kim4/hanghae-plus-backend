package io.hhplus.tdd.point;

import java.util.List;

public interface PointService {

    UserPoint charge(long id, long amount);

    UserPoint use(long id, long amount);

    UserPoint selectById(long id);

    PointHistory insertPointHistory(long userId, long amount, TransactionType transactionType, long timeMillis);

    List<PointHistory> selectPointHistoryAllByUserId(long userPointId);
}
