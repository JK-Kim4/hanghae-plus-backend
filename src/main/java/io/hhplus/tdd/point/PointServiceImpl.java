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
        UserPoint result = selectById(id).charge(amount);
        insertOrUpdate(result.id(), result.point(), amount, TransactionType.CHARGE, result.updateMillis());
        return result;
    }

    @Override
    public UserPoint use(long id, long amount) {
        UserPoint result = selectById(id).use(amount);
        insertOrUpdate(result.id(), result.point(), amount, TransactionType.USE, result.updateMillis());
        return result;
    }

    @Override
    public UserPoint insertOrUpdate(long id, long currentAmount, long requestAmount, TransactionType type, long updateMillis) {
        insertPointHistory(id, requestAmount, type, updateMillis);
        return userPointTable.insertOrUpdate(id, currentAmount);
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
