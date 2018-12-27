package config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import trie.bintrie.BinTrie;
import util.FileUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;

import static config.Config.REDIS_HOST;

/**
 * 常量
 */
public class Constants {
    public static final String HAS_SPECIAL_CHAR = "hasSpecial";
    public static final String STR_REPLACE_SPECIAL = "str";
    public static final String MAX_KEY = "max";

    /**********************  redis->  Term对象属性值 **************************/
    public static final String SEG = "seg";
    public static final String COUNT = "count";
    public static final String LEFT_BOUND = "leftBound";
    public static final String RIGHT_BOUND = "rightBound";
    public static final String MI = "mi";
    public static final String LE = "le";
    public static final String RE = "re";
    public static final String SCORE = "score";
    /**********************  redis->  Term对象属性值 **************************/

    public static boolean NovelTest = false;   // 是测试小说还是人民日报语料,默认的是人名日报
    public static HashSet stopWordSet = new HashSet();     // 停用词哈希表
    //连接本地的 Redis 服务
    public static Map<String, Integer> wcMap = new HashMap<>(1000000);   // 用于存储切分结果和统计词频
    public static Map<String, Integer> singWordCountMap = new ConcurrentHashMap<>(100000);   // 单字词频
    public static LinkedTransferQueue<String> transferQueue = new LinkedTransferQueue();    // 阻塞队列,用来实现并发加速框架
    public static int QUEUE_SIZE = 0;

    /**********************  redis 相关静态变量 **************************/
    public static final JedisPool REDIS_POOL = new JedisPool(new JedisPoolConfig(), REDIS_HOST);   // 不能在多个线程中使用一个redis实例
    public static final String REDIS_WC_SINGLEWORD_KEY = "singleWordMap";
    public static final String REDIS_WC_KEY = "wcMap";


    //public static Map<String, Term> segTermMap = new HashMap<>(1000000);   //用于统计量归一化计算,占用内存空间太大，准备弃用
/*    public static final List<Float> LElist = new ArrayList<>();   //左熵
    public static final List<Float> RElist = new ArrayList<>();   //右熵
    public static final List<Float> MI_list = new ArrayList<>();   //互信息*/
    /**
     * 字典树,用于左前缀查询
     */
    public static BinTrie<Integer> trieLeft = new BinTrie<>();
    /**
     * 字典树,用于右前缀查询，也可以用于序列化计算结果
     */
    public static BinTrie<Integer> trieRight = new BinTrie<>();

    /**
     * 字典树,用于序列化计算结果
     */
//    public static BinTrie<Integer> trieRight = new BinTrie<>();

    static {
        // 自动读取配置

        // initStopWords();
        //redis.auth("root");  // redis 权限验证
    }

    //  初始化停用词哈希表
    private static void initStopWords() {
        stopWordSet = FileUtils.readFileByLineToHashSet(Config.StopWordsPath);
    }

}
