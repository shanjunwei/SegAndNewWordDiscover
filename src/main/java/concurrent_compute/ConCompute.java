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
 * ��������
 */
public abstract class ConCompute {
    private int threadNum = 0;  // �߳�����
    private int queueSize = 0;  // ������������������ѻ�����Ϣ����
    private boolean needRedis = false;  //Ĭ�ϲ�ʹ��redis
    public static final JedisPool REDIS_POOL = new JedisPool(new JedisPoolConfig(), REDIS_HOST, 6379, 30);  // �����ڶ���߳���ʹ��һ��redisʵ��
    public static LinkedTransferQueue transferQueue = new LinkedTransferQueue(); // ��������,����ʵ�ֲ������ٿ��
    public static AtomicInteger CONCURRENT_COUNT = new AtomicInteger(0);  // ���Ʋ����̹߳�����
    public ExecutorService executorService = Executors.newCachedThreadPool();    //�̳߳�
    public CountDownLatch countDownLatch;  // դ�������Ʋ����߳�ȫ������֮��ŷ������߳�

    /**
     * ����ǰ�ò���
     */
    void preProduce() {
    }

    /**
     * ����
     */
    void produce() {
    }

    /**
     * ����ǰ�ò���
     */
    void preConsumer() {
        countDownLatch = new CountDownLatch(threadNum);  //դ�������Ʋ����߳�ȫ������֮��ŷ������߳�
    }

    /**
     * ����
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
     * ���������Ļ�������������ֵĴ��룬�������õĵ������������̰߳�ȫ��
     */
    void doConcurrentUnitTask(Object msg, Jedis jedis) {

    }

    /**
     * ���������Ļ�������������ֵĴ��룬�������õĵ������������̰߳�ȫ��
     */
    void doConcurrentUnitTask(Object msg) {

    }

    /**
     * ���Ѻ��ò���
     */
    void afterConsumer() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
        REDIS_POOL.destroy();
        CONCURRENT_COUNT.set(0);    //��������������
    }

    /**
     * ��������
     */
    void compute() {
        long t1 = System.currentTimeMillis();
        preProduce();   // ǰ�ò���
        produce();
        preConsumer();  // ����ǰ�ò���
        consumer();
        afterConsumer();
        System.out.println("���������ܼƺ�ʱ:  " + (System.currentTimeMillis() - t1) + " ms");
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
