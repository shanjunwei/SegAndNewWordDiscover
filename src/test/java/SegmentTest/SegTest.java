package SegmentTest;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static config.Config.*;
import static config.Constants.REDIS_WC_KEY;
import static config.Constants.wcMap;

/**
 * �ִʲ�����
 */
public class SegTest {

    public static void main(String[] args) {
        //Constants.NovelTest = true;
        //testSingleSentenceSeg(args);
        //testRepeatRegx(args);
        //testCalculationAndSerializationToFile();    //  ���㲢���л����ļ�
        //testDebugByFileLine("H:\\С˵\\�������֮�衷ȫ��.txt",100);   // debug
        //testDebugByFileLine(Config.ErrorSegPath,600);
        //testAllChineseSeg(args);
        //saveCalculateResultToRedis();    // �������л���������redis
        //testExtractWords(args);   // ���Գ��
        //testSerializateTrieToFile();
        //testRediSave(args);
        //testExtractWord(args);
        //testExtractWords(args);
        //testRedisWordCount();
        //testCommonWordCount();
        testConCompute();
    }

    public static void testConCompute() {
        long t1 = System.currentTimeMillis();
        CHINA_DAILY_TEST = true;
        //  ��Ƶͳ�� ���������ڴ�
        WordCountConCompute wordCountConCompute = new WordCountConCompute();
        wordCountConCompute.compute();
        System.out.println("  ��Ƶͳ���� wcMap ������:" + wcMap.size());
        // ��������ͳ����
        MIERConCompute mierConCompute = new MIERConCompute();
        mierConCompute.compute();
        System.out.println("�ܺ�ʱ" + (System.currentTimeMillis() - t1) + " ms");
    }

    public static void testCommonWordCount() {
/*        long  t1  = System.currentTimeMillis();
        PreProcess preProcess   = new PreProcess();
        preProcess.initNovel();
        System.out.println("�ܹ���ѡ�ʴ�"+Constants.wcMap.size());
        System.out.println("�ܺ�ʱ"+(System.currentTimeMillis()-t1) +" ms");*/
        Jedis jedis = new Jedis(REDIS_HOST, 6379, 100000);
        jedis.auth(REDIS_AUTH_PASSWORD);
//        jedis.zrevrangeWithScores(REDIS_WC_KEY, 0, 200000).forEach(it -> {
//            System.out.println(it.getElement() + " -> " + it.getScore());
//        });

        System.out.println(jedis.zrevrangeWithScores(REDIS_WC_KEY, 0, 4000000).size());
        jedis.close();
    }


    /**
     * ���Ե�������
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
     * ���� ���� ����ƽ ��ƽ
     */
    public static void testRepeatRegx(String[] args) {
        String regEx = "��ƽ";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(args[0]);
        while (m.find()) {
            String find = m.group();
            System.out.println(find + " [" + m.start() + "-" + m.end() + "]");
        }
    }

    /**
     * ���� ֮ǰ�ִ��ǰ1000�в����������Ϣ
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
     * ���� �����������redis
     */
    public static void saveCalculateResultToRedis() {
//        Constants.NovelTest = true;
//        Config.NovelPath = "D:\\HanLP\\novel\\�����˲�.txt";
        //Constants.redis.flushAll();    // ��ˢ��֮ǰ���н��
        JsonSerializationUtil.saveCalculateResultToRedis();
        //System.out.println(Constants.redis.hgetAll("�Ӵ�"));
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
        Constants.NovelTest = true;
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
