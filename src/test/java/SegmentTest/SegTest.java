package SegmentTest;

import concurrent_compute.ExtractWordsConCompute;
import concurrent_compute.MIERConCompute;
import concurrent_compute.WordCountConCompute;
import config.Config;
import config.Constants;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.Jedis;
import seg.Segment;
import serilize.JsonSerializationUtil;
import java.io.*;
import java.util.List;
import static config.Config.*;
import static config.Constants.*;

/**
 * �ִʲ�����
 */
public class SegTest {

    public static void main(String[] args) {
        //ConCompute();
        Compute();
        //ConExtractWords();
        //Constants.NovelTest = true;
        //testSingleSentenceSeg(args);
        //testRepeatRegx(args);
        //testDebugByFileLine("H:\\С˵\\�������֮�衷ȫ��.txt",100);   // debug
        //testDebugByFileLine(Config.ErrorSegPath,600);
        //testAllChineseSeg(args);
        //saveCalculateResultToRedis();    // �������л���������redis
        //testExtractWords(args);   // ���Գ��
        //testSerializateTrieToFile();
        //testRediSave(args);
        //testExtractWord(args);
        //testExtractWords(args);
        //testCalculationAndSerializationToFile();    //  ���㲢���л����ļ�
        //testRedisWordCount();
        //testCommonWordCount();
    }

    /**
     * ����ͳ��������--���߳� ��Ƶͳ���Լ�����
     */
    public static void ConExtractWords() {
        long t1 = System.currentTimeMillis();
        ExtractWordsConCompute  extractWordsConCompute = new ExtractWordsConCompute();
        extractWordsConCompute.compute();
        System.out.println("�ܺ�ʱ" + (System.currentTimeMillis() - t1) + " ms");
    }
    /**
     * ����ͳ��������--���߳� ��Ƶͳ���Լ�����
     */
    public static void ConCompute() {
        long t1 = System.currentTimeMillis();
        //  ��Ƶͳ�� ���������ڴ�
        WordCountConCompute wordCountConCompute = new WordCountConCompute();
        wordCountConCompute.compute();
        System.out.println("  ��Ƶͳ���� wcMap ������:" + wcMap.size());
        // ��������ͳ����
        MIERConCompute mierConCompute = new MIERConCompute();
        mierConCompute.compute();
        System.out.println("�ܺ�ʱ" + (System.currentTimeMillis() - t1) + " ms");
    }

    /**
     * ͳ��������-- ���߳� ��Ƶͳ�������
     */
    public static void Compute() {
        JsonSerializationUtil.saveCalculateResultToRedis();
    }
    /**
     * ���ԶԵ������ӽ��г��
     */
    public static void testSingleSentenceSeg(String[] args) {
        if (args.length < 1) System.exit(0);
        String test_text = args[0];     // �����ı�ͨ����׼���봫��
        if (StringUtils.isBlank(test_text)) System.exit(0);
        //  ����Ϣ�� ����Ϣ��ͳ�������뵽���˾��߻�����
        Segment segment = new Segment();
        Config.DEBUG_MODE = true;
        List<String> result = segment.segment(test_text);
        System.out.println("\n*************************�ִʽ����" + result + "*************************\n");
    }
    /**
     * ���Դ����ִַ�
     */
    public static void testAllChineseSeg(String[] args) {
        if (args.length < 1) System.exit(0);
        String test_text = args[0];     // �����ı�ͨ����׼���봫��
        if (StringUtils.isBlank(test_text)) System.exit(0);
        //  ����Ϣ�� ����Ϣ��ͳ�������뵽���˾��߻�����
        Segment segment = new Segment();
        //NovelTest = true;
        Config.DEBUG_MODE = true;

        System.out.println("\n*************************�ִʽ����->" + segment.segmentWithAllChinese(test_text) + "<-*************************\n");
    }

    /**
     * ����������ı����г��
     *  Line  ���Ʋ��������ı�������
     */
    public static void testDebugByFileLine(String inputPath, int line) {
        Config.DEBUG_MODE = true;
        int count = 0;
        Segment segment = new Segment();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), "utf-8"))) {
            String text;
            while ((text = reader.readLine()) != null && count < line) {
                if (StringUtils.isNotBlank(text)) {
                    List<String> result = segment.segment(text);
                    count++;
                    System.out.println("\n*************************�ִʽ����" + result + "*************************\n");
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * ���Գ��
     */
    public static void testExtractWords(String[] args) {
        Config.DEBUG_MODE = true;
        Segment segment = new Segment();
        Jedis redis = new Jedis("localhost");
        redis.auth("root");
        //System.out.println("��ʽ��----->" + segment.extractWords(args[0]) + "<---");
    }
    /**
     * ���� redis ��ȡ
     */
    public static void testRediSave(String[] args) {
        Jedis jedis = new Jedis(REDIS_HOST);
        jedis.auth(REDIS_AUTH_PASSWORD);
        //System.out.println(jedis.hgetAll(args[0]));
        System.out.println(jedis.get(args[0]));
    }
    /**
     * ���� ���
     */
    public static void testExtractWord(String[] args) {
        Config.DEBUG_MODE = true;
        Segment segment = new Segment();
        //System.out.println("��ʽ��----->" + segment.extractWords(args[0]) + "<---");
    }


    /**
     * ����redis�Ĵ�Ƶͳ��
     */
    public static void testRedisWordCount() {
        //  long t1 = System.currentTimeMillis();
        Jedis jedis = new Jedis(REDIS_HOST);
        jedis.auth(REDIS_AUTH_PASSWORD);
        jedis.zrevrangeWithScores(REDIS_WC_KEY, 0, 200000).forEach(it -> {
            System.out.println(it.getElement() + " -> " + it.getScore());
        });
        //  System.out.println("�ܼƺ�ʱ:  " + (System.currentTimeMillis() - t1) + " ms");
    }


}
