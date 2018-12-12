package seg;

import computer.Occurrence;
import config.Config;
import config.Constants;
import org.apache.commons.lang.StringUtils;
import pojo.Term;
import serilize.JsonSerializationUtil;
import util.FileUtils;
import util.HanUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static config.Constants.*;

/**
 * ���ڻ���Ϣ ����Ϣ�صķִ����´ʷ���
 */
public class Segment {
    //public static StringBuilder debug_Info = new StringBuilder();   // debug ��Ϣ�����ڴ����ļ���
    // ����Ԥ��������
    static {
        //JsonSerializationUtil.serilizableStatisticsToFile();    // ���л�������
        JsonSerializationUtil.deserilizableStatistics();    // �����л�
    }
    public static void main(String[] args) {
        String  test_text = args[0];     // �����ı�ͨ����׼���봫��
        //  ����Ϣ�� ����Ϣ��ͳ�������뵽���˾��߻�����
        Segment segment = new Segment();
        //NovelTest = true;
        DEBUG_MODE = true;
        List<String> result = segment.segment(test_text);
        System.out.println("\n*************************�ִʽ����" + result + "*************************\n");
    }

    /**
     * ��¶���ⲿ���õķִʽӿ�
     * �������Ĳ��� û�з������ַ�����һ������
     */
    public List<String> segment(String text) {
        long t1 = System.currentTimeMillis();
        String segResult = segmentByNonChineseFilter(text);
        System.out.println("�ִʺ�ʱ: " + (System.currentTimeMillis() - t1) + "  ms");
        return Arrays.asList(segResult.split(" "));
    }
    /**
     * ��¶���ⲿ���õķִʽӿ�    TODO*************************TODO*******
     * �������Ĳ��� û�з������ַ���һ������  TODO*************************TODO*******
     * ���۳�����õ����������*****************TODO*************************TODO*******
     */
    public String segmentToString(String text) {
        return segmentByNonChineseFilter(text);
    }

