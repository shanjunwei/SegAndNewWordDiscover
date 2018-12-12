package computer;

import config.Config;
import pojo.Term;
import serilize.JsonSerilizable;
import util.FileUtils;
import util.HanUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static config.Config.MIN_PROBABILITY;
import static config.Constants.*;

/**
 * 词共现 统计量计算,包括 互信息,左右熵
 */
public class Occurrence {

    /**
     * 全部 切分 数量
     */
    double totalTerm;

    /**
     * 切分段 去重后频数累计和
     */
    static double totalCount;

    /**
     * 切分段去重后 总互信息
     */
    static double totalMI;


    /**
     * 切分段去重后  总左熵
     */
    static double totalLE;

    /**
     * 切分段去重后  总右熵
     */
    static double totalRE;


    public Occurrence() {
    }

    /**
     * 添加所有切分词  计算互信息 信息熵等统计量 ,对外提供这一个入口即可
     */
    public void addAllSegAndCompute(Map<String, Integer> wcMap) {
        System.out.println("计算统计量中-->");
        int count = 0;
        long t1 = System.currentTimeMillis();

        totalTerm = wcMap.size();
        for (String seg : wcMap.keySet()) {
            trieRight.put(seg, wcMap.get(seg));     // 右前缀字典树
            trieLeft.put(HanUtils.reverseString(seg), wcMap.get(seg));  // 左前缀字典树
            totalCount = totalCount + wcMap.get(seg);    // 计算总词频
        }
        for (String seg : wcMap.keySet()) {
            //System.out.println("====计算信息熵-->"+seg);
            // 1. 计算信息熵
            double rightEntropy = computeRightEntropy(seg);
            RElist.add(rightEntropy);
            totalRE = totalRE + rightEntropy;
            //  debug_Info.append(seg + "   :右邻信息熵->  " + rightEntropy + "\n");
            double leftEntropy = computeLeftEntropy(seg);
            LElist.add(leftEntropy);
            totalLE = totalLE + leftEntropy;
            // debug_Info.append(seg + "   :左邻信息熵->  " + leftEntropy + "\n"); // double entropy  =  Math.min(rightEntropy,leftEntropy);
            // 2. 计算互信息
            double mi = computeMutualInformation(seg);
            MI_list.add(mi);
            totalMI = totalMI + mi;
            Term term = new Term(seg, wcMap.get(seg), mi, leftEntropy, rightEntropy);
            segTermMap.put(seg, term);

            count++;
            if (count % 100000 == 0) {
                System.out.print("*");
            }

            if (count == 100000 * 100) {
                System.out.println();
                count = 0;
                continue;
            }
        }
        System.out.println("统计量计算总耗时: " + (System.currentTimeMillis() - t1) + "ms");
    }


    public void serilizableStatisticsToFile() {
        //  将三份统计量分别存于文件中便于分析
      /*  FileUtils.writeFileToPath(Config.segTermMapPath, RElist);
        FileUtils.writeFileToPath(Config.segTermMapPath, LElist);
        FileUtils.writeFileToPath("D:\\HanLP\\最新效果\\MI_list", MI_list);*/
        // 保存总量统计信息
        Term term = new Term("####", 0, totalMI, totalLE, totalRE);
        segTermMap.put("####", term);
        try {
            JsonSerilizable.serilizableForMap(segTermMap, Config.segTermMapPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        Occurrence occurrence = new Occurrence();
        occurrence.deserilizableStatistics();
    }

    /**
     * 反序列化统计量到内存
     */
    public void deserilizableStatistics() {
        long  t1  = System.currentTimeMillis();
        try {
            segTermMap = JsonSerilizable.deserilizableForMapFromFile(Config.segTermMapPath);
            totalRE = segTermMap.get("####").getRe();
            totalLE = segTermMap.get("####").getLe();
            totalMI = segTermMap.get("####").getMi();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long  t2  = System.currentTimeMillis();
        System.out.println("反序列化文件到内存耗时:  "+(t2-t1) +" ms");
    }

    /**
     * 计算左邻熵  测试正确
     */
    public double computeLeftEntropy(String prefix) {
        Set<Map.Entry<String, Integer>> entrySet = trieLeft.prefixSearch(HanUtils.reverseString(prefix));
        return computeEntropy(entrySet, prefix);
    }

    /**
     * 计算右邻熵
     */
    public double computeRightEntropy(String prefix) {
        Set<Map.Entry<String, Integer>> entrySet = trieRight.prefixSearch(prefix);
        return computeEntropy(entrySet, prefix);
    }


    /**
     * 信息熵计算
     */
    private double computeEntropy(Set<Map.Entry<String, Integer>> entrySet, String prefix) {
        double totalFrequency = 0;
        for (Map.Entry<String, Integer> entry : entrySet) {
            if (entry.getKey().length() != prefix.length() + 1) {
                continue;
            }
            totalFrequency += entry.getValue();
        }
        double le = 0;
        for (Map.Entry<String, Integer> entry : entrySet) {
            if (entry.getKey().length() != prefix.length() + 1) {
                continue;
            }
            double p = entry.getValue() / totalFrequency;
            le += -p * Math.log(p);
        }
        return le;
    }

    /**
     * 计算互信息 穷举当前切分的所有可能组合，将互信息全部计算,然后取最小值
     */
    public double computeMutualInformation(String co_occurrence) {
        List<List<String>> possibleCombines = HanUtils.getPossibleCombination(co_occurrence);
        double result = 10e20;
        for (List<String> combine : possibleCombines) {
            String x = combine.get(0);
            String y = combine.get(1);
            result = Math.min(computeMI(co_occurrence, x, y), result);
        }
        return result;
    }

    // 改变思路,一开始就先全部算好

    private double computeMI(String co_occurrence, String x, String y) {
        double p_xy = Math.max(MIN_PROBABILITY, wcMap.get(co_occurrence) / totalCount);
        int x_count = x.length() == 1 ? singWordCountMap.get(x) : wcMap.get(x);
        double p_x = Math.max(MIN_PROBABILITY, x_count / totalCount);
        int y_count = y.length() == 1 ? singWordCountMap.get(y) : wcMap.get(y);
        double p_y = Math.max(MIN_PROBABILITY, y_count / totalCount);
        //return  (Math.log(p_xy / (p_x * p_y)) / Math.log(2)) * 10;
        return p_xy * (Math.log(p_xy / (p_x * p_y)) / Math.log(2)) * 1e5;
    }

    /**
     * 得到归一化值
     */
    public double getNormalizedScore(String seg) {
        //Term term = JSON.parseObject(String.valueOf(segTermMap.get(seg)),new TypeReference<Term>() {});
        Term term = segTermMap.get(seg);
        if(term == null){
            System.out.println("计算结果中不包含->  "+seg);
            return 0;
        }
        term.score = term.mi / totalMI + term.le / totalLE + term.re / totalRE;   // 归一化
        //更换归一化策略
        term.score  =  term.mi / totalMI + Math.min(term.le / totalLE,term.re / totalRE);
        if(DEBUG_MODE) System.out.println(seg + "   mi->   " + term.mi + " le->" + term.le + " re->" + term.re + " wc->" + term.count + " score->" + term.score + "\n");
        //  term.score *= totalTerm;  // 这个先不加看看结果
        return term.score;
    }
}
