package config;

import pojo.Term;
import trie.bintrie.BinTrie;
import util.FileUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  常量
 */
public class Constants {
    public static boolean DEBUG_MODE = false;   //分词DEBUG 模式

    public static final String  HAS_SPECIAL_CHAR  = "hasSpecial";
    public static final String  STR_REPLACE_SPECIAL  = "str";
    public static final String  MAX_KEY  = "max";

    public static boolean NovelTest = false;   // 是测试小说还是人民日报语料,默认的是人名日报
    public static  String NOVEL;
    public static HashSet stopWordSet = new HashSet();     // 停用词哈希表
    public static Map<String, Integer> wcMap = new HashMap<>(1000000);   // 用于存储切分结果和统计词频

    public static Map<String, Integer> singWordCountMap = new ConcurrentHashMap<>();   // 单字词频

    public static Map<String, Term> segTermMap = new HashMap<>(1000000);   //用于统计量归一化计算

/*    public static final List<Float> LElist = new ArrayList<>();   //左熵
    public static final List<Float> RElist = new ArrayList<>();   //右熵
    public static final List<Float> MI_list = new ArrayList<>();   //互信息*/

    /**
     * 字典树,用于左前缀查询
     */
    public static BinTrie<Integer> trieLeft = new BinTrie<>();
    /**
     * 字典树,用于右前缀查询
     */
    public static BinTrie<Integer> trieRight = new BinTrie<>();
    static {
       // initStopWords();
    }
    //  初始化停用词哈希表
    private static void initStopWords() {
        stopWordSet = FileUtils.readFileByLineToHashSet(Config.StopWordsPath);
    }

}
