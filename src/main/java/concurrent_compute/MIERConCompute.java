package concurrent_compute;

import computer.Occurrence;
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
        setThreadNum(COMPUTE_THREAD_NUM);   // 设置并发线程数
        super.preConsumer();
        Jedis jedis = REDIS_POOL.getResource();
        jedis.auth(REDIS_AUTH_PASSWORD);
        setNeedRedis(true);
    }

    @Override
    void doConcurrentUnitTask(Object msg, Jedis jedis) {
        if (msg instanceof SegMsg) {
            SegMsg seg = (SegMsg) msg;
            //  并发计算互信息
            float mi = ConCalculateUtil.computeMutualInformation(seg.seg, jedis);
            // 并发计算左右邻字信息熵
            float rightEntropy = ConCalculateUtil.computeRightEntropy(seg.seg, seg.count);
            // maxRE = Math.max(maxRE, rightEntropy);  // 求最大右信息熵   //totalRE = totalRE + rightEntropy;
            float leftEntropy = ConCalculateUtil.computeLeftEntropy(seg.seg, seg.count);
            Term term = new Term(seg.seg, seg.count, mi, leftEntropy, rightEntropy);  // 这里没办法算最后得分
            /**********************  redis存取 **************************/
//            synchronized (jedis){
            jedis.hmset(seg.seg, term.convertToMap());
//            }
            /**********************  redis存取 **************************/
            System.out.println(Thread.currentThread().getName() + " 消费:" + CONCURRENT_COUNT.get() + "->" + getQueueSize() + "==" + term.toTotalString());
        }
    }

    public void computeAllSeg() {
        Jedis jedis = new Jedis(REDIS_HOST);
        jedis.auth(REDIS_AUTH_PASSWORD);
        singWordCountMap = wcMap;
        Occurrence occurrence = new Occurrence();
        occurrence.totalCount = segTotalCount;
        for (Map.Entry<String, Integer> entry : wcMap.entrySet()) {
            String segStr = entry.getKey();
            if (segStr.length() == 1) continue;

            int segCount = entry.getValue();
            //  并发计算互信息
            float mi = occurrence.computeMutualInformation(segStr);
            // 并发计算左右邻字信息熵
            float rightEntropy = occurrence.computeRightEntropy(segStr, segCount);
            // maxRE = Math.max(maxRE, rightEntropy);  // 求最大右信息熵   //totalRE = totalRE + rightEntropy;
            float leftEntropy = occurrence.computeLeftEntropy(segStr, segCount);
            Term term = new Term(segStr, segCount, mi, leftEntropy, rightEntropy);  // 这里没办法算最后得分
            /**********************  redis存取 **************************/
            System.out.println(term.toTotalString());
            jedis.hmset(segStr, term.convertToMap());
            /**********************  redis存取 **************************/
        }
        jedis.close();
    }


    @Override
    public void produce() {
        Jedis jedis = new Jedis(REDIS_HOST, 6379, Integer.MAX_VALUE);
        jedis.auth(REDIS_AUTH_PASSWORD);
        jedis.flushAll();
        TreeMap<String, Integer> rightTreeMap = new TreeMap();
        TreeMap<String, Integer> leftTreeMap = new TreeMap();
        int count = 0;
        //  两个字以上的词频
       /* Set<Tuple> set = jedis.zrevrangeWithScores(REDIS_WC_KEY, 0, Long.MAX_VALUE);
        for (Tuple it : set) {
            SegMsg segMsg = new SegMsg(it.getElement(), (int) it.getScore());
            if (it.getElement().length() > 1) {
                transferQueue.add(segMsg);   // 统计两个字以上的候选字串的互信息等
                segTotalCount += it.getScore();
            }
            rightTreeMap.put(it.getElement(), (int) it.getScore());
            leftTreeMap.put(HanUtils.reverseString(it.getElement()), (int) it.getScore());
            // jedis.set(String.valueOf(it.getElement()), String.valueOf((int) it.getScore()));   //  将词频统计结果放redis
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
        //  词频统计结果存在内存哈希表中
        for (Map.Entry<String, Integer> entry : wcMap.entrySet()) {
            String seg = entry.getKey();
            int frequency = entry.getValue();
            SegMsg segMsg = new SegMsg(seg, frequency);
            if (seg.length() > 1) {
                transferQueue.add(segMsg);   // 统计两个字以上的候选字串的互信息等
                segTotalCount += frequency;
            }
            rightTreeMap.put(seg, frequency);
            leftTreeMap.put(HanUtils.reverseString(seg), frequency);
            // jedis.set(String.valueOf(it.getElement()), String.valueOf((int) it.getScore()));   //  将词频统计结果放redis
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
        //  构造字典树应避免动态构造
        trieRight.build(rightTreeMap);
        trieLeft.build(leftTreeMap);
        // Java 集合容器用完后不需要手动清空内存，方法执行完后会被GC   //leftTreeMap.clear();   //rightTreeMap.clear();
        setQueueSize(transferQueue.size());
    }

    @Override
    void afterConsumer() {
        super.afterConsumer();
        executorService.shutdown();
        REDIS_POOL.destroy();
    }

    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        CHINA_DAILY_TEST = false;
        //  词频统计 保存结果到内存
        WordCountConCompute wordCountConCompute = new WordCountConCompute();
        wordCountConCompute.compute();
        System.out.println("  词频统计里 wcMap 的容量:" + wcMap.size());
        // 并发计算统计量
        MIERConCompute mierConCompute = new MIERConCompute();
        mierConCompute.compute();
        System.out.println("总耗时" + (System.currentTimeMillis() - t1) + " ms");
    }

}
