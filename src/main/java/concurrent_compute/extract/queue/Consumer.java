package concurrent_compute.extract.queue;


import config.Constants;
import pojo.LineMsg;
import redis.clients.jedis.Jedis;
import seg.Segment;
import util.FileUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static concurrent_compute.extract.queue.Constans.*;
import static config.Config.*;

public class Consumer {
    private static MyBlockingQueue blockingQueue = new MyBlockingQueue();
    private static AtomicInteger COUNT = new AtomicInteger(0);

    //  消费后的数据存hashMap
    public static void consumer() {
        for (int i = 0; i < SEG_THREAD_NUM; i++) {    // 开了20个线程
            executorService.submit(() -> {
                try (Jedis jedis = pool.getResource()) {
                    jedis.auth(REDIS_AUTH_PASSWORD);
                    while (COUNT.incrementAndGet() <= Constants.QUEUE_SIZE) {
                        LineMsg lineMsg = blockingQueue.consume();
                        String extract_result = Segment.extractWords(lineMsg.text, jedis);
                        Constans.EXTRACT_WORD_MAP.put(lineMsg.lineCount, extract_result);
                        System.out.println(Thread.currentThread().getName() + " 消费:" + lineMsg.lineCount+":"+ lineMsg.text + "->" + extract_result);
                    }
                    jedis.close();
                    countDownLatch.countDown();
                }
            });
        }
    }

    public static void compute() {
        Producer.produceNovel();
        Constants.QUEUE_SIZE  = MyBlockingQueue.fairQueue.size();
        System.out.println("生产的消息的数量:" + MyBlockingQueue.fairQueue.size());
        long t1 = System.currentTimeMillis();
        Consumer.consumer();
        try {
            countDownLatch.await();
            System.out.println("开的" + SEG_THREAD_NUM + "个子线程全部完成");
            System.out.println("结果Map中存放的数量:" + Constans.EXTRACT_WORD_MAP.size());
            executorService.shutdown();
            pool.destroy();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //  将最终结果写入文件中
        //  将map  排序后写入结果
        List<Map.Entry<Integer,String>> list = new ArrayList<>(Constans.EXTRACT_WORD_MAP.entrySet());
        //升序排序
        Collections.sort(list, Comparator.comparing(Map.Entry::getKey));
        FileUtils.writeMapResultToFile(EXTRACT_OUTPUT,list);
        System.out.println("总计耗时:  " + (System.currentTimeMillis() - t1) + " ms");
    }
/*    public static void main(String[] args) {
        Producer.produceNovel();
        Constants.QUEUE_SIZE  = MyBlockingQueue.fairQueue.size();
        System.out.println("生产的消息的数量:" + MyBlockingQueue.fairQueue.size());
        long t1 = System.currentTimeMillis();
        Consumer.consumer();
        try {
            countDownLatch.await();
            System.out.println("开的" + SEG_THREAD_NUM + "个子线程全部完成");
            System.out.println("结果Map中存放的数量:" + Constans.EXTRACT_WORD_MAP.size());
            executorService.shutdown();
            pool.destroy();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //  将最终结果写入文件中
        //  将map  排序后写入结果
        List<Map.Entry<Integer,String>> list = new ArrayList<>(Constans.EXTRACT_WORD_MAP.entrySet());
        //升序排序
        Collections.sort(list, Comparator.comparing(Map.Entry::getKey));
        FileUtils.writeMapResultToFile(EXTRACT_OUTPUT,list);
        System.out.println("总计耗时:  " + (System.currentTimeMillis() - t1) + " ms");
    }*/
}
