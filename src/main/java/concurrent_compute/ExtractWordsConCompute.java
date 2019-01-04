package concurrent_compute;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.Jedis;
import seg.Segment;
import util.FileUtils;
import util.HanUtils;
import java.util.*;
import static config.Config.*;
import static config.Config.REDIS_AUTH_PASSWORD;

/**
 * �������
 */
public class ExtractWordsConCompute extends ConCompute {
    private static List<String> extractResults = new ArrayList<>();

    @Override
    void preConsumer() {
        setThreadNum(SEG_THREAD_NUM);   // ���ò����߳���
        super.preConsumer();
        setNeedRedis(true);
    }

    @Override
    void doConcurrentUnitTask(Object msg, Jedis jedis) {
        if (msg instanceof String) {
            String text = (String) msg;
            String extract_result = Segment.extractWords(text, jedis);
            extractResults.add(extract_result);
            //  ���´�ӡ����ȥ����Ϊ��ָʾ����
            System.out.println(Thread.currentThread().getName() + " ����:" + CONCURRENT_COUNT.get() + "<-" + getQueueSize() +"->"+extract_result);
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
                transferQueue.add(textDS);
            }
        }
        setQueueSize(transferQueue.size());
        System.out.println("�������-----������Ϣ��" + transferQueue.size());
    }

    @Override
    void afterConsumer() {
        super.afterConsumer();
        executorService.shutdown();
        REDIS_POOL.destroy();
        // д����
        FileUtils.writeResultToFile(EXTRACT_OUTPUT, extractResults);    // �����д���ļ�
    }
}
