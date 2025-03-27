# [Java] 동시성 제어 방식

### Thread safe하다?
하나의 프로세스가 두개 이상의 스레드를 할당받아 동작하는 멀티스레드 환경에서는
공유 자원에 대해 다수의 스레드가 동시에 접근하는 상황이 발생할 수 있습니다. Thread safe하다는 것은 이러한 멀티 스레드 환경에서도 프로그램의 동작에 문제가 없음을 이야기합니다.

### Java는 Thread safe한가?

우리가 사용하는 Java는 멀티스레드 프로그래밍을 지원하지만 개발자가 명시적으로 스레드 안전성을 고려하지 않으면 여러 개의 스레드가 공유 자원에 동시에 접근하여 예상과는 다른 결과를 초래할 수 있습니다.
```java
import java.util.concurrent.CountDownLatch;

public class CountDownLatchRaceCondition {

    private static int sharedValue = 0;

    public static void main(String[] args) throws InterruptedException {
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    // 경합 상황 발생: 여러 스레드가 동시에 sharedValue를 수정
                    sharedValue++;
                }
                latch.countDown();
            }).start();
        }

        // 모든 스레드가 작업을 완료할 때까지 대기
        latch.await();

        System.out.println("Final sharedValue: " + sharedValue);
        // 예상 결과: 5000 (threadCount * 1000)
        // 실제 결과: 매번 실행 시 값이 달라짐 (경합 조건으로 인해)
    }
}
```
5개의 스레드를 생성하여 공유 자원인 ststic 변수 sharedValue를 각각 1000번씩 증가시켜보는 예제 코드입니다.
실행 결과로 `Final sharedValue: 5000`이 나오는 것이 정상이지만 매번 실행마다 동일하지않은 결과가 나오는 것을 확인할 수 있습니다.

만약 공유 자원에 대한 스레드간 경합 상황이 빈번하게 발생하는 서비스라면 동시성 제어 부족으로 인하여 치명적인 문제 상황이 발생할 수 있기 때문에 
공유 자원에 대한 동시성 문제는 필수적인 고려사항입니다.
<br> 

---

---

## Java의 동시성 제어 기술과 장단점

### synchronized 키워드
`synchronized`키워드는 가장 간단하게 동시성 문제를 해결할 수 있는 방법입니다.
메서드혹은 특정 코드 블럭에 대하여 스레드의 접근을 제어할 수 있습니다.

`synchronized`는 모니터(Monitor)라는 개념을 기반으로 동작합니다.
모니터는 객체 또는 클래스와 연결된 잠금 메커니즘으로 객체 또는 클래스의 상태를 추적하고 변경에 대한 스레드간 동기화를 관리합니다.
 <br>synchronized 블록 또는 메서드를 실행하려는 스레드는 해당 객체 또는 클래스의 모니터를 획득해야 합니다. 모니터는 한 번에 하나의 스레드만이 획득할 수있으며
다른 스레드가 이미 모니터를 획득한 상태일 경우, 현재 스레드는 모니터가 반환될 때까지 대기상태가 됩니다.

```java
import java.util.concurrent.CountDownLatch;

public class CountDownLatchRaceCondition {

    private static int sharedValue = 0;

    private static synchronized void incrementSharedValue() { //synchronized 키워드를 사용하여 객체에 대한 동시성 제어
        sharedValue++;
    }

    public static void main(String[] args) throws InterruptedException {
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    incrementSharedValue(); //동시성 제어 객체 실행, 스레드 접근 제어
                }
                latch.countDown();
            }).start();
        }

        latch.await();
        System.out.println("Final sharedValue: " + sharedValue);
        // 예상 결과: 5000 (threadCount * 1000)
        // 실제 결과: 항상 5000 (synchronized 키워드로 경합 조건 해결)
    }
}
```
앞서 확인하였던 동시성 제어 문제 예시 코드에 synchronized 키워드를 사용하여 동시성 제어를 위한 incrementSharedValue() 메서드를 생성하였습니다.
이제 incrementSharedValue() 가 실행되면 해당 객체는 Lock을 획득하게 되고 동작이 완료(Lock 해제)가 되기 전까지는 다른 스레드의 접근을 제한합니다.
예시 코드를 실행해보면 예상하였던 `Final sharedValue: 5000`이 변함없이 반환되는 것을 확인할 수 있습니다.
<br>(코드 블럭 수준에서 동시성 제어를 하기위한다면 `synchronized(lock){ //... }` 와 같이 사용)

