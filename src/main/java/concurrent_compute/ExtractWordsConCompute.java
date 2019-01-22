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
 * �������
 */
public class ExtractWordsConCompute extends ConCompute {
    //private static List<String> extractResults = new Vector<>();
    private static Map<Integer, String> extractResults = new ConcurrentHashMap();

    @Override
    void preConsumer() {
        setThreadNum(SEG_THREAD_NUM);   // ���ò����߳���
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
            //  ���´�ӡ����ȥ����Ϊ��ָʾ����
            System.out.println(Thread.currentThread().getName() + " ����:" + CONCURRENT_COUNT.get() + "<-" + getQueueSize() + "->" + extract_result);
        }
    }

    @Override
    public void produce() {
        Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT, Integer.MAX_VALUE);
        jedis.auth(REDIS_AUTH_PASSWORD);
        // ��ȡС˵�ı�
        String novel = FileUtils.readFileToString(NOVEL_INPUT_PATH);
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(novel);  // ȥ���������ַ�   ���û�ж���
        // �ٲ��ͣ�ô�
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // ����û�ж���
            if (StringUtils.isNotBlank(textDS) && textDS.length() != 1) {
                LineMsg lineMsg = new LineMsg(i, textDS);
                transferQueue.add(lineMsg);
            }
        }
        setQueueSize(transferQueue.size());
        // setQueueSize(10000);
        System.out.println("�������-----������Ϣ��" + transferQueue.size());
    }

    @Override
    void afterConsumer() {
        super.afterConsumer();
        //executorService.shutdown();
        //REDIS_POOL.destroy();
        // д����
        List<Map.Entry<Integer, String>> list = new ArrayList<>(extractResults.entrySet());
        //��������
        Collections.sort(list, Comparator.comparing(Map.Entry::getKey));
        FileUtils.writeMapResultToFile(EXTRACT_OUTPUT, list);    // �����д���ļ�
       // FileUtils.writeResultToFile(EXTRACT_OUTPUT, extractResults);    // �����д���ļ�
    }
}
