package io.hhplus.tdd.point;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockFactory {
    private static final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    public static ReentrantLock getLock(long id) {
        return lockMap.computeIfAbsent(id, k -> new ReentrantLock(true));
    }
}
