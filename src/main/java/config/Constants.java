package config;

import pojo.Term;
import trie.bintrie.BinTrie;
import util.FileUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  常量
 */
public class Constants {
    public static  String NOVEL;
    public static HashSet stopWordSet = new HashSet();     // 停用词哈希表
    public static Map<String, Integer> wcMap = new ConcurrentHashMap<>();   // 用于存储切分结果和统计词频

    public static Map<String, Integer> singWordCountMap = new ConcurrentHashMap<>();   // 单字词频

    public static Map<String, Term> segTermMap = new ConcurrentHashMap<>();   //用于统计量归一化计算
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
