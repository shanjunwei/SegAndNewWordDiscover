package concurrent;

import config.Config;
import org.apache.commons.lang.StringUtils;
import util.HanUtils;

import java.io.*;

/**
 * ����Ƶͳ�����ɲ�����ʽ���Ƚ��봮�е�ʱ������
 */
public class SegCountProcess {

    public static void main(String[] args) {
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
}
