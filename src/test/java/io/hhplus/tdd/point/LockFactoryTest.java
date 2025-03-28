package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LockFactoryTest {

    @DisplayName("동일한 id를 Key로 생성되는 Lock객체는 동일하다.")
    @Test
    void same_lock_test(){
        long id = 10L;
        ReentrantLock lock = LockFactory.getLock(id);
        ReentrantLock lock2 = LockFactory.getLock(id);

        assertEquals(lock, lock2);
    }

    @DisplayName("동일한 key값으로 비동기 생성된 ReentrantLock 객체는 모두 동일하다.")
    @Test
    void test() throws InterruptedException, ExecutionException {
        long id = 123L;
        int numThreads = 10;
        CompletableFuture<ReentrantLock>[] futures = new CompletableFuture[numThreads];

        //스레드별로 key가 123L인 ReentrantLock 객체 획득
        for (int i = 0; i < numThreads; i++) {
            futures[i] = CompletableFuture.supplyAsync(() -> LockFactory.getLock(id));
        }

        //획득한 ReentrantLock 객체가 동일한이 검증
        ReentrantLock expectedLock = futures[0].get();
        for (int i = 1; i < numThreads; i++) {
            assertEquals(expectedLock, futures[i].get());
        }
    }

}