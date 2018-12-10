package config;

import pojo.Term;
import trie.bintrie.BinTrie;
import util.FileUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  ����
 */
public class Constants {
    public static  String NOVEL;
    public static HashSet stopWordSet = new HashSet();     // ͣ�ôʹ�ϣ��
    public static Map<String, Integer> wcMap = new ConcurrentHashMap<>();   // ���ڴ洢�зֽ����ͳ�ƴ�Ƶ

    public static Map<String, Integer> singWordCountMap = new ConcurrentHashMap<>();   // ���ִ�Ƶ

    public static Map<String, Term> segTermMap = new ConcurrentHashMap<>();   //����ͳ������һ������

    public static final List<Double> LElist = new ArrayList<>();   //����
    public static final List<Double> RElist = new ArrayList<>();   //����
    public static final List<Double> MI_list = new ArrayList<>();   //����Ϣ

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
