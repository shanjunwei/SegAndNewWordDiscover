package concurrent_compute;

import config.Config;
import config.Constants;
import redis.clients.jedis.Jedis;
import util.HanUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static config.Config.*;
import static config.Constants.*;

public class Consumer {
    public static final ExecutorService executorService = Executors.newCachedThreadPool();   // 线程池
    private static AtomicInteger COUNT = new AtomicInteger(0);
    public static final CountDownLatch countDownLatch = new CountDownLatch(Config.WC_THREAD_NUM);  // 栅栏，控制并发线程全部结束之后才返回主线程


    //  消费后的数据存hashMap
    public static void consumer() {
        for (int i = 0; i < WC_THREAD_NUM; i++) {    // 开了20个线程
            executorService.submit(() -> {
                try (Jedis jedis = Constants.REDIS_POOL.getResource()) {
                    jedis.auth("root");
                    while (COUNT.get() <= QUEUE_SIZE) {
                        String text = transferQueue.poll();
                        HanUtils.FMMAndSaveWCToRedis(text, jedis);
                    }
                    countDownLatch.countDown();
                }
            });
        }
    }


    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();

       /* Producer.produceNovel("D:\\HanLP\\novel\\天龙八部.txt");
        Consumer.consumer();
        try {
            countDownLatch.await();
            System.out.println("开的" + WC_THREAD_NUM + "个词频统计子线程全部完成");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        executorService.shutdown();
        REDIS_POOL.destroy();*/
        Jedis jedis = new Jedis(REDIS_HOST);
        jedis.auth(REDIS_AUTH_PASSWORD);
        jedis.zrevrangeWithScores(REDIS_WC_SINGLEWORD_KEY, 0, 200000).forEach(it -> {
            System.out.println(it + " -> " + it.getScore());
        });

        System.out.println("总计耗时:  " + (System.currentTimeMillis() - t1) + " ms");
    }
}
