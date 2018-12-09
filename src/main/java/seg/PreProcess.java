package seg;


import config.Config;
import org.apache.commons.lang.StringUtils;
import util.HanUtils;

import java.io.*;

import static config.Config.MAX_STOP_WORD_LEN;
import static config.Constants.stopWordSet;

/**
 * ����Ԥ����
 */
public class PreProcess {
    public static String novel_text;

    public void initData() {   // ����Ԥ����,��������ձ�������һЩ�޸�
        try {
            // ��utf-8��ȡ�ļ�
            FileInputStream fis = new FileInputStream(Config.NovelPath);
            InputStreamReader reader = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(reader);
            String str = null;
            while ((str = br.readLine()) != null) {
                if(StringUtils.isNotBlank(str)){
                    HanUtils.FMMSegment(str, true);   // FMM�㷨�зֺ�ѡ�ʲ�ͳ�ƴ�Ƶ
                }
            }
            br.close();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // ȥ��ͣ�ôʣ�ȥ��ͣ�ôʴӵ͵���
    private String[] segmentByStopWordsAes(String text) {
        if (text.length() == 1) {
            return " ".split(" ");    // ���ؽ��Ϊ {}
        }
        int temp_max_len = Math.min(text.length() + 1, MAX_STOP_WORD_LEN + 1);
        int p = 0;
        while (p < text.length()) {
            int q = 0;
            while (q < temp_max_len) {  // ����ȡ�ʵĳ���
                // ȡ�ʴ�  p --> p+q
                if (p + q > text.length()) {
                    break;
                }
                String strChar = text.substring(p, p + q);
                if (stopWordSet.contains(strChar)) {
                   // System.out.println("  |==>" + strChar);
                    text = text.replaceAll(strChar, ",");
                    p++;
                    continue;  // ͣ�ô��Թ�
                }
                q++;
            }
            p++;
        }

        String temp = text.replaceAll("[,]+", ",");  // �Զ���Ƿִ��ַ����кϲ�����
        String[] seg_stop_result = temp.split(",");
        return seg_stop_result;
    }
}



   /* public void initData2() {   // ����Ԥ����
        // ��ȡС˵�ı�
        novel_text = FileUtils.readFileToString(Config.NovelPath);
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(novel_text);  // ȥ���������ַ�   ���û�ж���
        // �ٲ��ͣ�ô�
        System.out.println("ȥ�����ĺ���ַ�������" + replaceNonChinese.length + "   ");
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // ����û�ж���
            if (StringUtils.isNotBlank(textDS) && textDS.length() != 1) {
                //String[] withoutStopWords = HanUtils.segmentByStopWordsDes(textDS);   // ���Է������ַ��ָ��Ľ������ͣ�ôʷָ�
                // for (int j = 0; j < withoutStopWords.length; j++) {
                // String text = withoutStopWords[j];
                String text = textDS;
                if (StringUtils.isNotBlank(text) && text.length() != 1) {
                    LinkedHashSet<String> termList = HanUtils.segment(text, true);
                    if (termList != null) {
                        seg_result.addAll(termList);
                    }
                }
            }
        }
        System.out.println("�з��ִ��ĸ���" + seg_result.size());
        System.out.println();
    }*/