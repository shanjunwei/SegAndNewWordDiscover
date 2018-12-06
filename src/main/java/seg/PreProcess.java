package seg;

import config.Config;
import org.apache.commons.lang.StringUtils;
import util.FileUtils;
import util.HanUtils;

import java.util.*;

import static config.Config.MAX_STOP_WORD_LEN;
import static config.Constants.stopWordSet;

/**
 * 数据预处理
 */
public class PreProcess {
    public static String novel_text;
    private static boolean countWordFrequency = true;   // 是否开启词频统计
    public static LinkedHashSet<String> seg_result = new LinkedHashSet<>();   // 切词结果集

    public void initData() {   // 数据预处理
        // 读取小说文本
        novel_text = FileUtils.readFileToString(Config.Novelpath);
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(novel_text);  // 去掉非中文字符   里边没有逗号
        // FileUtils.writeStringToFile("D:\\HanLP\\replaceNonChinese.txt", Arrays.toString(replaceNonChinese));          //  将结果写入文件
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
            //}
        }
        System.out.println("切分字串的个数" + seg_result.size());
        System.out.println();
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
                System.out.print(strChar + "=");
                if (stopWordSet.contains(strChar)) {
                    System.out.println("  |==>" + strChar);
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


    public static String getNovel_text() {
        return novel_text;
    }

    public static void main(String[] args) {
        PreProcess preProcess = new PreProcess();
        preProcess.initData();
        // preProcess.doConfidenceCalculation();
      /*  String test_text = "牙湾煤矿采五区孙少平请速";
        countWordFrequency = false;
        LinkedHashSet<String> termList = preProcess.segment(test_text);    // 置信度比较的是这里面的值

        termList.forEach(it -> {
            System.out.print(it + "->" + wcMap.get(it) + " ");
        });
        System.out.println();
        System.out.println();
        preProcess.fiterByConfidence(test_text.length(), termList);*/
    }
}