- **장점**
  - `synchronized` 키워드는 자바에서 제공하는 기능으로 별도의 라이브러리 없이 간단하게 키워드를 추가하는 방식으로 동시성 문제를 해결할 수 있습니다.
  - 한 번에 하나의 스레드만 접근이 허용되므로 데이터의 일관성을 보장할 수 있습니다.
- **단점**
  - `synchronized`는 단순한 동기화 처리만 지원하므로 잠금 획득 및 해제 순서, 타임아웃 설정 등 세밀한 제어가 불가능합니다.
    - Lock을 획득한 스레드에 문제가 생겨 반환이 되지 않을 경우 시스템이 멈추는 문제가 발생할 수도 있습니다.
  - 여러개의 스레드가 공유 자원에 대한 잠금(Lock)을 동시에 획득하려고 하는 경우 데드락(DeadLock)이 발생할 수 있습니다.
  - 잠금을 획득하지 못한 스레드는 블로킹되어 대기해야 하므로, 스레드 풀의 효율성을 떨어뜨릴 수 있습니다.

---

### [java.util.concurrent](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html) 패키지
java에서는 java.util.concurrent 패키지는 다양한 기능을 수행하는 Lock 인터페이스와 구현체(ReentrantLock 등), 원자성이 보장되는 자료 구조(ConcurrentHashMap, AtomicInteger 등)를 제공하므로서
멀티스레드 환경에서도 안전한 기능을 구현할 수 있도록 도와줍니다.

#### ReentrantLock
`ReentrantLock`은 java.util.concurrent.locks.Lock 인터페이스의 구현체 중 하나입니다.
`synchronized` 키워드와 비교하여 보다 세밀한 동기화 메커니즘의 제어가 가능합니다.
```java
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CountDownLatchRaceCondition {

    private static int sharedValue = 0;
    private static Lock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    // ReentrantLock을 사용하여 sharedValue를 안전하게 수정
                    lock.lock();  // lock을 획득한 스레드가 해제(unlock())이전까지 다른 스레드의 접근 제어
                    try {
                        sharedValue++;
                    } finally {
                        lock.unlock();
                    }
                }
                latch.countDown();
            }).start();
        }

        // 모든 스레드가 작업을 완료할 때까지 대기
        latch.await();

        System.out.println("Final sharedValue: " + sharedValue);
        // 예상 결과: 5000 (threadCount * 1000)
        // 실제 결과: 항상 5000 (ReentrantLock으로 경합 조건 해결)
    }
}

```

- 장점
  - `ReentrantLock`은 synchronized 키워드보다 더 다양한 기능을 제공합니다. 예를 들어, 잠금 획득 시도를 중단하거나, 잠금 획득 시 타임아웃을 설정하거나, 조건 변수를 사용하여 스레드 간 통신을 더 세밀하게 제어할 수 있습니다.
  - `ReentrantLock`은 공정한 잠금(fair lock)을 지원합니다. 공정한 잠금은 스레드들이 요청한 순서대로 잠금을 획득하도록 보장합니다. 이는 특정 스레드가 잠금을 계속해서 획득하는 것을 방지하고, 모든 스레드가 공평하게 자원에 접근할 수 있도록 합니다.
    - 반면 `synchronized`는 대기 순서에 상관 없이 어떤 스레드가 lock을 획득하는지 예측할 수 없습니다.
  - tryLock(long time, TimeUnit unit) 메서드를 사용하여 지정된 시간 동안만 잠금 획득을 시도할 수 있습니다. 이는 데드락(deadlock) 발생 가능성을 줄이고, 애플리케이션의 응답성을 향상시킵니다.
  - newCondition() 메서드를 통해 Condition 객체를 생성하여 스레드 간의 통신을 보다 세밀하게 제어할 수 있습니다. 이는 특정 조건이 만족될 때까지 스레드를 대기시키거나, 대기 중인 스레드를 깨우는 데 유용합니다.
- 단점
  - ReentrantLock은 synchronized 키워드보다 사용 방법이 복잡합니다. 개발자는 명시적으로 lock() 및 unlock() 메서드를 호출해야 하며, try-finally 블록을 사용하여 잠금 해제를 보장해야 합니다. 이는 코드의 가독성을 떨어뜨리고, 개발자의 실수를 유발할 수 있습니다.
  - ReentrantLock은 사용자가 명시적으로 잠금을 해제해야 합니다. 만약 잠금을 해제하지 않으면 다른 스레드들이 영원히 대기하게 되어 데드락이 발생할 수 있습니다. 이러한 이유로 try-finally 구문을 사용하여 반드시 잠금을 해제하여야 합니다.

---


