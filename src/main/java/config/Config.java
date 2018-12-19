package config;

import static config.Constants.NovelTest;

/**
 * Created by bruce_shan on 2018/12/4 16:51.
 * Corporation CSU Software
 */
public class Config {
    public final static double MIN_PROBABILITY = 1e-10;   // 10 �ĸ�10 �η�
    public static final int MAX_STOP_WORD_LEN = 4;  // ͣ�ô���󳤶�Ϊ4
    public static final int MAX_WORD_LEN = 4 + 1;  // �ִ���󳤶�Ϊ5
    public static final float entropy_theta = 0.4f;  // ������Ϣ�ر�ֵ����  ֮ǰ����0.78 ��������0.44
    public static final float MI_THRESHOLD_VALUE = 0.02f;  // ����Ϣ������ֵ,֮ǰ�õ�0.89
    public static final int MAX_WORD_COUNT = 1;  // �ִ���С��Ƶ
    public static final float beta = 0.51f;  // ���Ŷ� ��
    public final static float MIN_LEFT_ENTROPY = 0.01f;   // ��С����,���������ع���
    public final static float MIN_RIGHT_ENTROPY = 0.01f;   // ��С����,���������ع���
    public static String NovelPath =   "data\\test-text.txt"; // �������
    public static String segTermMapPath = "data\\segTermMap.txt";   //���л��ļ����·��
    public static String ErrorSegPath =   "data/error_seg.txt"; // �зִ�����м�¼

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
    public static float maxMI =0f;
    /**
     * �зֶ�ȥ�غ�  �������
     */
    public static float maxLE =0f;
    /**
     * �зֶ�ȥ�غ�  �������
     */
    public static float maxRE =0f;


    static {
        if(NovelTest == true){
            NovelPath = "D:\\HanLP\\novel\\ƽ��������.txt"; // �������
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
