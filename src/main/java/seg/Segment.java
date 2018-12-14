package seg;

import computer.Occurrence;
import config.Config;
import config.Constants;
import org.apache.commons.lang.StringUtils;
import pojo.Term;
import serilize.JsonSerializationUtil;
import util.FileUtils;
import util.HanUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static config.Config.ErrorSegPath;
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

    }

    /**
     * ��¶���ⲿ���õķִʽӿ�
     * �������Ĳ��� û�з������ַ�����һ������
     */
    public List<String> segment(String text) {
        if (StringUtils.isBlank(text)) return null;
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
        if (StringUtils.isBlank(text)) return "";

        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(text);
        List<Term> exactWords = new ArrayList<>();
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // ����û�ж���
            if (StringUtils.isNotBlank(textDS)) {
                //List<String> termList = HanUtils.getFMMList(textDS, false);    // ���ŶȱȽϵ����������ֵ
                List<Term> termList = HanUtils.segmentToTerm(textDS, false);
                // ����ȡ
                if (termList == null) {    //  ����һ���ֵ�
                    exactWords.add(new Term(textDS, 0, textDS.length()));
                } else {
                    List<Term> result = extractWordsFromNGram(termList);
                    exactWords.addAll(result);
                }
            }
        }

        text = HanUtils.handleSentenceWithExtractWords(text, exactWords);  //  �ȴ�����
        //  ���������ַ�����
        boolean hasSpecialCharacter = (boolean) HanUtils.makeQueryStringAllRegExp(text).get(HAS_SPECIAL_CHAR);
        if (hasSpecialCharacter)
            text = String.valueOf(HanUtils.makeQueryStringAllRegExp(text).get(STR_REPLACE_SPECIAL));   // �ȴ��������ַ�
        // ��ȥ�������ַ��ͳ�ȡ�����Ĵ�,ʣ�µĶ��Ǵ���
        for (String nonChinese : HanUtils.replaceNonChineseCharacterAddBlank(text)) {
            if (StringUtils.isNotBlank(nonChinese)) {
                text = text.replaceAll(nonChinese, " " + nonChinese + " ");
            }
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
     * @param termList ��ѡ��   FMM �зִ�
     * @return ���Ŷȹ��ˣ� ���˵Ľ���޽���
     */
    public List<Term> extractWordsFromNGram(List<Term> termList) {
        // ���зֽ������Ϊ�����ַ����ֵ�������
        List<List<Term>> teams = new ArrayList<>();  //  ���鼯��   //List<String> seg_list = new ArrayList<>(termList);
        int p = 0;
        String history = termList.get(0).getSeg().substring(0, 1);
        String seg = termList.get(0).getSeg();
        String firstChar = seg.substring(0, 1);  // ���ַ�
        while (p < termList.size()) {
            List<Term> seg_team = new ArrayList<>();
            while (firstChar.equals(history)) {
                seg_team.add(termList.get(p));
                p++;

                if (p >= termList.size()) break;
                firstChar = termList.get(p).getSeg().substring(0, 1);
            }
            teams.add(seg_team);
            if (p >= termList.size()) break;
            history = termList.get(p).getSeg().substring(0, 1);
        }
        // ÿ������ѡһ����ѡ����  ������ÿ�鵹������
        List<Term> result_list = new ArrayList<>();
        teams.forEach(list -> {
            Term topCandidateFromSet = getTopCandidateFromSet(list);   // ��һ�־���
            if (topCandidateFromSet != null && StringUtils.isNotBlank(topCandidateFromSet.getSeg())) {
                result_list.add(topCandidateFromSet);
            }
        });
        // ����
        if (DEBUG_MODE) System.out.println("�ڶ���ɸѡǰ: " + result_list);
        result_list.sort((o1, o2) -> Double.compare(segTermMap.get(o2.getSeg()).score, segTermMap.get(o1.getSeg()).score));
        if (DEBUG_MODE) System.out.println("�ڶ�������󡪡�����������������> " + result_list);
        List<Term> final_result = new ArrayList<>();
        for (int i = 0; i < result_list.size(); i++) {
            if (final_result.isEmpty()) {
                final_result.add(result_list.get(0));
            }
            if (HanUtils.hasNonCommonWithAllAddedResultSet(final_result, result_list.get(i))) {
                final_result.add(result_list.get(i));
            }
        }
        final_result.sort(Comparator.comparing(term -> term.getLeftBound()));       // ����ٰ���ԭ����λ������
        if (DEBUG_MODE) System.out.println("���###########��������������������> " + final_result);
        return final_result;
    }

    //  ��һ��ɸѡ
    private Term getTopCandidateFromSet(List<Term> termList) {
        if (DEBUG_MODE) System.out.println("   ��һ��ɸѡǰ->   " + termList + "\n");
        List<Term> result = new ArrayList<>();
        Occurrence occurrence = new Occurrence();
        if (termList.size() == 1) {    // һ����Ҳ����ͳ����
            Term seg = termList.get(0);
            Term term = segTermMap.get(seg);
            float score = occurrence.getNormalizedScore(term);
            if (term == null) {
                Term term1 = new Term();
                term1.setScore(score);
                segTermMap.put(seg.getSeg(), term1);
            } else {
                term.setScore(score);   // ��ֵ
            }
            return termList.get(0);
        }
        // �����ѡ�ʵ� ����Ϣ �� ��Ϣ��
        for (Term seg : termList) {
            Term term = segTermMap.get(seg.getSeg());
            if (term != null) {
                // ��Ϣ�ع���
                if (occurrence.EntropyFilter(term.le, term.re)) { // ���˵���Ϣ�ع������Բ��Ǵʵ�
                    if (DEBUG_MODE)
                        System.out.println("��Ϣ�ع���-> " + seg + "   mi->   " + term.mi + " le->" + term.le + " re->" + term.re);
                    term.setScore(0);
                }  // ����Ϣ����
                else if (occurrence.MutualInformationFilter(term.mi)) {
                    if (DEBUG_MODE)
                        System.out.println("����Ϣ����-> " + seg + "   mi->   " + term.mi + " le->" + term.le + " re->" + term.re);
                    term.setScore(0);
                } else {
                    float score = occurrence.getNormalizedScore(term);
                    term.setScore(score);   // ��ֵ
                    result.add(seg);
                }
            }
        }
        // �Ժ�ѡ������ ��һ���÷� ��������
        result.sort((o1, o2) -> Double.compare(segTermMap.get(o2.getSeg()).score, segTermMap.get(o1.getSeg()).score));
        if (DEBUG_MODE) System.out.println("   ��һ�������*****->   " + result + "\n");
        //System.out.println("   ��һ��ɸѡ���->   " + termList.get(0) + "\n");
        return result.size() == 0 ? null : result.get(0);
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