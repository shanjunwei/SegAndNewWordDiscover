package util;


import config.Constants;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static config.Config.MAX_WORD_LEN;
import static config.Config.entropy_theta;
import static config.Constants.singWordCountMap;
import static config.Constants.wcMap;

/**
 * 汉字处理相关工具类
 */
public class HanUtils {

    // 识别非中文符号  包括英文，标点，数学运算符
    public static boolean isChineseCharacter(String text) {   // 可以识别繁体字
        // 中文验证规则
        String regEx = "[\\u4e00-\\u9fa5]+";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();  // 字符串是否与正则表达式相匹配
    }

    public static boolean isSpecialCharacter(char c) {   // 可以识别繁体字
        if (c == '+' || c == '*' || c == '|' || c == '\\') {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 判断两个字符串首字符是否相等
     */
    public static boolean hasCommonFirstCharacter(String str1, String str2) {
        if (StringUtils.isBlank(str1) || StringUtils.isBlank(str2)) {
            return false;
        }
        return str1.substring(0, 1).equals(str2.substring(0, 1));
    }


    /**
     * 取中文词汇转换成拼音    如 张三丰 ->  zhangshanfeng
     */
    public static String firstPinyinCharStr(String chineseWord) {
        StringBuffer pinyinName = new StringBuffer();
        char[] nameChar = chineseWord.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char cha : nameChar) {
            try {
                String[] str_array = PinyinHelper.toHanyuPinyinStringArray(cha, defaultFormat);
                pinyinName.append(str_array[0]);
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
        }
        return pinyinName.toString();
    }





    // 将非中文字符  以及中文停用词  以空格替代
    public static String[] replaceNonChineseCharacterAsBlank(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (HanUtils.isChineseCharacter(String.valueOf(chars[i]))) {
                stringBuilder.append(chars[i]);
            } else {
                stringBuilder.append(",");
            }
        }
        String temp = stringBuilder.toString().replaceAll("[,]+", ",");  // 对多个非分词字符进行合并处理
        String[] seg_nonChinese_result = temp.split(",");
        return seg_nonChinese_result;
    }

    public static String getOriginalStr(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        if (str.contains("\\\\")) str = str.replace("\\\\", "\\");
        String regEx = "[\\\\\\*\\+\\|\\{\\}\\(\\)\\^\\$\\[\\]\\?\\,\\.\\&]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            str = str.replace("\\", "");
        }
        return str;
    }
    /**
     * 2      * 转义正则特殊字符 （$()*+.[]?\^{}
     * 3      * \\需要第一个替换，否则replace方法替换时会有逻辑bug
     * 4
     */
    public static Map<String, Object> makeQueryStringAllRegExp(String str) {
        Map<String, Object> result = new HashMap<>();
        if (StringUtils.isBlank(str)) {
            return null;
        }
        String regEx = "[\\*+|{}()^$\\[\\]?,.&]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        result.put(Constants.HAS_SPECIAL_CHAR, m.find() ? true : false);
        while (m.find()) {
            String find = m.group();
            str = str.replace(find, "\\" + find);
        }
        result.put(Constants.STR_REPLACE_SPECIAL, str);
        return result;
    }


