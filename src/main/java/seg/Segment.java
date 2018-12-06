package seg;

import computer.Occurrence;
import config.Config;
import org.apache.commons.lang.StringUtils;
import pojo.Term;
import util.FileUtils;
import util.HanUtils;

import java.util.*;

import static config.Config.beta;
import static config.Constants.segTermMap;
import static config.Constants.wcMap;
import static util.HanUtils.hasCommonStr;

/**
 * ���ڻ���Ϣ ����Ϣ�صķִ����´ʷ���
 */
public class Segment {
    private static boolean debug_text_lenth = false;   // debug ��ʾ��Ϣ���ƣ����ȴ���4�Ĳ���ʾ
    public static StringBuilder debug_Info = new StringBuilder();   // debug ��Ϣ�����ڴ����ļ���
    public static LinkedHashSet<String> seg_final_result = new LinkedHashSet<>();   // �ִ����պ�ѡ���

    public static void main(String[] args) {
        // ����Ԥ����
        PreProcess preProcess = new PreProcess();
        preProcess.initData();

        // �������з�Ƭ�� ���ؽ� �ֵ��������������Ϣ�صļ���
        Occurrence occurrence = new Occurrence();
        occurrence.addAllSegAndCompute(PreProcess.seg_result, wcMap);

        //  ����Ϣ�� ����Ϣ��ͳ�������뵽���˾��߻�����
        Segment segment = new Segment();
        segment.doConfidenceCalculation();
    }

    public void doConfidenceCalculation() {
        // ���Ŷȼ���
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(PreProcess.novel_text);  // ȥ���������ַ�   ���û�ж���
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // ����û�ж���
            System.out.println("ԭ�ַ���1=>" + textDS);
            debug_Info.append("ԭ�ַ���1=>" + textDS + "\n");
            if (StringUtils.isNotBlank(textDS) && textDS.length() != 1) {
                String text = textDS;
                if (StringUtils.isNotBlank(text) && text.length() != 1) {
                    debug_text_lenth = text.length() > 4 ? true : false;
                    if (debug_text_lenth) debug_Info.append("ԭ�ַ���2=>" + text + "\n");
                    LinkedHashSet<String> termList = HanUtils.segment(text, false);    // ���ŶȱȽϵ����������ֵ
                    if (debug_text_lenth) debug_Info.append("�з��ִ�=>");
                    if (debug_text_lenth) debug_Info.append("\n");
                    System.out.println();
                    // ȡ����
                    LinkedHashSet result = fiterByConfidence(text.length(), termList);
                    seg_final_result.addAll(result);
                    if (debug_text_lenth)
                        debug_Info.append("\n ***************************�����->" + result + "***************************************\n");
                }
                if (debug_text_lenth) debug_Info.append("\n");
                System.out.println();
            }
        }

