package config;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by bruce_shan on 2018/12/4 16:51.
 * Corporation CSU Software
 */
public class Config {
    public final static double MIN_PROBABILITY = 1e-10;   // 10 �ĸ�10 �η�
    public static final int MAX_STOP_WORD_LEN = 4;  // ͣ�ô���󳤶�Ϊ4
    public static int MAX_WORD_LEN = 5 + 1;  // �ִ���󳤶�Ϊ6
    public static float ENTROPY_THETA = 0.4f;  // ������Ϣ�ر�ֵ����  ֮ǰ����0.78 ��������0.44
    public static float MI_THRESHOLD_VALUE = 1.0f;  // ����Ϣ������ֵ,֮ǰ�õ�0.89
    public static final int MIN_WORD_COUNT = 1;  // �ִ���С��Ƶ��ֵ
    public static final float beta = 0.51f;  // ���Ŷ� ��
    public static float MIN_LEFT_ENTROPY = 0.01f;   // ��С����,���������ع���
    public static float MIN_RIGHT_ENTROPY = 0.01f;   // ��С����,���������ع���
    public static String CORPUS_INPUT_PATH = "data\\test-text.txt"; // �������
    public static String ErrorSegPath = "data/error_seg.txt"; // �зִ�����м�¼
    public static final String trailSerailPath = "data/trail_save.txt"; // �зִ�����м�¼
    public static boolean DEBUG_MODE = false;   //�ִ�DEBUG ģʽ
    public static int WC_THREAD_NUM = 20;   //  �߳���

    public static String REDIS_HOST = "localhost";
    public static String REDIS_AUTH_PASSWORD = "root"; // redis��֤������


    public static boolean CHINA_DAILY_TEST = true; // �Ƿ��������ձ�����
    public static String NOVEL_INPUT_PATH = "data\\GameOfThrones.txt"; // С˵�ı��������

    /**
     * �зֶ�ȥ�غ� �ܻ���Ϣ
     */
    public static float totalMI;
    /**
     * �зֶ�ȥ�غ�  ������
     */
    public static float totalLE;
    /**
     * �зֶ�ȥ�غ�  ������
     */
    public static float totalRE;


    /**
     * �зֶ�ȥ�غ� �����Ϣ
     */
    public static final float maxMI = 1123.5605f;
    /**
     * �зֶ�ȥ�غ�  �������
     */
    public static final float maxLE = 5.094753f;
    /**
     * �зֶ�ȥ�غ�  �������
     */
    public static final float maxRE = 5.173816f;

    static {
        /****************************************** ��ȡ�����ļ� ************************************************/
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
            REDIS_AUTH_PASSWORD = p.getProperty("REDIS_AUTH_PASSWORD", REDIS_AUTH_PASSWORD);  // redis ����
            REDIS_HOST = p.getProperty("REDIS_HOST", REDIS_HOST);  // redis �����ַ
            NOVEL_INPUT_PATH = p.getProperty("NOVEL_INPUT_PATH", NOVEL_INPUT_PATH);  // С˵�����������
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * �ַ����Ͷ�Ӧ��
     */
    public static String CharTypePath = "data/dictionary/other/CharType.bin";
    /**
     * ͣ�ôʴʵ�
     */
    public static String StopWordsPath = "D:\\HanLP\\stopwords.txt";   // ͣ�ô�·��

    /**
     * �ֵ������л�·��
     */
    public static String SerialPath = "D:\\BigData\\HanLP\\trie_serial.txt";


}
