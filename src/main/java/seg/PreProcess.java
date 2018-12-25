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
 * 数据预处理
 */
public class PreProcess {
    public void initData() {   // 数据预处理,针对人民日报语料做一些修改
        try {
            // 以utf-8读取文件
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

    public void initNovel() {   // 数据预处理,针对人民日报语料做一些修改
        // 读取小说文本
        String novel = FileUtils.readFileToString(Config.CORPUS_INPUT_PATH);
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(novel);  // 去掉非中文字符   里边没有逗号
        // 再拆分停用词
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // 这里没有逗号
            if (StringUtils.isNotBlank(textDS) && textDS.length() != 1) {
                HanUtils.FMMSegment(textDS, true);    // 置信度比较的是这里面的值
            }
        }
    }

    // 去掉停用词，去掉停用词从低到高
    private String[] segmentByStopWordsAes(String text) {
        if (text.length() == 1) {
            return " ".split(" ");    // 返回结果为 {}
        }
        int temp_max_len = Math.min(text.length() + 1, MAX_STOP_WORD_LEN + 1);
        int p = 0;
        while (p < text.length()) {
            int q = 0;
            while (q < temp_max_len) {  // 控制取词的长度
                // 取词串  p --> p+q
                if (p + q > text.length()) {
                    break;
                }
                String strChar = text.substring(p, p + q);
                if (stopWordSet.contains(strChar)) {
                    // System.out.println("  |==>" + strChar);
                    text = text.replaceAll(strChar, ",");
                    p++;
                    continue;  // 停用词略过
                }
                q++;
            }
            p++;
        }

        String temp = text.replaceAll("[,]+", ",");  // 对多个非分词字符进行合并处理
        String[] seg_stop_result = temp.split(",");
        return seg_stop_result;
    }
}