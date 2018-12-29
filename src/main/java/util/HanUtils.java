package util;

import computer.Occurrence;
import config.Constants;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang.StringUtils;
import pojo.Term;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static config.Config.MAX_WORD_LEN;
import static config.Constants.*;

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


    // ʶ������ķ���  ����Ӣ�ģ���㣬��ѧ�����
    public static boolean isChineseCha(char cha) {   // ����ʶ������
        // ������֤����
        String regEx = "[\\u4e00-\\u9fa5]";
        // ����������ʽ
        Pattern pattern = Pattern.compile(regEx);
        // ���Դ�Сд��д��
        Matcher matcher = pattern.matcher(String.valueOf(cha));
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

    public static String[] splitWithNonChineseChar(String text) {
        // ������֤����
        String regEx = "[^\\u4e00-\\u9fa5]+";    // ����������ַ�
        // ����������ʽ
        Pattern pattern = Pattern.compile(regEx);
        // ���Դ�Сд��д��
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            text = text.replace(matcher.group(), " " + matcher.group() + " ");
        }
        String temp = text.trim();   // ȥ��β�ո�
        temp = temp.replaceAll("\\s{1,}", " ");  // ȥ�����ո�
        String[] result = temp.split(" ");
        return result;
    }

    // ���������ַ�  �Կո����
    public static String[] replaceNonChineseCharacterAsBlank(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (HanUtils.isChineseCharacter(String.valueOf(chars[i]))) {
                stringBuilder.append(chars[i]);
            } else {
                stringBuilder.append(" ");
            }
        }
        String temp = stringBuilder.toString().replaceAll("\\s{1,}", " ");
        temp = temp.trim();    // ȥ��β�ո�
        String[] seg_nonChinese_result = temp.split(" ");
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

    /**
     * ���ó�ȡ�����Ĵʶ�ԭ������зִ���   ���Ǵ����ȡ��ִ�ת��Ĺ���
     * ����bug,��������exactWords���ǰ�˳������,��������֮��Ľ��
     */
    public static String handleSentenceWithExtractWords2(String sentence, List<Term> exactWords) {
        if (exactWords == null || StringUtils.isBlank(sentence)) return sentence;

        StringBuilder stringBuilder = new StringBuilder(sentence);
        List<Term> termExactWords = new ArrayList<>();
        int shift = 0;
        for (Term word : exactWords) {
            Pattern p = Pattern.compile(word.getSeg());
            Matcher m = p.matcher(sentence);
            while (m.find()) {
                String find = m.group();
                Term seg = new Term(find, m.start(), m.end());
                if (termExactWords.isEmpty()) {
                    termExactWords.add(seg);
                    //  ��ԭ����seg������ϱ߽�ֵ
                    stringBuilder.insert(m.end(), Occurrence.getShiftStandardPosition(seg.leftBound, seg.rightBound));  // ��Ϊ��ֵ��ԭ��,λ�÷���ƫ��
                    shift = shift + 6;
                }
                if (HanUtils.hasNonCommonWithAllAddedResultSet(termExactWords, seg)) {
                    termExactWords.add(seg);
                    //  ��ԭ����seg������ϱ߽�ֵ
                    //  ȷ��ǰ���м�����ֵ
                    //String  beforeStr =  stringBuilder.substring(0,m.)
                    stringBuilder.insert(m.end() + shift, Occurrence.getShiftStandardPosition(seg.leftBound, seg.rightBound));  // ��Ϊ��ֵ��ԭ��,λ�÷���ƫ��
                    shift = shift + 6;
                }
            }
        }

        System.out.println("��ֵ��stringBuilder__> " + stringBuilder.toString());

        sentence = stringBuilder.toString();
        for (Term word : termExactWords) {
            String hatWord = word.getSeg() + Occurrence.getShiftStandardPosition(word.leftBound, word.rightBound);  // ���߽�Ĵ�
            System.out.println("���ֵ߽Ĵ�---��" + hatWord);
            sentence = sentence.replaceAll(hatWord, " " + word.getSeg() + " ");    // ��������һ������ ���� 5 25 ;����ƽ ��ƽ
        }
        System.out.println("��������Ԫ����-->" + sentence);
        return sentence;
    }

    /**
     * ���ó�ȡ�����Ĵʶ�ԭ������зִ���   ���Ǵ����ȡ��ִ�ת��Ĺ���
     * ��������exactWords�ǰ�˳������
     */
    public static String handleSentenceWithExtractWords(String sentence, List<Term> exactWords) {
        StringBuilder stringBuilder = new StringBuilder(sentence);
        int shift = 0;  // ƫ����
        for (Term term : exactWords) {
            stringBuilder.insert(term.rightBound + shift, Occurrence.getShiftStandardPosition(term.leftBound, term.rightBound));  // ��Ϊ��ֵ��ԭ��,λ�÷���ƫ��
            shift = shift + 6;
        }
        //System.out.println("��ֵ��stringBuilder__> "+stringBuilder.toString());
        sentence = stringBuilder.toString();
        for (Term word : exactWords) {
            String hatWord = word.getSeg() + Occurrence.getShiftStandardPosition(word.leftBound, word.rightBound);  // ���߽�Ĵ�
            //System.out.println("���ֵ߽Ĵ�---��"+hatWord);
            sentence = sentence.replaceAll(hatWord, " " + word.getSeg() + " ");    // ��������һ������ ���� 5 25 ;����ƽ ��ƽ
        }
        //System.out.println("��������Ԫ����-->"+sentence);
        return sentence;
    }


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
                if (countWordFrequency) wordCount(strChar);    // ͳ�ƴʴ��Ĵ�Ƶ
                q++;
            }
            p++;
        }
    }

    // �зִ�  FMM �㷨
    public static List<String> getFMMList(String text, boolean countWordFrequency) {
        // ����ͳ�Ƶ����ֵĴ�Ƶ
        if (countWordFrequency) {
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
                // ͳ�ƴʴ��Ĵ�Ƶ
                if (countWordFrequency) wordCount(strChar);    // ͳ�ƴʴ��Ĵ�Ƶ
                result.add(strChar);
                q++;
            }
            p++;
        }
        return result;
    }


    // �зִ�  FMM �㷨
    public static void FMMAndSaveWCToRedis(String text, Jedis jedis) {
        if (StringUtils.isBlank(text)) return;
        wordCountSingleWordAndSaveToRedis(text, jedis);   // ����ͳ�Ƶ����ֵĴ�Ƶ
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
                jedis.zincrby(REDIS_WC_KEY, 1, strChar);
                q++;
            }
            p++;
        }
    }


    // �зִ�  FMM �㷨 ,��ȡһ���ֵĺ�ѡ��
    public static List<Term> segmentToTerm(String text, boolean countWordFrequency) {
        //  �ͽ�����������ͣ�ô��з�
        List<Term> termList = new ArrayList<>();
        if (text.length() == 1) {
            return null;
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
                if (countWordFrequency) wordCount(strChar);    // ͳ�ƴʴ��Ĵ�Ƶ
                Term term = new Term(strChar, p, p + q);
                termList.add(term);
                q++;
            }
            p++;
        }
        return termList;
    }

    public static void wordCountSingleWord(String text) {
        char[] chars = text.toCharArray();
        for (char singleWord : chars) {
            String key = String.valueOf(singleWord);
           /* if (!singWordCountMap.containsKey(String.valueOf(singleWord))) {
                singWordCountMap.put(String.valueOf(singleWord), 1);
            } else {
                singWordCountMap.put(String.valueOf(singleWord), singWordCountMap.get(String.valueOf(singleWord)) + 1);
            }*/
            wordCount(key);
        }
    }

    /**
     * ��Ƶͳ�ƻ�������
     */
    public static void wordCount(String text) {
        if (StringUtils.isBlank(text)) return;
        if (wcMap.containsKey(text)) {
            wcMap.put(text, wcMap.get(text) + 1);
        } else {
            wcMap.put(text, 1);
        }
    }

    //  ͳ�ƴ�Ƶ��������洢��redis
    public static void wordCountSingleWordAndSaveToRedis(String text, Jedis jedis) {
        char[] chars = text.toCharArray();
        for (char singleWord : chars) {
            jedis.zincrby(REDIS_WC_KEY, 1, String.valueOf(singleWord));
        }
    }

    public static boolean hasNonCommonWithAllAddedResultSet(List<Term> AddedResultSet, Term key) {
        Iterator<Term> iterator = AddedResultSet.iterator();
        while (iterator.hasNext()) {
            if (hasCommonStr(iterator.next(), key)) {    // ������߼�����--> Ӧ������֮ǰ���������κ��ַ��������غ�
                return false;
            }
        }
        return true;
    }

    /**
     * �ж��ַ����Ƿ��н������㷨����
     * ������bug
     */
    public static boolean hasCommonStr(Term str1, Term str2) {  // ���Ʋ������Ƿ��ǵ�һ��ɸѡ
        if (isInnerBound(str1, str2) || isInnerBound(str2, str1)) {
            return true;
        }
        return false;
    }

    public static boolean isInnerBound(Term str1, Term str2) {  // ���Ʋ������Ƿ��ǵ�һ��ɸѡ
        int leftBoundOfStr1 = str1.leftBound;
        int rightBoundOfStr1 = str1.rightBound;
        int leftBoundOfStr2 = str2.leftBound;
        int rightBoundOfStr2 = str2.rightBound;
        if (leftBoundOfStr2 >= leftBoundOfStr1 && leftBoundOfStr2 < rightBoundOfStr1) {
            return true;
        }
        if (rightBoundOfStr2 > leftBoundOfStr1 && rightBoundOfStr2 < rightBoundOfStr1) {
            return true;
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
            case 5:
                List<String> list51 = new ArrayList<>();
                list51.add(str.substring(0, 1));
                list51.add(str.substring(1));
                result.add(list51);   //  1 4
                List<String> list52 = new ArrayList<>();
                list52.add(str.substring(0, 2));
                list52.add(str.substring(2));   // 2 3
                result.add(list52);
                List<String> list53 = new ArrayList<>();
                list53.add(str.substring(0, 3));
                list53.add(str.substring(3));   // 3 2
                result.add(list53);
                List<String> list54 = new ArrayList<>();
                list54.add(str.substring(0, 4));
                list54.add(str.substring(4));   // 4 1
                result.add(list54);
                break;

            case 6:
                List<String> list61 = new ArrayList<>();
                list61.add(str.substring(0, 1));
                list61.add(str.substring(1));
                result.add(list61);   //  1 5
                List<String> list62 = new ArrayList<>();
                list62.add(str.substring(0, 2));
                list62.add(str.substring(2));   // 2 4
                result.add(list62);
                List<String> list63 = new ArrayList<>();
                list63.add(str.substring(0, 3));
                list63.add(str.substring(3));   // 3 3
                result.add(list63);
                List<String> list64 = new ArrayList<>();
                list64.add(str.substring(0, 4));
                list64.add(str.substring(4));   // 4 2
                result.add(list64);
                List<String> list65 = new ArrayList<>();
                list65.add(str.substring(0, 5));
                list65.add(str.substring(5));   // 5 1
                result.add(list65);
                break;
        }
        return result;
    }
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
