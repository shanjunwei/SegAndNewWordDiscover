package concurrent_compute;
import pojo.SegMsg;
import pojo.Term;
import redis.clients.jedis.Jedis;
import util.HanUtils;
import java.util.Map;
import java.util.TreeMap;

import static config.CommonValue.segTotalCount;
import static config.Config.*;
import static config.Constants.*;

public class MIERConCompute extends ConCompute {
    @Override
    void preConsumer() {
        setThreadNum(WC_THREAD_NUM);   // ���ò����߳���
        super.preConsumer();
        Jedis jedis = REDIS_POOL.getResource();
        jedis.auth(REDIS_AUTH_PASSWORD);
        setNeedRedis(true);
//        jedis.zrevrangeWithScores(REDIS_WC_SINGLEWORD_KEY, 0, 4000000).forEach(it -> {   //  �����������±�
//            singWordCountMap.put(it.getElement(), (int) it.getScore());
//        });
        //setQueueSize(3271991);
    }

    @Override
    void doConcurrentUnitTask(Object msg, Jedis jedis) {
        if (msg instanceof SegMsg) {
            SegMsg seg = (SegMsg) msg;
            //  �������㻥��Ϣ
            float mi = ConCalculateUtil.computeMutualInformation(seg.seg, jedis);
            // ������������������Ϣ��
            float rightEntropy = ConCalculateUtil.computeRightEntropy(seg.seg, seg.count);
            // maxRE = Math.max(maxRE, rightEntropy);  // ���������Ϣ��   //totalRE = totalRE + rightEntropy;
            float leftEntropy = ConCalculateUtil.computeLeftEntropy(seg.seg, seg.count);
            Term term = new Term(seg.seg, seg.count, mi, leftEntropy, rightEntropy);  // ����û�취�����÷�
            /**********************  redis��ȡ **************************/
//            synchronized (jedis){
//                jedis.hmset(seg.seg, term.convertToMap());
//            }
            /**********************  redis��ȡ **************************/
            System.out.println(Thread.currentThread().getName() + " ����:" + CONCURRENT_COUNT.get() + "->" + getQueueSize());
        }
    }

    @Override
    public void produce() {
        Jedis jedis = REDIS_POOL.getResource();
        jedis.auth(REDIS_AUTH_PASSWORD);
        TreeMap<String, Integer> rightTreeMap = new TreeMap();
        TreeMap<String, Integer> leftTreeMap = new TreeMap();
        int count = 0;
        //  ���������ϵĴ�Ƶ
       /* Set<Tuple> set = jedis.zrevrangeWithScores(REDIS_WC_KEY, 0, Long.MAX_VALUE);
        for (Tuple it : set) {
            SegMsg segMsg = new SegMsg(it.getElement(), (int) it.getScore());
            if (it.getElement().length() > 1) {
                transferQueue.add(segMsg);   // ͳ�����������ϵĺ�ѡ�ִ��Ļ���Ϣ��
                segTotalCount += it.getScore();
            }
            rightTreeMap.put(it.getElement(), (int) it.getScore());
            leftTreeMap.put(HanUtils.reverseString(it.getElement()), (int) it.getScore());
            // jedis.set(String.valueOf(it.getElement()), String.valueOf((int) it.getScore()));   //  ����Ƶͳ�ƽ����redis
            count++;
            if (count % 100000 == 0) {
                System.out.print("*");
            }
            if (count == 100000 * 100) {
                System.out.println();
                count = 0;
                continue;
            }
        }*/
        //  ��Ƶͳ�ƽ�������ڴ��ϣ����
        for (Map.Entry<String, Integer> entry : wcMap.entrySet()) {
            String seg = entry.getKey();
            int frequency = entry.getValue();
            SegMsg segMsg = new SegMsg(seg, frequency);
            if (seg.length() > 1) {
                transferQueue.add(segMsg);   // ͳ�����������ϵĺ�ѡ�ִ��Ļ���Ϣ��
                segTotalCount += frequency;
            }
            rightTreeMap.put(seg, frequency);
            leftTreeMap.put(HanUtils.reverseString(seg), frequency);
            // jedis.set(String.valueOf(it.getElement()), String.valueOf((int) it.getScore()));   //  ����Ƶͳ�ƽ����redis
            count++;
            if (count % 100000 == 0) {
                System.out.print("*");
            }
            if (count == 100000 * 100) {
                System.out.println();
                count = 0;
                continue;
            }
        }
        jedis.close();
        //  �����ֵ���Ӧ���⶯̬����
        trieRight.build(rightTreeMap);
        trieLeft.build(leftTreeMap);
        // Java ���������������Ҫ�ֶ�����ڴ棬����ִ�����ᱻGC   //leftTreeMap.clear();   //rightTreeMap.clear();
        setQueueSize(transferQueue.size());
    }

    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        CHINA_DAILY_TEST = false;
        //  ��Ƶͳ�� ���������ڴ�
        WordCountConCompute wordCountConCompute = new WordCountConCompute();
        wordCountConCompute.compute();
        // ��������ͳ����
        MIERConCompute mierConCompute = new MIERConCompute();
        mierConCompute.compute();
        System.out.println("�ܺ�ʱ" + (System.currentTimeMillis() - t1) + " ms");
    }

}
