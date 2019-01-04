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
 * 分词测试类
 */
public class SegTest {

    public static void main(String[] args) {
        //ConCompute();
        Compute();
        //ConExtractWords();
        //Constants.NovelTest = true;
        //testSingleSentenceSeg(args);
        //testRepeatRegx(args);
        //testDebugByFileLine("H:\\小说\\《冰与火之歌》全集.txt",100);   // debug
        //testDebugByFileLine(Config.ErrorSegPath,600);
        //testAllChineseSeg(args);
        //saveCalculateResultToRedis();    // 重新序列化计算结果到redis
        //testExtractWords(args);   // 测试抽词
        //testSerializateTrieToFile();
        //testRediSave(args);
        //testExtractWord(args);
        //testExtractWords(args);
        //testCalculationAndSerializationToFile();    //  计算并序列化到文件
        //testRedisWordCount();
        //testCommonWordCount();
    }

    /**
     * 并发统计量计算--多线程 词频统计以及计算
     */
    public static void ConExtractWords() {
        long t1 = System.currentTimeMillis();
        ExtractWordsConCompute  extractWordsConCompute = new ExtractWordsConCompute();
        extractWordsConCompute.compute();
        System.out.println("总耗时" + (System.currentTimeMillis() - t1) + " ms");
    }
    /**
     * 并发统计量计算--多线程 词频统计以及计算
     */
    public static void ConCompute() {
        long t1 = System.currentTimeMillis();
        //  词频统计 保存结果到内存
        WordCountConCompute wordCountConCompute = new WordCountConCompute();
        wordCountConCompute.compute();
        System.out.println("  词频统计里 wcMap 的容量:" + wcMap.size());
        // 并发计算统计量
        MIERConCompute mierConCompute = new MIERConCompute();
        mierConCompute.compute();
        System.out.println("总耗时" + (System.currentTimeMillis() - t1) + " ms");
    }

    /**
     * 统计量计算-- 单线程 词频统计与计算
     */
    public static void Compute() {
        JsonSerializationUtil.saveCalculateResultToRedis();
    }
    /**
     * 测试对单个句子进行抽词
     */
    public static void testSingleSentenceSeg(String[] args) {
        if (args.length < 1) System.exit(0);
        String test_text = args[0];     // 测试文本通过标准输入传入
        if (StringUtils.isBlank(test_text)) System.exit(0);
        //  将信息熵 互信息等统计量加入到过滤决策机制中
        Segment segment = new Segment();
        Config.DEBUG_MODE = true;
        List<String> result = segment.segment(test_text);
        System.out.println("\n*************************分词结果集" + result + "*************************\n");
    }
    /**
     * 测试纯汉字分词
     */
    public static void testAllChineseSeg(String[] args) {
        if (args.length < 1) System.exit(0);
        String test_text = args[0];     // 测试文本通过标准输入传入
        if (StringUtils.isBlank(test_text)) System.exit(0);
        //  将信息熵 互信息等统计量加入到过滤决策机制中
        Segment segment = new Segment();
        //NovelTest = true;
        Config.DEBUG_MODE = true;

        System.out.println("\n*************************分词结果集->" + segment.segmentWithAllChinese(test_text) + "<-*************************\n");
    }

    /**
     * 根据输入的文本按行抽词
     *  Line  控制测试输入文本的行数
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
                    System.out.println("\n*************************分词结果集" + result + "*************************\n");
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
     * 测试抽词
     */
    public static void testExtractWords(String[] args) {
        Config.DEBUG_MODE = true;
        Segment segment = new Segment();
        Jedis redis = new Jedis("localhost");
        redis.auth("root");
        //System.out.println("抽词结果----->" + segment.extractWords(args[0]) + "<---");
    }
    /**
     * 测试 redis 存取
     */
    public static void testRediSave(String[] args) {
        Jedis jedis = new Jedis(REDIS_HOST);
        jedis.auth(REDIS_AUTH_PASSWORD);
        //System.out.println(jedis.hgetAll(args[0]));
        System.out.println(jedis.get(args[0]));
    }
    /**
     * 测试 抽词
     */
    public static void testExtractWord(String[] args) {
        Config.DEBUG_MODE = true;
        Segment segment = new Segment();
        //System.out.println("抽词结果----->" + segment.extractWords(args[0]) + "<---");
    }


    /**
     * 测试redis的词频统计
     */
    public static void testRedisWordCount() {
        //  long t1 = System.currentTimeMillis();
        Jedis jedis = new Jedis(REDIS_HOST);
        jedis.auth(REDIS_AUTH_PASSWORD);
        jedis.zrevrangeWithScores(REDIS_WC_KEY, 0, 200000).forEach(it -> {
            System.out.println(it.getElement() + " -> " + it.getScore());
        });
        //  System.out.println("总计耗时:  " + (System.currentTimeMillis() - t1) + " ms");
    }


}