        // ���ս���������
        List<String> list = new ArrayList<>(seg_final_result);
        list.sort(Comparator.comparing(HanUtils::firstPinyinCharStr));
        //seg_final_result.stream().sorted((o1, o2) -> HanUtils.firstPinyinCharStr(o2).compareTo(HanUtils.firstPinyinCharStr(o1)));
        //  FileUtils.writeStringToFile("D:\\HanLP\\result235.txt",String.valueOf(list));
/*        FileUtils.writeFileToPath("D:\\BigData\\HanLP\\�����˲�\\result.txt",list,wcMap);
        FileUtils.writeStringToFile("D:\\BigData\\HanLP\\�����˲�\\debug.txt", debug_Info.toString());*/
        FileUtils.writeStringToFile(Config.DebugPath, debug_Info.toString());
        System.out.println();
    }


    /**
     * @param s_len    ԭ�ַ�������  sָ�Ӿ����Ĵ���æ  ȡ��ѡ��ǰ����len(s)��  ,sΪ��FMM �зִ�
     * @param termList ��ѡ��  ָ��->117 ָ�Ӿ�->2 ָ�Ӿ���->2 �Ӿ�->2 �Ӿ���->2 �Ӿ�����->2 ����->51 ������->2 �����Ĵ�->2 ����->2 ���Ĵ�->2 ���Ĵ���->2 �Ĵ�->35 �Ĵ���->6 �Ĵ���æ->2 ����->10 ����æ->2 ��æ->4
     * @return ���Ŷȹ��ˣ����˵Ľ���޽���
     */
    public LinkedHashSet<String> fiterByConfidence(int s_len, LinkedHashSet<String> termList) {
        // ���зֽ������Ϊ�����ַ����ֵ�������
        HashSet<String> firstCharacters = new HashSet<>();   // ���ַ� ����
        for (String word : termList) {
            if (!firstCharacters.contains(word.substring(0, 1))) {
                firstCharacters.add(word.substring(0, 1));
            }
        }
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

        debug_Info.append("������" + teams);


        // ÿ������ѡһ����ѡ����  ������ÿ�鵹������
        List<String> result_list = new ArrayList<>();
        teams.forEach(list -> {
            String topCandidateFromSet = getTopCandidateFromSet(list);   // ��һ�־���
            result_list.add(topCandidateFromSet);
        });

        // ������ע�͵�
    /*    // ����ÿ������ѡ�����ĺ�ѡ���� �ٽ��е�����
        Map<String, Integer> map = new LinkedHashMap<>();
        for (String word : result_set) {
            map.put(word, wcMap.get(word));
        }
        List<Map.Entry<String, Integer>> infoIds = new ArrayList(map.entrySet());
        //����
        //  System.out.println("�ڶ���ɸѡ����󼯺ϵ�����ɸѡ");
        //  System.out.println("�ڶ���ɸѡ����ǰ" + infoIds);
        Collections.sort(infoIds, (o1, o2) -> (orderByConfidence(o1, o2, false)));    // ������ȶ���
        //   System.out.println("�ڶ���ɸѡ�����" + infoIds);

        //��������Ҫ������ݴ�Сȡ���ս����
        LinkedHashSet final_result = new LinkedHashSet();
        int n = (int) Math.round(Math.sqrt(s_len));
        for (int i = 0; i < result_set.size(); i++) {
            if (final_result.isEmpty()) {
                final_result.add(infoIds.get(0).getKey());
            }
            if (HanUtils.hasNonCommonWithAllAddedResultSet(final_result, infoIds.get(i).getKey())) {
                final_result.add(infoIds.get(i).getKey());
            }
        }
        */
        // ����
        result_list.sort((o1, o2) -> Double.compare(segTermMap.get(o2).score, segTermMap.get(o1).score));

        LinkedHashSet final_result = new LinkedHashSet();
        int n = (int) Math.round(Math.sqrt(s_len));
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


    public int orderByConfidence(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2, boolean isFirstTimeScreen) {
        // System.out.print("o1:" + o1.getKey() + "->" + o1.getValue() + "  o2:" + o2.getKey() + "->" + o2.getValue() + "   ");
        int min = Math.min(o1.getValue(), o2.getValue());
        int max = Math.max(o1.getValue(), o2.getValue());
        double conf = (double) min / max;

        // �����ڵڶ���ɸѡ
        if (min != 1 && (hasCommonStr(o1.getKey(), o2.getKey(), isFirstTimeScreen) && conf > beta)) {   //  �н������ַ��������ʺϵڶ���ɸѡ
            // ������ȡ  ƫ���ַ������Ƚϳ���
           /* System.out.print("������ȡ  ƫ���ַ������Ƚϳ���-->");
            System.out.println(o2.getKey().length() - o1.getKey().length() > 0 ? o2.getKey() : o1.getKey());*/
            return o2.getKey().length() - o1.getKey().length();
        } else {
           /* System.out.print("ȡ��Ƶ�ϳ�-->");
            System.out.println(o2.getValue() - o1.getValue() > 0 ? o2.getKey() : o1.getKey());*/
            return o2.getValue() - o1.getValue();
        }
    }

    //  ����ȡһ��
    private String getTopCandidateFromSet(List<String> termList) {
        Occurrence occurrence = new Occurrence();
        if (termList.size() == 1) {    // һ����Ҳ����ͳ����
            String  seg = termList.get(0);
            Term term = segTermMap.get(seg);
            double score = occurrence.getNormalizedScore(seg);
            term.setScore(score);   // ��ֵ
            return termList.get(0);
        }
        debug_Info.append("\n��ӡ������termList->   " + termList + "\n");
        System.out.println("\n��ӡ������termList->   " + termList + "\n");
        // �����ѡ�ʵ� ����Ϣ �� ��Ϣ��

        for (String seg : termList) {
            Term term = segTermMap.get(seg);
            double score = occurrence.getNormalizedScore(seg);
            term.setScore(score);   // ��ֵ
        }
        // �Ժ�ѡ������ ��һ���÷� ��������
        termList.sort((o1, o2) -> Double.compare(segTermMap.get(o2).score, segTermMap.get(o1).score));
        debug_Info.append("   ��һ��ɸѡ���->   " + termList.get(0) + "\n");
        System.out.println("   ��һ��������->   " + termList + "\n");
        System.out.println("   ��һ��ɸѡ���->   " + termList.get(0) + "\n");
        return termList.get(0);
    }

}
