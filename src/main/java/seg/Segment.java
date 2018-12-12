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
 * 基于互信息 和信息熵的分词与新词发现
 */
public class Segment {
    //public static StringBuilder debug_Info = new StringBuilder();   // debug 信息，用于存于文件中
    // 数据预处理先做
    static {
        //JsonSerializationUtil.serilizableStatisticsToFile();    // 序列化计算结果
        JsonSerializationUtil.deserilizableStatistics();    // 反序列化
    }
    public static void main(String[] args) {
        String  test_text = args[0];     // 测试文本通过标准输入传入
        //  将信息熵 互信息等统计量加入到过滤决策机制中
        Segment segment = new Segment();
        //NovelTest = true;
        DEBUG_MODE = true;
        List<String> result = segment.segment(test_text);
        System.out.println("\n*************************分词结果集" + result + "*************************\n");
    }

    /**
     * 暴露给外部调用的分词接口
     * 传进来的参数 没有非中文字符，是一个句子
     */
    public List<String> segment(String text) {
        long t1 = System.currentTimeMillis();
        String segResult = segmentByNonChineseFilter(text);
        System.out.println("分词耗时: " + (System.currentTimeMillis() - t1) + "  ms");
        return Arrays.asList(segResult.split(" "));
    }
    /**
     * 暴露给外部调用的分词接口    TODO*************************TODO*******
     * 传进来的参数 没有非中文字符，一个句子  TODO*************************TODO*******
     * 评价程序调用的是这个函数*****************TODO*************************TODO*******
     */
    public String segmentToString(String text) {
        return segmentByNonChineseFilter(text);
    }

    /**
     * //TODO  年份识别
     * 暴露给外部调用的分词接口
     * 传进来的参数 没有非中文字符，一个句子
     */
    public String segmentByNonChineseFilter(String text) {
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(text);
        List<String> exactWords = new ArrayList<>();
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // 这里没有逗号
            if (StringUtils.isNotBlank(textDS)) {
                List<String> termList = HanUtils.getFMMList(textDS, false);    // 置信度比较的是这里面的值
                // 词提取
                if (termList == null) {    //  单独一个字的
                    exactWords.add(textDS);
                } else {
                    LinkedHashSet<String> result = extractWordsFromNGram(termList);
                    exactWords.addAll(result);
                }
            }
        }
        //  正则特殊字符处理
        boolean hasSpecialCharacter = (boolean) HanUtils.makeQueryStringAllRegExp(text).get(HAS_SPECIAL_CHAR);
        text = String.valueOf(HanUtils.makeQueryStringAllRegExp(text).get(STR_REPLACE_SPECIAL));   // 先处理特殊字符
        // 除去非中文字符和抽取出来的词,剩下的都是词了
        for (String nonChinese : HanUtils.replaceNonChineseCharacterAddBlank(text)) {
            if (StringUtils.isNotBlank(nonChinese)) {
                text = text.replaceAll(nonChinese, " " + nonChinese + " ");
            }
        }
        for (String seg : exactWords) {
            text = text.replaceAll(seg, " " + seg + " ");
        }
        text = text.trim();   // 去首尾空格
        text = text.replaceAll("\\s{1,}", " ");  // 去连续空格
        // 还原
        if (hasSpecialCharacter) text = HanUtils.getOriginalStr(text);
        return text;
    }
    /**
     * 原字符串长度  s指挥警察四处奔忙  取候选集前根号len(s)个  ,s为待FMM 切分串
     *
     * @param termList 候选集  指挥->117 指挥警->2 指挥警察->2 挥警->2 挥警察->2 挥警察四->2 警察->51 警察四->2 警察四处->2 察四->2 察四处->2 察四处奔->2 四处->35 四处奔->6 四处奔忙->2 处奔->10 处奔忙->2 奔忙->4
     * @return 置信度过滤， 过滤的结果无交集
     */
    public LinkedHashSet<String> extractWordsFromNGram(List<String> termList) {

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
            if (StringUtils.isNotBlank(topCandidateFromSet)) {
                result_list.add(topCandidateFromSet);
            }
        });

        // 排序
        if (DEBUG_MODE) System.out.println("第二轮筛选前: " + result_list);
        result_list.sort((o1, o2) -> Double.compare(segTermMap.get(o2).score, segTermMap.get(o1).score));
        if (DEBUG_MODE) System.out.println("第二轮排序后: " + result_list);
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

    //  第一轮筛选
    private String getTopCandidateFromSet(List<String> termList) {
        if (DEBUG_MODE) System.out.println("   第一轮筛选前->   " + termList + "\n");
        List<String> result = new ArrayList<>();
        Occurrence occurrence = new Occurrence();
        if (termList.size() == 1) {    // 一个的也计算统计量
            String seg = termList.get(0);
            Term term = segTermMap.get(seg);
            double score = occurrence.getNormalizedScore(seg);
            if (term == null) {
                Term term1 = new Term();
                term1.setScore(score);
                segTermMap.put(seg, term1);
            } else {
                term.setScore(score);   // 赋值
            }
            return termList.get(0);
        }
        // 计算候选词的 互信息 和 信息熵
        for (String seg : termList) {
            Term term = segTermMap.get(seg);
            if (term != null) {
                if (HanUtils.EntropyFilter(term.le, term.re)) { // 过滤掉信息熵过滤明显不是词的
                    if (DEBUG_MODE) System.out.println("信息熵过滤-> "  + seg + "   mi->   " + term.mi +" le->" + term.le + " re->" + term.re);
                    term.setScore(0);
                } else {
                    double score = occurrence.getNormalizedScore(seg);
                    term.setScore(score);   // 赋值
                    result.add(seg);
                }
            }
        }
        // 对候选集根据 归一化得分 降序排列
        result.sort((o1, o2) -> Double.compare(segTermMap.get(o2).score, segTermMap.get(o1).score));
        if (DEBUG_MODE) System.out.println("   第一轮排序后->   " + result + "\n");
        //System.out.println("   第一轮筛选结果->   " + termList.get(0) + "\n");
        return result.size() == 0 ? null : termList.get(0);
    }

}


// 读取小说文本
        /*String novel = FileUtils.readFileToString(Config.NovelPath);
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(novel);  // 去掉非中文字符   里边没有逗号
        // 再拆分停用词
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // 这里没有逗号
            if (StringUtils.isNotBlank(textDS) && textDS.length() != 1) {
                System.out.println("原文本---> "+textDS);
                System.out.println("切分后---> "+segment.segment(textDS));
            }
        }*/