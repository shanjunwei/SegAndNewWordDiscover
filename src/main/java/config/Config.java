package config;

/**
 * Created by bruce_shan on 2018/12/4 16:51.
 * Corporation CSU Software
 */
public class Config {
    public final static double MIN_PROBABILITY = 1e-10;   // 10 的负10 次方
    public static final int MAX_STOP_WORD_LEN = 4;  // 停用词最大长度为4
    public static final int MAX_WORD_LEN = 4 + 1;  // 分词最大长度为4
    public static final int MAX_WORD_COUNT = 1;  // 分词最小词频
    public static final double beta = 0.51;  // 置信度 β

    public  final static double MIN_LEFT_ENTROPY = 0.01;   // 最小左熵,用于左邻熵过滤
    public  final static double MIN_RIGHT_ENTROPY = 0.01;   // 最小右熵,用于右邻熵过滤

    public static String NovelPath = "D:\\Code\\Java\\SegEvaluate-master\\data\\test-text.txt"; // 语料入口
    public static String DebugPath = "F:\\JAVATools\\HanLP\\人名日报\\666.txt";   //  debug 信息输出
    /**
     * 字符类型对应表
     */
    public static String CharTypePath = "data/dictionary/other/CharType.bin";
    /**
     * 停用词词典
     */
    public static String StopWordsPath = "D:\\HanLP\\stopwords.txt";   // 停用词路径

    /**
     *  字典树序列化路径
     */
    public static String SerialPath = "D:\\BigData\\HanLP\\trie_serial.txt";
}
