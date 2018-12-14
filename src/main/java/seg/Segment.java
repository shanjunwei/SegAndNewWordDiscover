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
        /*int count = 0;
        try {
            // 以utf-8读取文件
            FileInputStream fis = new FileInputStream(ErrorSegPath);
            InputStreamReader reader = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(reader);
            String str = null;
            while ((str = br.readLine()) != null) {
                if (count > 1000) {
                    break;
                }
                if (StringUtils.isNotBlank(str)) {
                    List<String> result = segment.segment(str);
                    count++;
                    System.out.println("\n*************************分词结果集" + result + "*************************\n");
                }
            }
            br.close();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    /**
     * 暴露给外部调用的分词接口
     * 传进来的参数 没有非中文字符，是一个句子
     */
    public List<String> segment(String text) {
        if (StringUtils.isBlank(text)) return null;
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
        if (StringUtils.isBlank(text)) return "";

        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(text);
        List<String> exactWords = new ArrayList<>();
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // 这里没有逗号
            if (StringUtils.isNotBlank(textDS)) {
                //List<String> termList = HanUtils.getFMMList(textDS, false);    // 置信度比较的是这里面的值
                List<Term> termList = HanUtils.segmentToTerm(textDS, false);
                // 词提取
                if (termList == null) {    //  单独一个字的
                    exactWords.add(textDS);
                } else {
                    List<String> result = extractWordsFromNGram(termList);
                    exactWords.addAll(result);
                }
            }
        }

        text = HanUtils.handleSentenceWithExtractWords(text,exactWords);  //  先处理抽词
        //  正则特殊字符处理
        boolean hasSpecialCharacter = (boolean) HanUtils.makeQueryStringAllRegExp(text).get(HAS_SPECIAL_CHAR);
        if (hasSpecialCharacter)
            text = String.valueOf(HanUtils.makeQueryStringAllRegExp(text).get(STR_REPLACE_SPECIAL));   // 先处理特殊字符
        // 除去非中文字符和抽取出来的词,剩下的都是词了
        for (String nonChinese : HanUtils.replaceNonChineseCharacterAddBlank(text)) {
            if (StringUtils.isNotBlank(nonChinese)) {
                text = text.replaceAll(nonChinese, " " + nonChinese + " ");
            }
        }
//        for (String seg : exactWords) {
//            text = text.replaceAll(seg, " " + seg + " ");    // 这样做有一定隐患 比如 5 25 ;孙少平 少平
//        }
        text = text.trim();   // 去首尾空格
        text = text.replaceAll("\\s{1,}", " ");  // 去连续空格
        // 还原
        if (hasSpecialCharacter) text = HanUtils.getOriginalStr(text);
        return text;
    }

    /**
     * 原字符串长度  s指挥警察四处奔忙  取候选集前根号len(s)个  ,s为待FMM 切分串
     *
     * @param termList 候选集   FMM 切分串
     * @return 置信度过滤， 过滤的结果无交集
     */
    public List<String> extractWordsFromNGram(List<Term> termList) {
        // 将切分结果集分为以首字符区分的若干组
        List<List<Term>> teams = new ArrayList<>();  //  分组集合
        //List<String> seg_list = new ArrayList<>(termList);
        int p = 0;
        String history = termList.get(0).getSeg().substring(0, 1);
        String seg = termList.get(0).getSeg();
        String firstChar = seg.substring(0, 1);  // 首字符
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
        // 每个组挑选一个候选对象  方法是每组倒序排列
        List<Term> result_list = new ArrayList<>();
        teams.forEach(list -> {
            Term topCandidateFromSet = getTopCandidateFromSet(list);   // 第一轮决策
            if (topCandidateFromSet != null && StringUtils.isNotBlank(topCandidateFromSet.getSeg())) {
                result_list.add(topCandidateFromSet);
            }
        });
        // 排序
        if (DEBUG_MODE) System.out.println("第二轮筛选前: " + result_list);
        result_list.sort((o1, o2) -> Double.compare(segTermMap.get(o2.getSeg()).score, segTermMap.get(o1.getSeg()).score));
        if (DEBUG_MODE) System.out.println("第二轮排序后: " + result_list);
        List<Term> final_result = new ArrayList<>();
        for (int i = 0; i < result_list.size(); i++) {
            if (final_result.isEmpty()) {
                final_result.add(result_list.get(0));
            }
            if (HanUtils.hasNonCommonWithAllAddedResultSet(final_result, result_list.get(i))) {
                final_result.add(result_list.get(i));
            }
        }
        List<String> result = new ArrayList<>();
        for (Term word : final_result) {
            result.add(word.getSeg());
        }
        return result;
    }

    //  第一轮筛选
    private Term getTopCandidateFromSet(List<Term> termList) {
        if (DEBUG_MODE) System.out.println("   第一轮筛选前->   " + termList + "\n");
        List<Term> result = new ArrayList<>();
        Occurrence occurrence = new Occurrence();
        if (termList.size() == 1) {    // 一个的也计算统计量
            Term seg = termList.get(0);
            Term term = segTermMap.get(seg);
            float score = occurrence.getNormalizedScore(term);
            if (term == null) {
                Term term1 = new Term();
                term1.setScore(score);
                segTermMap.put(seg.getSeg(), term1);
            } else {
                term.setScore(score);   // 赋值
            }
            return termList.get(0);
        }
        // 计算候选词的 互信息 和 信息熵
        for (Term seg : termList) {
            Term term = segTermMap.get(seg.getSeg());
            if (term != null) {
                // 信息熵过滤
                if (occurrence.EntropyFilter(term.le, term.re)) { // 过滤掉信息熵过滤明显不是词的
                    if (DEBUG_MODE)
                        System.out.println("信息熵过滤-> " + seg + "   mi->   " + term.mi + " le->" + term.le + " re->" + term.re);
                    term.setScore(0);
                }  // 互信息过滤
                else if (occurrence.MutualInformationFilter(term.mi)) {
                    if (DEBUG_MODE)
                        System.out.println("互信息过滤-> " + seg + "   mi->   " + term.mi + " le->" + term.le + " re->" + term.re);
                    term.setScore(0);
                } else {
                    float score = occurrence.getNormalizedScore(term);
                    term.setScore(score);   // 赋值
                    result.add(seg);
                }
            }
        }
        // 对候选集根据 归一化得分 降序排列
        result.sort((o1, o2) -> Double.compare(segTermMap.get(o2).score, segTermMap.get(o1).score));
        if (DEBUG_MODE) System.out.println("   第一轮排序后*****->   " + result + "\n");
        //System.out.println("   第一轮筛选结果->   " + termList.get(0) + "\n");
        return result.size() == 0 ? null : result.get(0);
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