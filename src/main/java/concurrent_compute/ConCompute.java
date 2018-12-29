package concurrent_compute;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static config.Config.*;

/**
 * 并发计算
 */
public abstract class ConCompute {
    private int threadNum = 0;  // 线程数量
    private int queueSize = 0;  // 阻塞队列生产结束后堆积的消息数量
    private boolean needRedis = false;  //默认不使用redis
    public static final JedisPool REDIS_POOL = new JedisPool(new JedisPoolConfig(), REDIS_HOST, 6379, 30);  // 不能在多个线程中使用一个redis实例
    public static LinkedTransferQueue transferQueue = new LinkedTransferQueue(); // 阻塞队列,用来实现并发加速框架
    public static AtomicInteger CONCURRENT_COUNT = new AtomicInteger(0);  // 控制并发线程工作量
    public ExecutorService executorService = Executors.newCachedThreadPool();    //线程池
    public CountDownLatch countDownLatch;  // 栅栏，控制并发线程全部结束之后才返回主线程

    /**
     * 生产前置操作
     */
    void preProduce() {
    }

    /**
     * 生产
     */
    void produce() {
    }

    /**
     * 消费前置操作
     */
    void preConsumer() {
        countDownLatch = new CountDownLatch(threadNum);  //栅栏，控制并发线程全部结束之后才返回主线程
    }

    /**
     * 消费
     */
    void consumer() {
        for (int i = 0; i < WC_THREAD_NUM; i++) {
            executorService.submit(() -> {
                if (needRedis) {
                    try (Jedis jedis = REDIS_POOL.getResource()) {
                        jedis.auth(REDIS_AUTH_PASSWORD);
                        while (CONCURRENT_COUNT.incrementAndGet() <= getQueueSize()) {
                            Object msg = transferQueue.poll();
                            doConcurrentUnitTask(msg, jedis);
                        }
                        countDownLatch.countDown();
                    }
                } else {
                    while (CONCURRENT_COUNT.incrementAndGet() <= getQueueSize()) {
                        Object msg = transferQueue.poll();
                        doConcurrentUnitTask(msg);
                    }
                    countDownLatch.countDown();
                }

            });
        }
        // }
    }

    /**
     * 并发操作的基础任务，这个部分的代码，包括引用的第三方必须是线程安全的
     */
    void doConcurrentUnitTask(Object msg, Jedis jedis) {

    }

    /**
     * 并发操作的基础任务，这个部分的代码，包括引用的第三方必须是线程安全的
     */
    void doConcurrentUnitTask(Object msg) {

    }

    /**
     * 消费后置操作
     */
    void afterConsumer() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
        REDIS_POOL.destroy();
        CONCURRENT_COUNT.set(0);    //计数器重新置零
    }

    /**
     * 并发计算
     */
    void compute() {
        long t1 = System.currentTimeMillis();
        preProduce();   // 前置操作
        produce();
        preConsumer();  // 消费前置操作
        consumer();
        afterConsumer();
        System.out.println("并发计算总计耗时:  " + (System.currentTimeMillis() - t1) + " ms");
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public boolean isNeedRedis() {
        return needRedis;
    }

    public void setNeedRedis(boolean needRedis) {
        this.needRedis = needRedis;
    }
}