    /**
     * //TODO  ���ʶ��
     * ��¶���ⲿ���õķִʽӿ�
     * �������Ĳ��� û�з������ַ���һ������
     */
    public String segmentByNonChineseFilter(String text) {
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(text);
        List<String> exactWords = new ArrayList<>();
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // ����û�ж���
            if (StringUtils.isNotBlank(textDS)) {
                List<String> termList = HanUtils.getFMMList(textDS, false);    // ���ŶȱȽϵ����������ֵ
                // ����ȡ
                if (termList == null) {    //  ����һ���ֵ�
                    exactWords.add(textDS);
                } else {
                    LinkedHashSet<String> result = extractWordsFromNGram(termList);
                    exactWords.addAll(result);
                }
            }
        }
        //  ���������ַ�����
        boolean hasSpecialCharacter = (boolean) HanUtils.makeQueryStringAllRegExp(text).get(HAS_SPECIAL_CHAR);
        text = String.valueOf(HanUtils.makeQueryStringAllRegExp(text).get(STR_REPLACE_SPECIAL));   // �ȴ��������ַ�
        // ��ȥ�������ַ��ͳ�ȡ�����Ĵ�,ʣ�µĶ��Ǵ���
        for (String nonChinese : HanUtils.replaceNonChineseCharacterAddBlank(text)) {
            if (StringUtils.isNotBlank(nonChinese)) {
                text = text.replaceAll(nonChinese, " " + nonChinese + " ");
            }
        }
        for (String seg : exactWords) {
            text = text.replaceAll(seg, " " + seg + " ");
        }
        text = text.trim();   // ȥ��β�ո�
        text = text.replaceAll("\\s{1,}", " ");  // ȥ�����ո�
        // ��ԭ
        if (hasSpecialCharacter) text = HanUtils.getOriginalStr(text);
        return text;
    }
    /**
     * ԭ�ַ�������  sָ�Ӿ����Ĵ���æ  ȡ��ѡ��ǰ����len(s)��  ,sΪ��FMM �зִ�
     *
     * @param termList ��ѡ��  ָ��->117 ָ�Ӿ�->2 ָ�Ӿ���->2 �Ӿ�->2 �Ӿ���->2 �Ӿ�����->2 ����->51 ������->2 �����Ĵ�->2 ����->2 ���Ĵ�->2 ���Ĵ���->2 �Ĵ�->35 �Ĵ���->6 �Ĵ���æ->2 ����->10 ����æ->2 ��æ->4
     * @return ���Ŷȹ��ˣ� ���˵Ľ���޽���
     */
    public LinkedHashSet<String> extractWordsFromNGram(List<String> termList) {

        // ���зֽ������Ϊ�����ַ����ֵ�������
        List<List<String>> teams = new ArrayList<>();  //  ���鼯��
        List<String> seg_list = new ArrayList<>(termList);
        int p = 0;
        String history = seg_list.get(0).substring(0, 1);
        String seg = seg_list.get(0);
        String firstChar = seg.substring(0, 1);  // ���ַ�
        while (p < seg_list.size()) {
            List<String> seg_team = new ArrayList<>();
            while (firstChar.equals(history)) {
                seg_team.add(seg_list.get(p));
                p++;

                if (p >= seg_list.size()) break;

                firstChar = seg_list.get(p).substring(0, 1);
            }
            teams.add(seg_team);
            if (p >= seg_list.size()) break;
            history = seg_list.get(p).substring(0, 1);
        }

        // debug_Info.append("������" + teams);
        // ÿ������ѡһ����ѡ����  ������ÿ�鵹������
        List<String> result_list = new ArrayList<>();
        teams.forEach(list -> {
            String topCandidateFromSet = getTopCandidateFromSet(list);   // ��һ�־���
            if (StringUtils.isNotBlank(topCandidateFromSet)) {
                result_list.add(topCandidateFromSet);
            }
        });

        // ����
        if (DEBUG_MODE) System.out.println("�ڶ���ɸѡǰ: " + result_list);
        result_list.sort((o1, o2) -> Double.compare(segTermMap.get(o2).score, segTermMap.get(o1).score));
        if (DEBUG_MODE) System.out.println("�ڶ��������: " + result_list);
        LinkedHashSet final_result = new LinkedHashSet();
        for (int i = 0; i < result_list.size(); i++) {
            if (final_result.isEmpty()) {
                final_result.add(result_list.get(0));
            }
            if (HanUtils.hasNonCommonWithAllAddedResultSet(final_result, result_list.get(i))) {
                final_result.add(result_list.get(i));
            }
        }
        return final_result;
    }

    //  ��һ��ɸѡ
    private String getTopCandidateFromSet(List<String> termList) {
        if (DEBUG_MODE) System.out.println("   ��һ��ɸѡǰ->   " + termList + "\n");
        List<String> result = new ArrayList<>();
        Occurrence occurrence = new Occurrence();
        if (termList.size() == 1) {    // һ����Ҳ����ͳ����
            String seg = termList.get(0);
            Term term = segTermMap.get(seg);
            double score = occurrence.getNormalizedScore(seg);
            if (term == null) {
                Term term1 = new Term();
                term1.setScore(score);
                segTermMap.put(seg, term1);
            } else {
                term.setScore(score);   // ��ֵ
            }
            return termList.get(0);
        }
        // �����ѡ�ʵ� ����Ϣ �� ��Ϣ��
        for (String seg : termList) {
            Term term = segTermMap.get(seg);
            if (term != null) {
                if (HanUtils.EntropyFilter(term.le, term.re)) { // ���˵���Ϣ�ع������Բ��Ǵʵ�
                    if (DEBUG_MODE) System.out.println("��Ϣ�ع���-> "  + seg + "   mi->   " + term.mi +" le->" + term.le + " re->" + term.re);
                    term.setScore(0);
                } else {
                    double score = occurrence.getNormalizedScore(seg);
                    term.setScore(score);   // ��ֵ
                    result.add(seg);
                }
            }
        }
        // �Ժ�ѡ������ ��һ���÷� ��������
        result.sort((o1, o2) -> Double.compare(segTermMap.get(o2).score, segTermMap.get(o1).score));
        if (DEBUG_MODE) System.out.println("   ��һ�������->   " + result + "\n");
        //System.out.println("   ��һ��ɸѡ���->   " + termList.get(0) + "\n");
        return result.size() == 0 ? null : termList.get(0);
    }

}


// ��ȡС˵�ı�
        /*String novel = FileUtils.readFileToString(Config.NovelPath);
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(novel);  // ȥ���������ַ�   ���û�ж���
        // �ٲ��ͣ�ô�
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // ����û�ж���
            if (StringUtils.isNotBlank(textDS) && textDS.length() != 1) {
                System.out.println("ԭ�ı�---> "+textDS);
                System.out.println("�зֺ�---> "+segment.segment(textDS));
            }
        }*/