package io.hhplus.tdd.point;

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
        UserPoint userPoint = userPointTable.selectById(id);
        PointPolicy.validate(amount, userPoint.point());

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
