package io.hhplus.tdd.point;

import io.hhplus.tdd.ErrorResponse;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final Logger logger = LoggerFactory.getLogger(PointServiceImpl.class);

    private final UserPointTable userPointTable;

    @Override
    public UserPoint charge(long id, long amount) {
        if (amount < 100L) throw new IllegalArgumentException(ErrorResponse.ERROR_MESSAGE_NOT_ALLOWED_PARAMETER);
        if (amount > 3_000_000L) throw new IllegalArgumentException(ErrorResponse.ERROR_MESSAGE_OUT_OF_RANGE);

        UserPoint userPoint = userPointTable.selectById(id);
        if((userPoint.point() + amount) > 100_000_000L) throw new IllegalArgumentException(ErrorResponse.ERROR_MESSAGE_OVER_MAX_BALANCE);

        return userPointTable.insertOrUpdate(userPoint.id(), userPoint.point() + amount);
    }

    @Override
    public UserPoint use(long id, long amount) {
        return null;
    }

    @Override
    public UserPoint findById(long id) {
        return userPointTable.selectById(id);
    }

}
