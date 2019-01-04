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
 * ������Ƶͳ��,��redis ��Ϊ�ⲿ�洢,�������¼������ڴ��ѹ��
 * Ȩ������Ϸ1-5����С˵�ı�,20���̺߳�ʱ  2069ms </>  ���߳���ʱ�� 2011 ms
 */
public class WordCountConCompute extends ConCompute {

    @Override
    void preConsumer() {
        setThreadNum(WC_THREAD_NUM);   // ���ò����߳���
        super.preConsumer();
        // ���redis���ݿ���ԭ�е�ֵ������ͳ�Ƴ���
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
        setQueueSize(transferQueue.size());   // QUEUE_SIZE = transferQueue.size();   // ��ֵ
    }

    private void produceNovel(String inputPath) {
        // ��ȡС˵�ı�
        String novel = FileUtils.readFileToString(inputPath);
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(novel);  // ȥ���������ַ�   ���û�ж���
        // �ٲ��ͣ�ô�
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // ����û�ж���
            if (StringUtils.isNotBlank(textDS) && textDS.length() != 1) {
                transferQueue.add(textDS);   //  ��Ԥ����̾���벢������
            }
        }
        System.out.println("�������������е�����" + transferQueue.size());
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
                            transferQueue.add(textDS);   //  ��Ԥ����̾���벢������
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
        System.out.println("�ܹ���ѡ�ʴ�" + Constants.wcMap.size());
        System.out.println( wordCountConCompute.getThreadNum()+ "���߳� �ܺ�ʱ" + (System.currentTimeMillis() - t1) + " ms");
    }
}
