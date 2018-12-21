package seg;

import computer.Occurrence;
import config.Constants;
import org.apache.commons.lang.StringUtils;
import pojo.Term;
import serilize.JsonSerializationUtil;
import util.HanUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static config.Constants.*;

/**
 * ���ڻ���Ϣ ����Ϣ�صķִ����´ʷ���
 */
public class Segment {
    // ����Ԥ��������
    static {
        //JsonSerializationUtil.serilizableStatisticsToFile();    // ���л�������
        //JsonSerializationUtil.deserilizableStatistics();    // �����л�
        //JsonSerializationUtil.loadTrieFromFile();  // �����л��ֵ���
    }

    /**
     * ��¶���ⲿ���õķִʽӿ�
     * �������Ĳ��� û�з������ַ�����һ������
     */
    public List<String> segment(String text) {
        if (StringUtils.isBlank(text)) return null;
        long t1 = System.currentTimeMillis();
        String segResult = segmentToString(text);
        if (DEBUG_MODE) System.out.println("�ִʺ�ʱ: " + (System.currentTimeMillis() - t1) + "  ms");
        return Arrays.asList(segResult.split(" "));
    }

    /**
     * ��¶���ⲿ���õķִʽӿ� TODO*****
     */
    public String segmentToString(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        // 1. �Է����ķָ���ǽ�β�����б���ԭ���ķ�����,Ŀ���Ǳ������ǵ�λ��
        String[] array = HanUtils.splitWithNonChineseChar(text);
        for (String str : array) {
            if (HanUtils.isChineseCha(str.charAt(0))) {
                stringBuilder.append(" " + segmentWithAllChinese(str) + " ");
            } else {
                stringBuilder.append(" " + str + " ");
            }
        }
        String result = stringBuilder.toString().trim();
        result = result.replaceAll("\\s{1,}", " ");
        if (DEBUG_MODE) System.out.println("segmentToString����������������������������>" + result);
        return result;
    }


    /**
     * ��¶���ⲿ���õ� ��� �ӿ� TODO*****
     * ����������һ�仰,������ܰ����������ַ�
     */
    public String extractWords(String text) {
        if (StringUtils.isBlank(text)) return text;
        // ���ַ����Է������ַ��и��Ƭ��
        String[] array = HanUtils.replaceNonChineseCharacterAsBlank(text);

        StringBuilder exactWords = new StringBuilder();
        for (String str : array) {
            if (str.length() == 1) {     // �䵥��һ���ֵ����ɴ�,��ѡ������һ���ֵ�
                exactWords.append(" " + str);
            } else {
                List<Term> termList = HanUtils.segmentToTerm(str, false);
                List<Term> result = extractWordsFromNGram(termList);
                for (Term term : result) {
                    exactWords.append(" " + term);
                }
            }
        }
        return exactWords.toString().trim();
    }

    /**
     * //TODO  ���ʶ��
     * ��¶���ⲿ���õķִʽӿ�
     * �������Ĳ��� û�з������ַ���һ������
     */
    public String segmentWithAllChinese(String text) {
        if (StringUtils.isBlank(text)) return " ";

        List<Term> termList = HanUtils.segmentToTerm(text, false);
        List<Term> exactWords = new ArrayList<>();
        // ����ȡ
        if (termList == null) {    //  ����һ���ֵ�
            exactWords.add(new Term(text, 0, text.length()));
        } else {
            List<Term> result = extractWordsFromNGram(termList);
            exactWords.addAll(result);
        }
        text = HanUtils.handleSentenceWithExtractWords(text, exactWords);  //  �ȴ�����
        return text;
    }

    public static void main(String[] args) {
        Constants.NovelTest = true;
        Segment segment = new Segment();
        segment.segmentWithAllChinese("������ϯ����ƽ���Ҷ�����ϯ����ƽ");
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

        text = HanUtils.handleSentenceWithExtractWords2(text, exactWords);  //  �ȴ�����
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
        result_list.sort((o1, o2) -> Double.compare(Double.valueOf(redis.hget(o2.seg, SCORE)), Double.valueOf(redis.hget(o1.seg, SCORE))));
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
        // �����ѡ�ʵ� ����Ϣ �� ��Ϣ��
        for (Term seg : termList) {
            //Term term = segTermMap.get(seg.seg);
            Term term = Term.getTermObjectFromMap(redis.hgetAll(seg.seg));   // ��redis�ж�ȡ
            if (term != null) {
                // ��Ϣ�ع���
                if (occurrence.EntropyFilter(term.le, term.re)) { // ���˵���Ϣ�ع������Բ��Ǵʵ�
                    if (DEBUG_MODE)
                        System.out.println("��Ϣ�ع���-> " + seg + "   mi->   " + term.mi + " le->" + term.le + " re->" + term.re);
                }
                // ����Ϣ����
                else if (occurrence.MutualInformationFilter(term.mi)) {
                    if (DEBUG_MODE)
                        System.out.println("����Ϣ����-> " + seg + "   mi->   " + term.mi + " le->" + term.le + " re->" + term.re);
                } else {
                    float score = occurrence.getNormalizedScore(term);
                    redis.hset(seg.seg, SCORE, String.valueOf(score));  // �޸�ĳһ����ֵ
                    term.setScore(score);   // ��ֵ
                    result.add(seg);
                }
            }
        }
        // �Ժ�ѡ������ ��һ���÷� ��������
        result.sort((o1, o2) -> Double.compare(Double.valueOf(redis.hget(o2.seg, SCORE)), Double.valueOf(redis.hget(o1.seg, SCORE))));
        if (DEBUG_MODE) System.out.println("   ��һ�������*****->   " + result + "\n");
        return result.size() == 0 ? null : result.get(0);
    }

}


 /*       if (termList.size() == 1 && termList.get(0).getSeg().length() == 1) {    // һ����Ҳ����ͳ����
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
        }*/