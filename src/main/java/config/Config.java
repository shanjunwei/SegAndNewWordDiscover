package config;

import static config.Constants.NovelTest;

/**
 * Created by bruce_shan on 2018/12/4 16:51.
 * Corporation CSU Software
 */
public class Config {
    public final static double MIN_PROBABILITY = 1e-10;   // 10 的负10 次方
    public static final int MAX_STOP_WORD_LEN = 4;  // 停用词最大长度为4
    public static final int MAX_WORD_LEN = 4 + 1;  // 分词最大长度为5
    public static final float entropy_theta = 0.4f;  // 左右信息熵比值过滤  之前采用0.78 现在试试0.44
    public static final float MI_THRESHOLD_VALUE = 0.02f;  // 互信息过滤阈值,之前用的0.89
    public static final int MAX_WORD_COUNT = 1;  // 分词最小词频
    public static final float beta = 0.51f;  // 置信度 β
    public final static float MIN_LEFT_ENTROPY = 0.01f;   // 最小左熵,用于左邻熵过滤
    public final static float MIN_RIGHT_ENTROPY = 0.01f;   // 最小右熵,用于右邻熵过滤
    public static String NovelPath =   "data\\test-text.txt"; // 语料入口
    public static String segTermMapPath = "data\\segTermMap.txt";   //序列化文件输出路径
    public static String ErrorSegPath =   "data/error_seg.txt"; // 切分错误的行记录

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
    public static float maxMI =0f;
    /**
     * 切分段去重后  最大左熵
     */
    public static float maxLE =0f;
    /**
     * 切分段去重后  最大右熵
     */
    public static float maxRE =0f;


    static {
        if(NovelTest == true){
            NovelPath = "D:\\HanLP\\novel\\平凡的世界.txt"; // 语料入口
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
