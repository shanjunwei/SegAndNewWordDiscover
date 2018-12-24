package config;
import redis.clients.jedis.Jedis;
import trie.bintrie.BinTrie;
import util.FileUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ����
 */
public class Constants {
    public static boolean DEBUG_MODE = false;   //�ִ�DEBUG ģʽ

    public static final String HAS_SPECIAL_CHAR = "hasSpecial";
    public static final String STR_REPLACE_SPECIAL = "str";
    public static final String MAX_KEY = "max";

    /**********************  redis->  Term��������ֵ **************************/
    public static final String SEG = "seg";
    public static final String COUNT = "count";
    public static final String LEFT_BOUND = "leftBound";
    public static final String RIGHT_BOUND = "rightBound";
    public static final String MI = "mi";
    public static final String LE = "le";
    public static final String RE = "re";
    public static final String SCORE = "score";
    /**********************  redis->  Term��������ֵ **************************/

    public static boolean NovelTest = false;   // �ǲ���С˵���������ձ�����,Ĭ�ϵ��������ձ�
    public static String NOVEL;
    public static HashSet stopWordSet = new HashSet();     // ͣ�ôʹ�ϣ��
    //���ӱ��ص� Redis ����
    public static final Jedis redis = new Jedis("localhost");   // redis client
    public static Map<String, Integer> wcMap = new HashMap<>(1000000);   // ���ڴ洢�зֽ����ͳ�ƴ�Ƶ
    public static Map<String, Integer> singWordCountMap = new ConcurrentHashMap<>(100000);   // ���ִ�Ƶ

    //public static Map<String, Term> segTermMap = new HashMap<>(1000000);   //����ͳ������һ������,ռ���ڴ�ռ�̫��׼������
/*    public static final List<Float> LElist = new ArrayList<>();   //����
    public static final List<Float> RElist = new ArrayList<>();   //����
    public static final List<Float> MI_list = new ArrayList<>();   //����Ϣ*/
    /**
     * �ֵ���,������ǰ׺��ѯ
     */
    public static BinTrie<Integer> trieLeft = new BinTrie<>();
    /**
     * �ֵ���,������ǰ׺��ѯ��Ҳ�����������л�������
     */
    public static BinTrie<Integer> trieRight = new BinTrie<>();

    /**
     * �ֵ���,�������л�������
     */
//    public static BinTrie<Integer> trieRight = new BinTrie<>();

    static {
        // initStopWords();
        redis.auth("root");  // redis Ȩ����֤
    }

    //  ��ʼ��ͣ�ôʹ�ϣ��
    private static void initStopWords() {
        stopWordSet = FileUtils.readFileByLineToHashSet(Config.StopWordsPath);
    }

}
