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

    //  ���Ѻ�����ݴ�hashMap
    public static void consumer() {
        for (int i = 0; i < SEG_THREAD_NUM; i++) {    // ����20���߳�
            executorService.submit(() -> {
                try (Jedis jedis = pool.getResource()) {
                    jedis.auth(REDIS_AUTH_PASSWORD);
                    while (COUNT.incrementAndGet() <= Constants.QUEUE_SIZE) {
                        LineMsg lineMsg = blockingQueue.consume();
                        String extract_result = Segment.extractWords(lineMsg.text, jedis);
                        Constans.EXTRACT_WORD_MAP.put(lineMsg.lineCount, extract_result);
                        System.out.println(Thread.currentThread().getName() + " ����:" + lineMsg.lineCount+":"+ lineMsg.text + "->" + extract_result);
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
        System.out.println("��������Ϣ������:" + MyBlockingQueue.fairQueue.size());
        long t1 = System.currentTimeMillis();
        Consumer.consumer();
        try {
            countDownLatch.await();
            System.out.println("����" + SEG_THREAD_NUM + "�����߳�ȫ�����");
            System.out.println("���Map�д�ŵ�����:" + Constans.EXTRACT_WORD_MAP.size());
            executorService.shutdown();
            pool.destroy();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //  �����ս��д���ļ���
        //  ��map  �����д����
        List<Map.Entry<Integer,String>> list = new ArrayList<>(Constans.EXTRACT_WORD_MAP.entrySet());
        //��������
        Collections.sort(list, Comparator.comparing(Map.Entry::getKey));
        FileUtils.writeMapResultToFile(EXTRACT_OUTPUT,list);
        System.out.println("�ܼƺ�ʱ:  " + (System.currentTimeMillis() - t1) + " ms");
    }
/*    public static void main(String[] args) {
        Producer.produceNovel();
        Constants.QUEUE_SIZE  = MyBlockingQueue.fairQueue.size();
        System.out.println("��������Ϣ������:" + MyBlockingQueue.fairQueue.size());
        long t1 = System.currentTimeMillis();
        Consumer.consumer();
        try {
            countDownLatch.await();
            System.out.println("����" + SEG_THREAD_NUM + "�����߳�ȫ�����");
            System.out.println("���Map�д�ŵ�����:" + Constans.EXTRACT_WORD_MAP.size());
            executorService.shutdown();
            pool.destroy();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //  �����ս��д���ļ���
        //  ��map  �����д����
        List<Map.Entry<Integer,String>> list = new ArrayList<>(Constans.EXTRACT_WORD_MAP.entrySet());
        //��������
        Collections.sort(list, Comparator.comparing(Map.Entry::getKey));
        FileUtils.writeMapResultToFile(EXTRACT_OUTPUT,list);
        System.out.println("�ܼƺ�ʱ:  " + (System.currentTimeMillis() - t1) + " ms");
    }*/
}
