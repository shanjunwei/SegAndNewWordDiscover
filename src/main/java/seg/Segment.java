package seg;

import computer.Occurrence;
import pojo.Term;
import util.HanUtils;

import java.util.*;

import static config.Constants.segTermMap;
import static config.Constants.wcMap;

/**
 * 基于互信息 和信息熵的分词与新词发现
 */
public class Segment {
    // private static boolean debug_text_lenth = false;   // debug 显示信息控制，长度大于4的才显示
    //public static StringBuilder debug_Info = new StringBuilder();   // debug 信息，用于存于文件中
    // 数据预处理先做
    static {
        PreProcess preProcess = new PreProcess();
        preProcess.initData();
        // 将所有切分片段 加载进 字典树，方便进行信息熵的计算
        Occurrence occurrence = new Occurrence();
        occurrence.addAllSegAndCompute(wcMap);
    }

    public static void main(String[] args) {
        //  将信息熵 互信息等统计量加入到过滤决策机制中
        Segment segment = new Segment();
        List<String> result = segment.segment("表示期待政府能在新的一年里采取有力措施");
        System.out.println("\n*************************分词结果集"+result+"*************************\n");
        List<String> result2 = segment.segment("带着对周恩来刻骨铭心的眷恋离开了人世");
        System.out.println("\n*************************分词结果集"+result2+"*************************\n");
       // System.out.println(result2);
    }

    /**
     * 暴露给外部调用的分词接口
     * 传进来的参数 没有非中文字符，是一个句子
     */
    public List<String> segment(String text) {
        String segResult = segmentToString(text);
        return Arrays.asList(segResult.split(" "));
    }

    /**
     * 暴露给外部调用的分词接口
     * 传进来的参数 没有非中文字符，一个句子
     */
    public String segmentToString(String text) {
        System.out.println("元句子————>  "+text);
        List<String> termList = HanUtils.getFMMList(text, false);    // 置信度比较的是这里面的值
        // 词提取
        LinkedHashSet<String> result = extractWordsFromNGram(text.length(), termList);
        // 剩下的都是词了
        for (String seg : result) {
            text = text.replaceAll(seg, " " + seg + " ");
        }
        text = text.trim();   // 去首尾空格
        text =text.replaceAll("\\s{1,}", " ");  // 去连续空格
        return text;
    }


    /**
     * @param s_len    原字符串长度  s指挥警察四处奔忙  取候选集前根号len(s)个  ,s为待FMM 切分串
     * @param termList 候选集  指挥->117 指挥警->2 指挥警察->2 挥警->2 挥警察->2 挥警察四->2 警察->51 警察四->2 警察四处->2 察四->2 察四处->2 察四处奔->2 四处->35 四处奔->6 四处奔忙->2 处奔->10 处奔忙->2 奔忙->4
     * @return 置信度过滤，过滤的结果无交集
     */
    public LinkedHashSet<String> extractWordsFromNGram(int s_len, List<String> termList) {
        // 将切分结果集分为以首字符区分的若干组
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

        // debug_Info.append("分组结果" + teams);
        // 每个组挑选一个候选对象  方法是每组倒序排列
        List<String> result_list = new ArrayList<>();
        teams.forEach(list -> {
            String topCandidateFromSet = getTopCandidateFromSet(list);   // 第一轮决策
            result_list.add(topCandidateFromSet);
        });

        // 排序
        System.out.println("第二轮筛选前: "+result_list);
        result_list.sort((o1, o2) -> Double.compare(segTermMap.get(o2).score, segTermMap.get(o1).score));
        System.out.println("第二轮排序后: "+result_list);
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

    //  第一轮筛选
    private String getTopCandidateFromSet(List<String> termList) {
        Occurrence occurrence = new Occurrence();
        if (termList.size() == 1) {    // 一个的也计算统计量
            String seg = termList.get(0);
            Term term = segTermMap.get(seg);
            double score = occurrence.getNormalizedScore(seg);
            term.setScore(score);   // 赋值
            return termList.get(0);
        }
        //  debug_Info.append("\n打印分组后的termList->   " + termList + "\n");
        System.out.println("\n打印分组后的termList->   " + termList + "\n");
        // 计算候选词的 互信息 和 信息熵

        for (String seg : termList) {
            Term term = segTermMap.get(seg);
            double score = occurrence.getNormalizedScore(seg);
            term.setScore(score);   // 赋值
        }
        // 对候选集根据 归一化得分 降序排列
        termList.sort((o1, o2) -> Double.compare(segTermMap.get(o2).score, segTermMap.get(o1).score));
        // debug_Info.append("   第一轮筛选结果->   " + termList.get(0) + "\n");
        System.out.println("   第一轮排序结果->   " + termList + "\n");
        System.out.println("   第一轮筛选结果->   " + termList.get(0) + "\n");
        return termList.get(0);
    }

}


/*// 输出总结果
    public void doConfidenceCalculation() {
        // 置信度计算
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(PreProcess.novel_text);  // 去掉非中文字符   里边没有逗号
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // 这里没有逗号
            System.out.println("原字符串1=>" + textDS);
           // debug_Info.append("原字符串1=>" + textDS + "\n");
            if (StringUtils.isNotBlank(textDS) && textDS.length() != 1) {
                String text = textDS;
                debug_text_lenth = text.length() > 4 ? true : false;
                LinkedHashSet<String> termList = HanUtils.segment(text, false);    // 置信度比较的是这里面的值
      *//*          if (debug_text_lenth) debug_Info.append("切分字串=>");
                if (debug_text_lenth) debug_Info.append("\n");*//*
                // 取过滤
                LinkedHashSet result = extractWordsFromNGram(text.length(), termList);
                seg_final_result.addAll(result);
               // if (debug_text_lenth)
                  //  debug_Info.append("\n ***************************结果集->" + result + "***************************************\n");
               // if (debug_text_lenth) debug_Info.append("\n");
            }
        }

        // 最终结果排序输出
        List<String> list = new ArrayList<>(seg_final_result);
        list.sort(Comparator.comparing(HanUtils::firstPinyinCharStr));
        FileUtils.writeStringToFile(Config.DebugPath, debug_Info.toString());
        System.out.println();
    }*/