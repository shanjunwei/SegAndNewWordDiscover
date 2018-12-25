package seg;


import config.Config;
import org.apache.commons.lang.StringUtils;
import util.FileUtils;
import util.HanUtils;

import java.io.*;
import java.util.LinkedHashSet;

import static config.Config.MAX_STOP_WORD_LEN;
import static config.Constants.stopWordSet;

/**
 * ����Ԥ����
 */
public class PreProcess {
    public void initData() {   // ����Ԥ����,��������ձ�������һЩ�޸�
        try {
            // ��utf-8��ȡ�ļ�
            FileInputStream fis = new FileInputStream(Config.CORPUS_INPUT_PATH);
            InputStreamReader reader = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(reader);
            String str = null;
            while ((str = br.readLine()) != null) {
                if (StringUtils.isNotBlank(str)) {
                    String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(str);
                    for (int i = 0; i < replaceNonChinese.length; i++) {
                        String textDS = replaceNonChinese[i];
                        if (StringUtils.isNotBlank(textDS)) {
                            HanUtils.FMMSegment(textDS, true);
                        }
                    }
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

    public void initNovel() {   // ����Ԥ����,��������ձ�������һЩ�޸�
        // ��ȡС˵�ı�
        String novel = FileUtils.readFileToString(Config.CORPUS_INPUT_PATH);
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(novel);  // ȥ���������ַ�   ���û�ж���
        // �ٲ��ͣ�ô�
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // ����û�ж���
            if (StringUtils.isNotBlank(textDS) && textDS.length() != 1) {
                HanUtils.FMMSegment(textDS, true);    // ���ŶȱȽϵ����������ֵ
            }
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