package seg;


import config.Config;
import org.apache.commons.lang.StringUtils;
import util.HanUtils;

import java.io.*;

import static config.Config.MAX_STOP_WORD_LEN;
import static config.Constants.stopWordSet;

/**
 * 数据预处理
 */
public class PreProcess {
    public static String novel_text;

    public void initData() {   // 数据预处理,针对人民日报语料做一些修改
        try {
            // 以utf-8读取文件
            FileInputStream fis = new FileInputStream(Config.NovelPath);
            InputStreamReader reader = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(reader);
            String str = null;
            while ((str = br.readLine()) != null) {
                if(StringUtils.isNotBlank(str)){
                    HanUtils.FMMSegment(str, true);   // FMM算法切分候选词并统计词频
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



   /* public void initData2() {   // 数据预处理
        // 读取小说文本
        novel_text = FileUtils.readFileToString(Config.NovelPath);
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(novel_text);  // 去掉非中文字符   里边没有逗号
        // 再拆分停用词
        System.out.println("去非中文后的字符串数量" + replaceNonChinese.length + "   ");
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // 这里没有逗号
            if (StringUtils.isNotBlank(textDS) && textDS.length() != 1) {
                //String[] withoutStopWords = HanUtils.segmentByStopWordsDes(textDS);   // 将以非中文字符分割后的结果再以停用词分割
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
        System.out.println("切分字串的个数" + seg_result.size());
        System.out.println();
    }*/