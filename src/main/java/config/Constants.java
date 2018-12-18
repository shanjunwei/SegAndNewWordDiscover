package config;

import pojo.Term;
import trie.bintrie.BinTrie;
import util.FileUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  ����
 */
public class Constants {
    public static boolean DEBUG_MODE = false;   //�ִ�DEBUG ģʽ

    public static final String  HAS_SPECIAL_CHAR  = "hasSpecial";
    public static final String  STR_REPLACE_SPECIAL  = "str";
    public static final String  MAX_KEY  = "max";

    public static boolean NovelTest = false;   // �ǲ���С˵���������ձ�����,Ĭ�ϵ��������ձ�
    public static  String NOVEL;
    public static HashSet stopWordSet = new HashSet();     // ͣ�ôʹ�ϣ��
    public static Map<String, Integer> wcMap = new HashMap<>(1000000);   // ���ڴ洢�зֽ����ͳ�ƴ�Ƶ

    public static Map<String, Integer> singWordCountMap = new ConcurrentHashMap<>();   // ���ִ�Ƶ

    public static Map<String, Term> segTermMap = new HashMap<>(1000000);   //����ͳ������һ������

/*    public static final List<Float> LElist = new ArrayList<>();   //����
    public static final List<Float> RElist = new ArrayList<>();   //����
    public static final List<Float> MI_list = new ArrayList<>();   //����Ϣ*/

    /**
     * �ֵ���,������ǰ׺��ѯ
     */
    public static BinTrie<Integer> trieLeft = new BinTrie<>();
    /**
     * �ֵ���,������ǰ׺��ѯ
     */
    public static BinTrie<Integer> trieRight = new BinTrie<>();
    static {
       // initStopWords();
    }
    //  ��ʼ��ͣ�ôʹ�ϣ��
    private static void initStopWords() {
        stopWordSet = FileUtils.readFileByLineToHashSet(Config.StopWordsPath);
    }

}
