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
 * ���ִ�����ع�����
 */
public class HanUtils {

    // ʶ������ķ���  ����Ӣ�ģ���㣬��ѧ�����
    public static boolean isChineseCharacter(String text) {   // ����ʶ������
        // ������֤����
        String regEx = "[\\u4e00-\\u9fa5]+";
        // ����������ʽ
        Pattern pattern = Pattern.compile(regEx);
        // ���Դ�Сд��д��
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();  // �ַ����Ƿ���������ʽ��ƥ��
    }

    public static boolean isSpecialCharacter(char c) {   // ����ʶ������
        if (c == '+' || c == '*' || c == '|' || c == '\\') {
            return true;
        } else {
            return false;
        }
    }


    /**
     * �ж������ַ������ַ��Ƿ����
     */
    public static boolean hasCommonFirstCharacter(String str1, String str2) {
        if (StringUtils.isBlank(str1) || StringUtils.isBlank(str2)) {
            return false;
        }
        return str1.substring(0, 1).equals(str2.substring(0, 1));
    }


    /**
     * ȡ���Ĵʻ�ת����ƴ��    �� ������ ->  zhangshanfeng
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





    // ���������ַ�  �Լ�����ͣ�ô�  �Կո����
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
        String temp = stringBuilder.toString().replaceAll("[,]+", ",");  // �Զ���Ƿִ��ַ����кϲ�����
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
     * 2      * ת�����������ַ� ��$()*+.[]?\^{}
     * 3      * \\��Ҫ��һ���滻������replace�����滻ʱ�����߼�bug
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
            text = text.replaceAll(m.group(), " " + m.group() + " ");   // ����ﵽ���˷������ַ�������
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
        String temp = stringBuilder.toString().trim();   // ȥ��β�ո�
        temp = temp.replaceAll("\\s{1,}", " ");  // ȥ�����ո�
        String[] nonChinese_result = temp.split(" ");
        return nonChinese_result;
    }


    public static void main(String[] args) {
        System.out.println(Arrays.asList(replaceNonChineseCharacterAddBlank("�й�������ȡ��ƽ��þ�ίԱ���������Ӧ���κ�ƽ���Ž�ίԱ���������12��25�յ��Ͻ���Ϊ��5����Ѻ÷��ʵ�")));
    }


    /**
     * ȥ��ͣ�ô�,�����ִʴ���ͣ�ôʷָ� �Ӹߵ���ȡ��
     */
   /* public static String[] segmentByStopWordsDes(String text) {
        if (text.length() == 1) {
            return " ".split(" ");    // ���ؽ��Ϊ {}
        }
        int temp_max_len = Math.min(text.length() + 1, MAX_STOP_WORD_LEN);
        int p = 0;
        while (p < text.length()) {
            int q = temp_max_len;
            while (q > 0) {  // ����ȡ�ʵĳ���
                // ȡ�ʴ�  p --> p+q
                if (p + q > text.length()) {
                    q--;    // ������ȥ����ȫ
                    continue;
                }
                String strChar = text.substring(p, p + q);
                if (stopWordSet.contains(strChar)) {  //|| strChar.contains(",")
                    // System.out.println("==>" + strChar);
                    text = text.replaceAll(strChar, ",");
                    p++;
                    q = temp_max_len;
                    continue;  // ͣ�ô��Թ�
                }
                q--;
            }
            p++;
        }

        String temp = text.replaceAll("[,]+", ",");  // �Զ���Ƿִ��ַ����кϲ�����
        String[] seg_stop_result = temp.split(",");
        return seg_stop_result;
    }
*/
    // �зִ�  FMM �㷨
    public static void FMMSegment(String text, boolean countWordFrequency) {
        // ����ͳ�Ƶ����ֵĴ�Ƶ
        wordCountSingleWord(text);
        if (text.length() == 1) {
            return;
        }
        int temp_max_len = Math.min(text.length() + 1, MAX_WORD_LEN);
        int p = 0;
        while (p < text.length()) {
            int q = 1;
            while (q < temp_max_len) {  // ����ȡ�ʵĳ���
                if (q == 1) {
                    q++;
                    continue;  // ����Ϊ1�Թ�,�������ֲ����зִ�����
                }
                // ȡ�ʴ�  p --> p+q
                if (p + q > text.length()) {
                    break;
                }
                String strChar = text.substring(p, p + q);
                // ͳ�ƴʴ��Ĵ�Ƶ
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


    // �зִ�  FMM �㷨
    public static List<String> getFMMList(String text, boolean countWordFrequency) {
        // ����ͳ�Ƶ����ֵĴ�Ƶ
        if (countWordFrequency){
            System.out.println("ͳ�Ƶ����ֵĴ�Ƶ");
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
            while (q < temp_max_len) {  // ����ȡ�ʵĳ���
                if (q == 1) {
                    q++;
                    continue;  // ����Ϊ1�Թ�,�������ֲ����зִ�����
                }
                // ȡ�ʴ�  p --> p+q
                if (p + q > text.length()) {
                    break;
                }
                String strChar = text.substring(p, p + q);
                result.add(strChar);
                // ͳ�ƴʴ��Ĵ�Ƶ
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


    // �жϴ���ӽ����ս������ �ִ��Ƿ���֮ǰ���غ�

    // �жϵ��߼��ٸ���,�ж��ص�Ӧ���������ԭ������е�λ��
    public static boolean hasNonCommonWithAllAddedResultSet(LinkedHashSet AddedResultSet, String key) {
        Iterator<String> iterator = AddedResultSet.iterator();
        while (iterator.hasNext()) {
            if (hasCommonStr(key, iterator.next(), false)) {    // ������߼�����--> Ӧ������֮ǰ���������κ��ַ��������غ�
                return false;
            }
        }
        return true;
    }


    // �ж������ַ����Ƿ��н���
    public static boolean hasCommonStr(String str1, String str2, boolean isFirstTimeScreen) {  // ���Ʋ������Ƿ��ǵ�һ��ɸѡ
        char[] chars = str1.toCharArray();
        // ��ͷ�Ĳ����н���
        if (!isFirstTimeScreen && str1.substring(0, 1).equals(str2.substring(0, 1))) {  // �ڶ���ɸѡ��ͷ�Ĳ����н���
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
     * ��ת�ַ���
     */
    public static String reverseString(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * ����ĳ����Ϊ4֮�µ��ҷֶε����п������  ���ӰԺ-> {��Ӱ,Ժ}��{��,ӰԺ}
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
