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
    public static final ExecutorService executorService = Executors.newCachedThreadPool();   // �̳߳�
    private static AtomicInteger COUNT = new AtomicInteger(0);
    public static final CountDownLatch countDownLatch = new CountDownLatch(Config.WC_THREAD_NUM);  // դ�������Ʋ����߳�ȫ������֮��ŷ������߳�


    //  ���Ѻ�����ݴ�hashMap
    public static void consumer() {
        for (int i = 0; i < WC_THREAD_NUM; i++) {    // ����20���߳�
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

       /* Producer.produceNovel("D:\\HanLP\\novel\\�����˲�.txt");
        Consumer.consumer();
        try {
            countDownLatch.await();
            System.out.println("����" + WC_THREAD_NUM + "����Ƶͳ�����߳�ȫ�����");
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

        System.out.println("�ܼƺ�ʱ:  " + (System.currentTimeMillis() - t1) + " ms");
    }
}
