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
 * 基于互信息 和信息熵的分词与新词发现
 */
public class Segment {
    private static boolean debug_text_lenth = false;   // debug 显示信息控制，长度大于4的才显示
    public static StringBuilder debug_Info = new StringBuilder();   // debug 信息，用于存于文件中
    public static LinkedHashSet<String> seg_final_result = new LinkedHashSet<>();   // 分词最终候选结果

    public static void main(String[] args) {
        // 数据预处理
        PreProcess preProcess = new PreProcess();
        preProcess.initData();

        // 将所有切分片段 加载进 字典树，方便进行信息熵的计算
        Occurrence occurrence = new Occurrence();
        occurrence.addAllSegAndCompute(PreProcess.seg_result, wcMap);

        //  将信息熵 互信息等统计量加入到过滤决策机制中
        Segment segment = new Segment();
        segment.doConfidenceCalculation();
    }

    public void doConfidenceCalculation() {
        // 置信度计算
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(PreProcess.novel_text);  // 去掉非中文字符   里边没有逗号
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // 这里没有逗号
            System.out.println("原字符串1=>" + textDS);
            debug_Info.append("原字符串1=>" + textDS + "\n");
            if (StringUtils.isNotBlank(textDS) && textDS.length() != 1) {
                String text = textDS;
                if (StringUtils.isNotBlank(text) && text.length() != 1) {
                    debug_text_lenth = text.length() > 4 ? true : false;
                    if (debug_text_lenth) debug_Info.append("原字符串2=>" + text + "\n");
                    LinkedHashSet<String> termList = HanUtils.segment(text, false);    // 置信度比较的是这里面的值
                    if (debug_text_lenth) debug_Info.append("切分字串=>");
                    if (debug_text_lenth) debug_Info.append("\n");
                    System.out.println();
                    // 取过滤
                    LinkedHashSet result = fiterByConfidence(text.length(), termList);
                    seg_final_result.addAll(result);
                    if (debug_text_lenth)
                        debug_Info.append("\n ***************************结果集->" + result + "***************************************\n");
                }
                if (debug_text_lenth) debug_Info.append("\n");
                System.out.println();
            }
        }

        // 最终结果排序输出
        List<String> list = new ArrayList<>(seg_final_result);
        list.sort(Comparator.comparing(HanUtils::firstPinyinCharStr));
        //seg_final_result.stream().sorted((o1, o2) -> HanUtils.firstPinyinCharStr(o2).compareTo(HanUtils.firstPinyinCharStr(o1)));
        //  FileUtils.writeStringToFile("D:\\HanLP\\result235.txt",String.valueOf(list));
/*        FileUtils.writeFileToPath("D:\\BigData\\HanLP\\天龙八部\\result.txt",list,wcMap);
        FileUtils.writeStringToFile("D:\\BigData\\HanLP\\天龙八部\\debug.txt", debug_Info.toString());*/
        FileUtils.writeStringToFile(Config.DebugPath, debug_Info.toString());
        System.out.println();
    }


    /**
     * @param s_len    原字符串长度  s指挥警察四处奔忙  取候选集前根号len(s)个  ,s为待FMM 切分串
     * @param termList 候选集  指挥->117 指挥警->2 指挥警察->2 挥警->2 挥警察->2 挥警察四->2 警察->51 警察四->2 警察四处->2 察四->2 察四处->2 察四处奔->2 四处->35 四处奔->6 四处奔忙->2 处奔->10 处奔忙->2 奔忙->4
     * @return 置信度过滤，过滤的结果无交集
     */
    public LinkedHashSet<String> fiterByConfidence(int s_len, LinkedHashSet<String> termList) {
        // 将切分结果集分为以首字符区分的若干组
        HashSet<String> firstCharacters = new HashSet<>();   // 首字符 集合
        for (String word : termList) {
            if (!firstCharacters.contains(word.substring(0, 1))) {
                firstCharacters.add(word.substring(0, 1));
            }
        }
        List<List<String>> teams = new ArrayList<>();  //  分组集合
        List<String> seg_list = new ArrayList<>(termList);
        int p = 0;
        String history = seg_list.get(0).substring(0, 1);
        String seg = seg_list.get(0);
        String firstChar = seg.substring(0, 1);  // 首字符
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

        debug_Info.append("分组结果" + teams);


        // 每个组挑选一个候选对象  方法是每组倒序排列
        List<String> result_list = new ArrayList<>();
        teams.forEach(list -> {
            String topCandidateFromSet = getTopCandidateFromSet(list);   // 第一轮决策
            result_list.add(topCandidateFromSet);
        });

        // 将排序注释掉
    /*    // 根据每个组挑选出来的候选对象 再进行倒排序
        Map<String, Integer> map = new LinkedHashMap<>();
        for (String word : result_set) {
            map.put(word, wcMap.get(word));
        }
        List<Map.Entry<String, Integer>> infoIds = new ArrayList(map.entrySet());
        //排序
        //  System.out.println("第二轮筛选，最后集合的排序筛选");
        //  System.out.println("第二轮筛选排序前" + infoIds);
        Collections.sort(infoIds, (o1, o2) -> (orderByConfidence(o1, o2, false)));    // 排序的稳定性
        //   System.out.println("第二轮筛选排序后" + infoIds);

        //根据最终要求的数据大小取最终结果集
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
        // 排序
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

        // 适用于第二轮筛选
        if (min != 1 && (hasCommonStr(o1.getKey(), o2.getKey(), isFirstTimeScreen) && conf > beta)) {   //  有交集的字符串处理，适合第二轮筛选
            // 两个都取  偏向字符串长度较长的
           /* System.out.print("两个都取  偏向字符串长度较长的-->");
            System.out.println(o2.getKey().length() - o1.getKey().length() > 0 ? o2.getKey() : o1.getKey());*/
            return o2.getKey().length() - o1.getKey().length();
        } else {
           /* System.out.print("取词频较长-->");
            System.out.println(o2.getValue() - o1.getValue() > 0 ? o2.getKey() : o1.getKey());*/
            return o2.getValue() - o1.getValue();
        }
    }

    //  倒序取一个
    private String getTopCandidateFromSet(List<String> termList) {
        Occurrence occurrence = new Occurrence();
        if (termList.size() == 1) {    // 一个的也计算统计量
            String  seg = termList.get(0);
            Term term = segTermMap.get(seg);
            double score = occurrence.getNormalizedScore(seg);
            term.setScore(score);   // 赋值
            return termList.get(0);
        }
        debug_Info.append("\n打印分组后的termList->   " + termList + "\n");
        System.out.println("\n打印分组后的termList->   " + termList + "\n");
        // 计算候选词的 互信息 和 信息熵

        for (String seg : termList) {
            Term term = segTermMap.get(seg);
            double score = occurrence.getNormalizedScore(seg);
            term.setScore(score);   // 赋值
        }
        // 对候选集根据 归一化得分 降序排列
        termList.sort((o1, o2) -> Double.compare(segTermMap.get(o2).score, segTermMap.get(o1).score));
        debug_Info.append("   第一轮筛选结果->   " + termList.get(0) + "\n");
        System.out.println("   第一轮排序结果->   " + termList + "\n");
        System.out.println("   第一轮筛选结果->   " + termList.get(0) + "\n");
        return termList.get(0);
    }

}