#### ConcurrentHashMap 
ConcurrentHashMap은 자바에서 멀티스레드 환경에서 안전하게 사용할 수 있는 해시 맵 구현체입니다. 일반적인 HashMap과 달리, 여러 스레드가 동시에 맵에 접근하고 수정하더라도 데이터 일관성을 유지합니다.

ConcurrentHashMap은 내부적으로 여러 개의 세그먼트로 분할되어 있습니다. 각 세그먼트는 독립적인 해시 테이블이며, 각 세그먼트는 자체적인 잠금(lock)을 가지고 있습니다.
<br><br>여러 스레드가 동시에 다른 세그먼트에 접근하는 경우, 잠금 경합(lock contention) 없이 작업을 수행할 수 있습니다.
→ 내부적으로는 좀 더 작은 단위로 쪼개어져있기 때문에 각각의 스레드가 서로 다른 조각에 접근할 경우 Thread-Safe하게 동작하는 것처럼 수행된다. 만약 동일한 세그먼트에 대하여 경합이 발생할 경우 Lock을 하여 동시성 이슈를 방지한다. (Lock striping)

```java
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapExample {

    public static void main(String[] args) throws InterruptedException {
        // 스레드 안전한 맵 생성
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        // 스레드 생성 및 실행
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    // 스레드 안전하게 맵의 값을 증가시킴
                    map.merge("key", 1, Integer::sum);
                }
            });
            threads[i].start();
        }

        // 스레드 작업 완료 대기
        for (Thread thread : threads) {
            thread.join();
        }

        // 최종 맵 값 출력
        System.out.println("Final map value: " + map.get("key"));
    }
}
```

- **장점**
  - 세그먼트 기법 사용으로 여러 스레드가 동시에 맵 객체에 접근하고 수정할 수 있으며 데이터의 일관성이 보장됩니다.
  - 읽기 작업에서 뛰어난 성능
- **단점**
  - 내부적으로 파편화된 여러개의 세그먼트를 관리하기 때문에 메모리 사용량이 증가할 수 있습니다.
  - 맵 전체에 영향을 주는 연산(size(), clear() 등)은 여전히 전체 잠금이 필요할 수 있고, 이러한 부분에서는 동시성이 제한될 수 있습니다.
  - 내부 구현이 복잡합니다.

---

#### Atomic 객체
AtomicInteger, AtomicLong 등 `java.util.concurrent.atomic`패키지의 객체들은
멀티스레드 환경에서 안전하게 사용할 수 있는 원자적(atomic) 연산을 제공하는 클래스들입니다.

대부분의 Atomic 클래스는 CAS(Compare-And-Swap) 알고리즘을 기반으로 동작합니다.
```text
**CAS 알고리즘 (Compare-And-Swap)**
1. CAS 알고리즘은 현재 값을 예상 값과 비교하고, 일치하는 경우에만 새로운 값으로 업데이트하는 원자적 연산입니다.
2. 만약 현재 값이 예상 값과 다르다면 업데이트를 실패하고, 다시 시도합니다.
3. 이러한 방식으로 잠금(lock) 없이 스레드 안전성을 보장합니다.
```

```java
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class CountDownLatchRaceCondition {

    private static AtomicInteger sharedValue = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    // AtomicInteger의 incrementAndGet() 메서드를 사용하여 원자적으로 값을 증가
                    sharedValue.incrementAndGet();
                }
                latch.countDown();
            }).start();
        }

        // 모든 스레드가 작업을 완료할 때까지 대기
        latch.await();

        System.out.println("Final sharedValue: " + sharedValue.get());
        // 예상 결과: 5000 (threadCount * 1000)
        // 실제 결과: 항상 5000 (AtomicInteger로 경합 조건 해결)
    }
}
```
- **장점**
  - CAS 알고리즘을 사용한 동시성 제어 방식은 Lock을 획득하는 일반적인 방식에 비해 높은 성능을 제공합니다.
  - Lock 획득을 위한 잠금 경합(lock contention)을 피하고 스레드간 대기 시간을 줄여줍니다.
  - 잠금 객체를 별도로 관리할 필요 없이 Atomic 객체의 기능 활용으로 동시성 제어를 구현할 수 있기 때문에 간결한 코드를 작성할 수 있고 유지보수에 유리한 코드를 작성할 수 있습니다.
- **단점**
  - CAS 알고리즘은 업데이트가 실패할 경우 다시 시도해야 하므로, 경쟁이 심한 환경에서는 CAS 루프가 발생하여 성능이 저하될 수 있습니다.
  - Atomic 객체는 단순한 원자적 연산만 제공하므로, 복잡한 동기화가 필요한 경우에는 다른 동시성 제어 메커니즘을 사용해야 합니다.





