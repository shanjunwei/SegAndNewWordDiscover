package SegmentTest;
import config.Config;
import config.Constants;
import org.apache.commons.lang.StringUtils;
import seg.Segment;
import serilize.JsonSerializationUtil;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static config.Constants.DEBUG_MODE;

/**
 * 分词测试类
 */
public class SegTest {

    static {
        //Constants.redis.auth("root");
    }


    public static void main(String[] args) {
        //Constants.NovelTest = true;
        testSingleSentenceSeg(args);
        //testRepeatRegx(args);
        //testCalculationAndSerializationToFile();    //  计算并序列化到文件
        //testDebugByFileLine("H:\\小说\\《冰与火之歌》全集.txt",100);   // debug
        //testDebugByFileLine(Config.ErrorSegPath,600);
        //testAllChineseSeg(args);
        //saveCalculateResultToRedis();    // 重新序列化计算结果到redis
        //testExtractWords(args);   // 测试抽词
        //testSerializateTrieToFile();
        //testRediSave(args);
        //testExtractWord(args);
    }

    /**
     * 测试单个句子
     */
    public static void testSingleSentenceSeg(String[] args) {
        if (args.length < 1) System.exit(0);
        String test_text = args[0];     // 测试文本通过标准输入传入
        if (StringUtils.isBlank(test_text)) System.exit(0);
        //  将信息熵 互信息等统计量加入到过滤决策机制中
        Segment segment = new Segment();
        //NovelTest = true;
        DEBUG_MODE = true;
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
        DEBUG_MODE = true;

        System.out.println("\n*************************分词结果集->" + segment.segmentWithAllChinese(test_text) + "<-*************************\n");
    }

    /**
     * 测试 例子 孙少平 少平
     */
    public static void testRepeatRegx(String[] args) {
        String regEx = "少平";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(args[0]);
        while (m.find()) {
            String find = m.group();
            System.out.println(find + " [" + m.start() + "-" + m.end() + "]");
        }
    }

    /**
     * 测试 之前分错的前1000行并输出调试信息
     */
    public static void testDebugByFileLine(String inputPath, int line) {
        DEBUG_MODE = true;
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
     * 测试 保存结算结果到redis
     */
    public static void saveCalculateResultToRedis() {
//        Constants.NovelTest = true;
//        Config.NovelPath = "D:\\HanLP\\novel\\天龙八部.txt";
        //Constants.redis.flushAll();    // 先刷掉之前所有结果
        JsonSerializationUtil.saveCalculateResultToRedis();
        //System.out.println(Constants.redis.hgetAll("从此"));
    }


    /**
     * 测试抽词
     */
    public static void testExtractWords(String[] args) {
        Constants.NovelTest = true;
        Constants.DEBUG_MODE = true;

        Segment segment = new Segment();
        System.out.println("抽词结果----->" + segment.extractWords(args[0]) + "<---");
    }


    /**
     * 测试 redis 存取
     */
    public static void testRediSave(String[] args) {
        System.out.println(Constants.redis.hgetAll(args[0]));
    }


    /**
     * 测试 抽词
     */
    public static void testExtractWord(String[] args) {
        Constants.NovelTest = true;
        Constants.DEBUG_MODE = true;

        Segment segment = new Segment();
        System.out.println("抽词结果----->" + segment.extractWords(args[0]) + "<---");
    }
}
