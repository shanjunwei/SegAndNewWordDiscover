package config;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by bruce_shan on 2018/12/4 16:51.
 * Corporation CSU Software
 */
public class Config {
    public final static double MIN_PROBABILITY = 1e-10;   // 10 的负10 次方
    public static final int MAX_STOP_WORD_LEN = 4;  // 停用词最大长度为4
    public static int MAX_WORD_LEN = 5 + 1;  // 分词最大长度为6
    public static float ENTROPY_THETA = 0.4f;  // 左右信息熵比值过滤  之前采用0.78 现在试试0.44
    public static float MI_THRESHOLD_VALUE = 1.0f;  // 互信息过滤阈值,之前用的0.89
    public static final int MIN_WORD_COUNT = 1;  // 分词最小词频阈值
    public static final float beta = 0.51f;  // 置信度 β
    public static float MIN_LEFT_ENTROPY = 0.01f;   // 最小左熵,用于左邻熵过滤
    public static float MIN_RIGHT_ENTROPY = 0.01f;   // 最小右熵,用于右邻熵过滤
    public static String CORPUS_INPUT_PATH = "data\\test-text.txt"; // 语料入口
    public static String ErrorSegPath = "data/error_seg.txt"; // 切分错误的行记录
    public static final String trailSerailPath = "data/trail_save.txt"; // 切分错误的行记录
    public static boolean DEBUG_MODE = false;   //分词DEBUG 模式
    public static int WC_THREAD_NUM = 20;   //  线程数

    public static String REDIS_HOST = "localhost";
    public static String REDIS_AUTH_PASSWORD = "root"; // redis验证的密码


    public static boolean CHINA_DAILY_TEST = true; // 是否是人民日报测试
    public static String NOVEL_INPUT_PATH = "data\\GameOfThrones.txt"; // 小说文本语料入口

    /**
     * 切分段去重后 总互信息
     */
    public static float totalMI;
    /**
     * 切分段去重后  总左熵
     */
    public static float totalLE;
    /**
     * 切分段去重后  总右熵
     */
    public static float totalRE;


    /**
     * 切分段去重后 最大互信息
     */
    public static final float maxMI = 1123.5605f;
    /**
     * 切分段去重后  最大左熵
     */
    public static final float maxLE = 5.094753f;
    /**
     * 切分段去重后  最大右熵
     */
    public static final float maxRE = 5.173816f;

    static {
        /****************************************** 读取配置文件 ************************************************/
        Properties p = new Properties();
        try {
            p.load(new InputStreamReader(new FileInputStream("segment.properties"), "UTF-8"));
            MI_THRESHOLD_VALUE = Float.valueOf(p.getProperty("MI_THRESHOLD_VALUE", String.valueOf(MI_THRESHOLD_VALUE)));
            ENTROPY_THETA = Float.valueOf(p.getProperty("ENTROPY_THETA", String.valueOf(ENTROPY_THETA)));
            MAX_WORD_LEN = Integer.valueOf(p.getProperty("MAX_WORD_LEN", String.valueOf(MAX_WORD_LEN)));
            MIN_LEFT_ENTROPY = Float.valueOf(p.getProperty("MIN_LEFT_ENTROPY", String.valueOf(MIN_LEFT_ENTROPY)));
            MIN_RIGHT_ENTROPY = Float.valueOf(p.getProperty("MIN_RIGHT_ENTROPY", String.valueOf(MIN_RIGHT_ENTROPY)));
            CORPUS_INPUT_PATH = p.getProperty("CORPUS_INPUT_PATH", CORPUS_INPUT_PATH);
            DEBUG_MODE = Boolean.valueOf(p.getProperty("DEBUG_MODE", String.valueOf(DEBUG_MODE)));
            WC_THREAD_NUM = Integer.valueOf(p.getProperty("WC_THREAD_NUM", String.valueOf(WC_THREAD_NUM)));
            REDIS_AUTH_PASSWORD = p.getProperty("REDIS_AUTH_PASSWORD", REDIS_AUTH_PASSWORD);  // redis 密码
            REDIS_HOST = p.getProperty("REDIS_HOST", REDIS_HOST);  // redis 服务地址
            NOVEL_INPUT_PATH = p.getProperty("NOVEL_INPUT_PATH", NOVEL_INPUT_PATH);  // 小说测试语料入口
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 字符类型对应表
     */
    public static String CharTypePath = "data/dictionary/other/CharType.bin";
    /**
     * 停用词词典
     */
    public static String StopWordsPath = "D:\\HanLP\\stopwords.txt";   // 停用词路径

    /**
     * 字典树序列化路径
     */
    public static String SerialPath = "D:\\BigData\\HanLP\\trie_serial.txt";


}
