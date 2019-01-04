package concurrent_compute;

import config.Constants;
import org.apache.commons.lang.StringUtils;
import pojo.SegMsg;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import util.FileUtils;
import util.HanUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeMap;

import static config.CommonValue.segTotalCount;
import static config.Config.*;
import static config.Constants.*;

/**
 * 并发词频统计,以redis 作为外部存储,大数据下减轻了内存的压力
 * 权利的游戏1-5完整小说文本,20个线程耗时  2069ms </>  单线程跑时间 2011 ms
 */
public class WordCountConCompute extends ConCompute {

    @Override
    void preConsumer() {
        setThreadNum(WC_THREAD_NUM);   // 设置并发线程数
        super.preConsumer();
        // 清空redis数据库中原有的值，避免统计出错
        Jedis jedis = new Jedis(REDIS_HOST);
        jedis.auth(REDIS_AUTH_PASSWORD);
        jedis.del(REDIS_WC_KEY);
        jedis.close();
    }

    @Override
    void doConcurrentUnitTask(Object msg) {
        if (msg instanceof String) {
            String segment = (String) msg;
            HanUtils.FMMSegment(segment, true);
        }
    }

    @Override
    public void produce() {
        if (CHINA_DAILY_TEST) {
            produceChinaDaily(CORPUS_INPUT_PATH);
        } else {
            produceNovel(NOVEL_INPUT_PATH);
        }
        setQueueSize(transferQueue.size());   // QUEUE_SIZE = transferQueue.size();   // 赋值
    }

    private void produceNovel(String inputPath) {
        // 读取小说文本
        String novel = FileUtils.readFileToString(inputPath);
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(novel);  // 去掉非中文字符   里边没有逗号
        // 再拆分停用词
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // 这里没有逗号
            if (StringUtils.isNotBlank(textDS) && textDS.length() != 1) {
                transferQueue.add(textDS);   //  将预处理短句加入并发操作
            }
        }
        System.out.println("生产后阻塞队列的数量" + transferQueue.size());
    }


    public void produceChinaDaily(String inputPath) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), "utf-8"))) {
            String str = null;
            while ((str = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(str)) {
                    String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(str);
                    for (int i = 0; i < replaceNonChinese.length; i++) {
                        String textDS = replaceNonChinese[i];
                        if (StringUtils.isNotBlank(textDS)) {
                            transferQueue.add(textDS);   //  将预处理短句加入并发操作
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        CHINA_DAILY_TEST = false;
        WordCountConCompute wordCountConCompute = new WordCountConCompute();
        REDIS_POOL.destroy();
        wordCountConCompute.compute();
        System.out.println("总共候选词串" + Constants.wcMap.size());
        System.out.println( wordCountConCompute.getThreadNum()+ "个线程 总耗时" + (System.currentTimeMillis() - t1) + " ms");
    }
}
