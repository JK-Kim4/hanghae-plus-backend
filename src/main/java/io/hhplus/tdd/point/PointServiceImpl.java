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

        return userPointTable.insertOrUpdate(userPoint.id(), userPoint.point() + amount);
    }

    @Override
    public UserPoint use(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);
        PointPolicy.usePointValidate(amount, userPoint.point());

        return userPointTable.insertOrUpdate(userPoint.id(), (userPoint.point() - amount));
    }

    @Override
    public UserPoint selectById(long id) {
        return userPointTable.selectById(id);
    }

    @Override
    public PointHistory insertPointHistory(UserPoint userPoint, TransactionType transactionType, long timeMillis) {
        if(transactionType == null) throw new IllegalArgumentException("transactionType is null");

        return pointHistoryTable.insert(userPoint.id(), userPoint.point(), transactionType, timeMillis);
    }

    @Override
    public List<PointHistory> selectAllByUserId(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }
}
