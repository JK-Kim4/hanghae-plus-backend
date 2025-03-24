package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final Logger logger = LoggerFactory.getLogger(PointServiceImpl.class);

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    @Override
    public UserPoint charge(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);
        PointPolicy.chargePointValidate(amount, userPoint.point());

        UserPoint chargedUserPoint = userPointTable.insertOrUpdate(userPoint.id(), userPoint.point() + amount);
        insertPointHistory(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
        return chargedUserPoint;
    }

    @Override
    public UserPoint use(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);
        PointPolicy.usePointValidate(amount, userPoint.point());

        UserPoint usedUserPoint = userPointTable.insertOrUpdate(userPoint.id(), (userPoint.point() - amount));
        insertPointHistory(id, amount, TransactionType.USE, System.currentTimeMillis());
        return usedUserPoint;
    }

    @Override
    public UserPoint selectById(long id) {
        return userPointTable.selectById(id);
    }

    @Override
    public PointHistory insertPointHistory(long userId, long amount, TransactionType transactionType, long timeMillis) {
        if(transactionType == null) throw new IllegalArgumentException("transactionType is null");

        return pointHistoryTable.insert(userId, amount, transactionType, timeMillis);
    }

    @Override
    public List<PointHistory> selectPointHistoryAllByUserId(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }
}
