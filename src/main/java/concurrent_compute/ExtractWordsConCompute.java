package concurrent_compute;

import org.apache.commons.lang.StringUtils;
import pojo.LineMsg;
import redis.clients.jedis.Jedis;
import seg.Segment;
import util.FileUtils;
import util.HanUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static config.Config.*;
import static config.Config.REDIS_AUTH_PASSWORD;

/**
 * 并发抽词
 */
public class ExtractWordsConCompute extends ConCompute {
    //private static List<String> extractResults = new Vector<>();
    private static Map<Integer, String> extractResults = new ConcurrentHashMap();

    @Override
    void preConsumer() {
        setThreadNum(SEG_THREAD_NUM);   // 设置并发线程数
        super.preConsumer();
        setNeedRedis(true);
    }

    @Override
    void doConcurrentUnitTask(Object msg, Jedis jedis) {
        if (msg instanceof LineMsg) {

            LineMsg lineMsg = (LineMsg) msg;
            String text = lineMsg.text;
            String extract_result = Segment.extractWords(text, jedis);
            //extractResults.add(extract_result);
            extractResults.put(lineMsg.lineCount, text);
            //  以下打印可以去掉，为了指示进度
            System.out.println(Thread.currentThread().getName() + " 消费:" + CONCURRENT_COUNT.get() + "<-" + getQueueSize() + "->" + extract_result);
        }
    }

    @Override
    public void produce() {
        Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT, Integer.MAX_VALUE);
        jedis.auth(REDIS_AUTH_PASSWORD);
        // 读取小说文本
        String novel = FileUtils.readFileToString(NOVEL_INPUT_PATH);
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(novel);  // 去掉非中文字符   里边没有逗号
        // 再拆分停用词
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // 这里没有逗号
            if (StringUtils.isNotBlank(textDS) && textDS.length() != 1) {
                LineMsg lineMsg = new LineMsg(i, textDS);
                transferQueue.add(lineMsg);
            }
        }
        setQueueSize(transferQueue.size());
        // setQueueSize(10000);
        System.out.println("并发抽词-----生产消息数" + transferQueue.size());
    }

    @Override
    void afterConsumer() {
        super.afterConsumer();
        //executorService.shutdown();
        //REDIS_POOL.destroy();
        // 写入结果
        List<Map.Entry<Integer, String>> list = new ArrayList<>(extractResults.entrySet());
        //升序排序
        Collections.sort(list, Comparator.comparing(Map.Entry::getKey));
        FileUtils.writeMapResultToFile(EXTRACT_OUTPUT, list);    // 将结果写入文件
       // FileUtils.writeResultToFile(EXTRACT_OUTPUT, extractResults);    // 将结果写入文件
    }
}