    public static String[] replaceNonChineseCharacterAddBlank(String text) {
         /*  Pattern r = Pattern.compile("[\\u4E00-\\u9FFF]+");
        Matcher m = r.matcher(text);
        while (m.find()) {
            text = text.replaceAll(m.group(), " " + m.group() + " ");   // 代码达到过滤非中文字符的作用
        }*/
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (HanUtils.isChineseCharacter(String.valueOf(chars[i]))) {
                stringBuilder.append(" ");
            } else if (!isSpecialCharacter(chars[i])) {
                stringBuilder.append(chars[i]);
            }/*else {
                stringBuilder.append(chars[i]);
            }*/
        }
        String temp = stringBuilder.toString().trim();   // 去首尾空格
        temp = temp.replaceAll("\\s{1,}", " ");  // 去连续空格
        String[] nonChinese_result = temp.split(" ");
        return nonChinese_result;
    }


    public static void main(String[] args) {
        System.out.println(Arrays.asList(replaceNonChineseCharacterAddBlank("中国人民争取和平与裁军委员会代表团是应老挝和平与团结委员会的邀请于12月25日抵老进行为期5天的友好访问的")));
    }


    /**
     * 去掉停用词,将待分词串以停用词分割 从高到低取词
     */
   /* public static String[] segmentByStopWordsDes(String text) {
        if (text.length() == 1) {
            return " ".split(" ");    // 返回结果为 {}
        }
        int temp_max_len = Math.min(text.length() + 1, MAX_STOP_WORD_LEN);
        int p = 0;
        while (p < text.length()) {
            int q = temp_max_len;
            while (q > 0) {  // 控制取词的长度
                // 取词串  p --> p+q
                if (p + q > text.length()) {
                    q--;    // 尝试下去，分全
                    continue;
                }
                String strChar = text.substring(p, p + q);
                if (stopWordSet.contains(strChar)) {  //|| strChar.contains(",")
                    // System.out.println("==>" + strChar);
                    text = text.replaceAll(strChar, ",");
                    p++;
                    q = temp_max_len;
                    continue;  // 停用词略过
                }
                q--;
            }
            p++;
        }

        String temp = text.replaceAll("[,]+", ",");  // 对多个非分词字符进行合并处理
        String[] seg_stop_result = temp.split(",");
        return seg_stop_result;
    }
*/
    // 切分词  FMM 算法
    public static void FMMSegment(String text, boolean countWordFrequency) {
        // 额外统计单个字的词频
        wordCountSingleWord(text);
        if (text.length() == 1) {
            return;
        }
        int temp_max_len = Math.min(text.length() + 1, MAX_WORD_LEN);
        int p = 0;
        while (p < text.length()) {
            int q = 1;
            while (q < temp_max_len) {  // 控制取词的长度
                if (q == 1) {
                    q++;
                    continue;  // 长度为1略过,单个汉字不具有分词意义
                }
                // 取词串  p --> p+q
                if (p + q > text.length()) {
                    break;
                }
                String strChar = text.substring(p, p + q);
                // 统计词串的词频
                if (countWordFrequency) {
                    if (wcMap.containsKey(strChar)) {
                        wcMap.put(strChar, wcMap.get(strChar) + 1);
                    } else {
                        wcMap.put(strChar, 1);
                    }
                }
                q++;
            }
            p++;
        }
    }


    // 切分词  FMM 算法
    public static List<String> getFMMList(String text, boolean countWordFrequency) {
        // 额外统计单个字的词频
        if (countWordFrequency){
            System.out.println("统计单个字的词频");
            wordCountSingleWord(text);
        }
        if (text.length() == 1) {
            return null;
        }
        List<String> result = new ArrayList<>();
        int temp_max_len = Math.min(text.length() + 1, MAX_WORD_LEN);
        int p = 0;
        while (p < text.length()) {
            int q = 1;
            while (q < temp_max_len) {  // 控制取词的长度
                if (q == 1) {
                    q++;
                    continue;  // 长度为1略过,单个汉字不具有分词意义
                }
                // 取词串  p --> p+q
                if (p + q > text.length()) {
                    break;
                }
                String strChar = text.substring(p, p + q);
                result.add(strChar);
                // 统计词串的词频
                if (countWordFrequency) {
                    if (wcMap.containsKey(strChar)) {
                        wcMap.put(strChar, wcMap.get(strChar) + 1);
                    } else {
                        wcMap.put(strChar, 1);
                    }
                }
                q++;
            }
            p++;
        }
        return result;
    }


    public static void wordCountSingleWord(String text) {
        char[] chars = text.toCharArray();
        for (char singleWord : chars) {
            if (!singWordCountMap.containsKey(String.valueOf(singleWord))) {
                singWordCountMap.put(String.valueOf(singleWord), 1);
            } else {
                singWordCountMap.put(String.valueOf(singleWord), singWordCountMap.get(String.valueOf(singleWord)) + 1);
            }
        }
    }


    // 判断待添加进最终结果集的 分词是否与之前的重合

    // 判断的逻辑再更改,判断重叠应该引入词在原来语句中的位置
    public static boolean hasNonCommonWithAllAddedResultSet(LinkedHashSet AddedResultSet, String key) {
        Iterator<String> iterator = AddedResultSet.iterator();
        while (iterator.hasNext()) {
            if (hasCommonStr(key, iterator.next(), false)) {    // 这里的逻辑有误--> 应该是与之前加入结果集任何字符串都不重合
                return false;
            }
        }
        return true;
    }


    // 判断两个字符串是否有交集
    public static boolean hasCommonStr(String str1, String str2, boolean isFirstTimeScreen) {  // 控制参数，是否是第一轮筛选
        char[] chars = str1.toCharArray();
        // 共头的不算有交集
        if (!isFirstTimeScreen && str1.substring(0, 1).equals(str2.substring(0, 1))) {  // 第二轮筛选共头的不算有交集
            return false;
        }
        for (int i = 0; i < chars.length; i++) {
            if (str2.contains(chars[i] + "")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 反转字符串
     */
    public static String reverseString(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * 遍历某长度为4之下的且分段的所有可能组合  如电影院-> {电影,院}、{电,影院}
     */
    public static List<List<String>> getPossibleCombination(String str) {
        if (StringUtils.isBlank(str) || str.length() == 1) {
            return null;
        }
        List<List<String>> result = new ArrayList<>();

        switch (str.length()) {
            case 2:
                List<String> list21 = new ArrayList<>();
                list21.add(str.substring(0, 1));
                list21.add(str.substring(1));
                result.add(list21);
                break;
            case 3:
                List<String> list31 = new ArrayList<>();
                list31.add(str.substring(0, 1));
                list31.add(str.substring(1));
                result.add(list31);
                List<String> list32 = new ArrayList<>();
                list32.add(str.substring(0, 2));
                list32.add(str.substring(2));
                result.add(list32);
                break;
            case 4:
                List<String> list41 = new ArrayList<>();
                list41.add(str.substring(0, 1));
                list41.add(str.substring(1));
                result.add(list41);   //  1 3
                List<String> list42 = new ArrayList<>();
                list42.add(str.substring(0, 3));
                list42.add(str.substring(3));   // 3 1
                result.add(list42);
                List<String> list43 = new ArrayList<>();
                list43.add(str.substring(0, 2));
                list43.add(str.substring(2));   // 2 2
                result.add(list43);
                break;
        }
        return result;
    }
}
