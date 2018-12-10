package seg;

import computer.Occurrence;
import pojo.Term;
import util.HanUtils;

import java.util.*;

import static config.Constants.segTermMap;
import static config.Constants.wcMap;

/**
 * ���ڻ���Ϣ ����Ϣ�صķִ����´ʷ���
 */
public class Segment {
    // private static boolean debug_text_lenth = false;   // debug ��ʾ��Ϣ���ƣ����ȴ���4�Ĳ���ʾ
    //public static StringBuilder debug_Info = new StringBuilder();   // debug ��Ϣ�����ڴ����ļ���
    // ����Ԥ��������
    static {
        PreProcess preProcess = new PreProcess();
        preProcess.initData();
        // �������з�Ƭ�� ���ؽ� �ֵ��������������Ϣ�صļ���
        Occurrence occurrence = new Occurrence();
        occurrence.addAllSegAndCompute(wcMap);
    }

    public static void main(String[] args) {
        //  ����Ϣ�� ����Ϣ��ͳ�������뵽���˾��߻�����
        Segment segment = new Segment();
        List<String> result = segment.segment("��ʾ�ڴ����������µ�һ�����ȡ������ʩ");
        System.out.println("\n*************************�ִʽ����"+result+"*************************\n");
        List<String> result2 = segment.segment("���Ŷ��ܶ����̹����ĵľ����뿪������");
        System.out.println("\n*************************�ִʽ����"+result2+"*************************\n");
       // System.out.println(result2);
    }

    /**
     * ��¶���ⲿ���õķִʽӿ�
     * �������Ĳ��� û�з������ַ�����һ������
     */
    public List<String> segment(String text) {
        String segResult = segmentToString(text);
        return Arrays.asList(segResult.split(" "));
    }

    /**
     * ��¶���ⲿ���õķִʽӿ�
     * �������Ĳ��� û�з������ַ���һ������
     */
    public String segmentToString(String text) {
        System.out.println("Ԫ���ӡ�������>  "+text);
        List<String> termList = HanUtils.getFMMList(text, false);    // ���ŶȱȽϵ����������ֵ
        // ����ȡ
        LinkedHashSet<String> result = extractWordsFromNGram(text.length(), termList);
        // ʣ�µĶ��Ǵ���
        for (String seg : result) {
            text = text.replaceAll(seg, " " + seg + " ");
        }
        text = text.trim();   // ȥ��β�ո�
        text =text.replaceAll("\\s{1,}", " ");  // ȥ�����ո�
        return text;
    }


    /**
     * @param s_len    ԭ�ַ�������  sָ�Ӿ����Ĵ���æ  ȡ��ѡ��ǰ����len(s)��  ,sΪ��FMM �зִ�
     * @param termList ��ѡ��  ָ��->117 ָ�Ӿ�->2 ָ�Ӿ���->2 �Ӿ�->2 �Ӿ���->2 �Ӿ�����->2 ����->51 ������->2 �����Ĵ�->2 ����->2 ���Ĵ�->2 ���Ĵ���->2 �Ĵ�->35 �Ĵ���->6 �Ĵ���æ->2 ����->10 ����æ->2 ��æ->4
     * @return ���Ŷȹ��ˣ����˵Ľ���޽���
     */
    public LinkedHashSet<String> extractWordsFromNGram(int s_len, List<String> termList) {
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
            result_list.add(topCandidateFromSet);
        });

        // ����
        System.out.println("�ڶ���ɸѡǰ: "+result_list);
        result_list.sort((o1, o2) -> Double.compare(segTermMap.get(o2).score, segTermMap.get(o1).score));
        System.out.println("�ڶ��������: "+result_list);
        LinkedHashSet final_result = new LinkedHashSet();
        //int n = (int) Math.round(Math.sqrt(s_len));
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
        Occurrence occurrence = new Occurrence();
        if (termList.size() == 1) {    // һ����Ҳ����ͳ����
            String seg = termList.get(0);
            Term term = segTermMap.get(seg);
            double score = occurrence.getNormalizedScore(seg);
            term.setScore(score);   // ��ֵ
            return termList.get(0);
        }
        //  debug_Info.append("\n��ӡ������termList->   " + termList + "\n");
        System.out.println("\n��ӡ������termList->   " + termList + "\n");
        // �����ѡ�ʵ� ����Ϣ �� ��Ϣ��

        for (String seg : termList) {
            Term term = segTermMap.get(seg);
            double score = occurrence.getNormalizedScore(seg);
            term.setScore(score);   // ��ֵ
        }
        // �Ժ�ѡ������ ��һ���÷� ��������
        termList.sort((o1, o2) -> Double.compare(segTermMap.get(o2).score, segTermMap.get(o1).score));
        // debug_Info.append("   ��һ��ɸѡ���->   " + termList.get(0) + "\n");
        System.out.println("   ��һ��������->   " + termList + "\n");
        System.out.println("   ��һ��ɸѡ���->   " + termList.get(0) + "\n");
        return termList.get(0);
    }

}


/*// ����ܽ��
    public void doConfidenceCalculation() {
        // ���Ŷȼ���
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(PreProcess.novel_text);  // ȥ���������ַ�   ���û�ж���
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // ����û�ж���
            System.out.println("ԭ�ַ���1=>" + textDS);
           // debug_Info.append("ԭ�ַ���1=>" + textDS + "\n");
            if (StringUtils.isNotBlank(textDS) && textDS.length() != 1) {
                String text = textDS;
                debug_text_lenth = text.length() > 4 ? true : false;
                LinkedHashSet<String> termList = HanUtils.segment(text, false);    // ���ŶȱȽϵ����������ֵ
      *//*          if (debug_text_lenth) debug_Info.append("�з��ִ�=>");
                if (debug_text_lenth) debug_Info.append("\n");*//*
                // ȡ����
                LinkedHashSet result = extractWordsFromNGram(text.length(), termList);
                seg_final_result.addAll(result);
               // if (debug_text_lenth)
                  //  debug_Info.append("\n ***************************�����->" + result + "***************************************\n");
               // if (debug_text_lenth) debug_Info.append("\n");
            }
        }

        // ���ս���������
        List<String> list = new ArrayList<>(seg_final_result);
        list.sort(Comparator.comparing(HanUtils::firstPinyinCharStr));
        FileUtils.writeStringToFile(Config.DebugPath, debug_Info.toString());
        System.out.println();
    }*/