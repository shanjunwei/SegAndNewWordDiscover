package computer;

import pojo.Term;
import util.HanUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static config.Config.*;
import static config.Constants.*;

/**
 * 词共现 统计量计算,包括 互信息,左右熵
 */
public class Occurrence {
    /**
     * 全部 切分 数量
     */
    float totalTerm;

    /**
     * 切分段 去重后频数累计和
     */
    static float totalCount;
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
            // 1. 计算信息熵
            float rightEntropy = computeRightEntropy(seg);
            RElist.add(rightEntropy);
            //totalRE = totalRE + rightEntropy;
            maxRE = Math.max(maxRE, rightEntropy);  // 求最大右信息熵
            float leftEntropy = computeLeftEntropy(seg);
            LElist.add(leftEntropy);
            // totalLE = totalLE + leftEntropy;
            maxLE = Math.max(maxLE, rightEntropy);  // 求最大左信息熵
            // 2. 计算互信息
            float mi = computeMutualInformation(seg);
            MI_list.add(mi);
            //totalMI = totalMI + mi;
            maxMI = Math.max(maxMI, mi);   // 计算最大互信息
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

    /**
     * 计算左邻熵  测试正确
     */
    public float computeLeftEntropy(String prefix) {
        Set<Map.Entry<String, Integer>> entrySet = trieLeft.prefixSearch(HanUtils.reverseString(prefix));
        return computeEntropy(entrySet, prefix);
    }

    /**
     * 计算右邻熵
     */
    public float computeRightEntropy(String prefix) {
        Set<Map.Entry<String, Integer>> entrySet = trieRight.prefixSearch(prefix);
        return computeEntropy(entrySet, prefix);
    }


    /**
     * 信息熵计算
     */
    private float computeEntropy(Set<Map.Entry<String, Integer>> entrySet, String prefix) {
        float totalFrequency = 0;
        for (Map.Entry<String, Integer> entry : entrySet) {
            if (entry.getKey().length() != prefix.length() + 1) {
                continue;
            }
            totalFrequency += entry.getValue();
        }
        float le = 0;
        for (Map.Entry<String, Integer> entry : entrySet) {
            if (entry.getKey().length() != prefix.length() + 1) {
                continue;
            }
            float p = entry.getValue() / totalFrequency;
            le += -p * Math.log(p);
        }
        return le;
    }

    /**
     * 计算互信息 穷举当前切分的所有可能组合，将互信息全部计算,然后取最小值
     */
    public float computeMutualInformation(String co_occurrence) {
        List<List<String>> possibleCombines = HanUtils.getPossibleCombination(co_occurrence);
        float result = 0.0f;
        for (List<String> combine : possibleCombines) {
            String x = combine.get(0);
            String y = combine.get(1);
            result = computeMI(co_occurrence, x, y);
        }
        return result;
    }

    /**
     * 计算互信息
     */
    private float computeMI(String co_occurrence, String x, String y) {
        float p_xy = (float) Math.max(MIN_PROBABILITY, wcMap.get(co_occurrence) / totalCount);
        int x_count = x.length() == 1 ? singWordCountMap.get(x) : wcMap.get(x);
        float p_x = (float) Math.max(MIN_PROBABILITY, x_count / totalCount);
        int y_count = y.length() == 1 ? singWordCountMap.get(y) : wcMap.get(y);
        float p_y = (float) Math.max(MIN_PROBABILITY, y_count / totalCount);
        return (float) (p_xy * (Math.log(p_xy / (p_x * p_y)) / Math.log(2)) * 1e5);   //return  (Math.log(p_xy / (p_x * p_y)) / Math.log(2)) * 10;
    }

    /**
     * 得到归一化值
     */
    public float getNormalizedScore(Term term) {
        if (term == null) {
            return 0f;
        }
        // Term term = segTermMap.get(seg);
        //term.score = term.mi / totalMI + term.le / totalLE + term.re / totalRE;   // 归一化
        //term.score = term.mi / totalMI + Math.min(term.le / totalLE, term.re / totalRE);   // 01更换归一化策略 -> 取左右熵最小值
        // 用log 函数进行归一化,参考 http://www.cnblogs.com/pejsidney/p/8031250.html
        float normalizedMi = (float) (Math.log(term.mi) / Math.log(maxMI))*2;  // 归一化信息熵
        float normalizedEntropy = (float) (Math.log(Math.min(term.le, term.re)) / Math.log(term.le < term.re ? maxLE : maxRE));  // 归一化左右熵
        term.score = normalizedMi + normalizedEntropy + getEntropyRate(term.le, term.re);          // 加入左右熵紧密程度的度量
        if (DEBUG_MODE)
            System.out.println(term.seg + "   mi->   " + term.mi + "->" + normalizedMi + " le->" + term.le + " re->" + term.re + "->" + normalizedEntropy + " ER->" + getEntropyRate(term.le, term.re) + " score->" + term.score + "\n");
        return term.score;
    }

    /**
     * 信息熵过滤
     */
    public static boolean EntropyFilter(float leftEntropy, float rightEntropy) {
        if (leftEntropy == 0 || rightEntropy == 0) {
            return true;
        }
        float min = Math.min(leftEntropy, rightEntropy);
        float max = Math.max(leftEntropy, rightEntropy);
        if (min / max < entropy_theta) return true;

        return false;
    }

    /**
     * 左右熵比值
     */
    public static float getEntropyRate(float leftEntropy, float rightEntropy) {
        if (leftEntropy == 0 || rightEntropy == 0) {
            return 0;
        }
        float min = Math.min(leftEntropy, rightEntropy);
        float max = Math.max(leftEntropy, rightEntropy);
        return min / max;
    }

    /**
     * 互信息过滤
     */
    public static boolean MutualInformationFilter(float mi) {
        if (mi < MI_THRESHOLD_VALUE) return true;
        return false;
    }

    /**
     * 标准化偏移位置插值,目的是为了让插入的值的长度大小固定
     * TODO: 这里假设句子不够长,最长不过100个字
     */
    public static String getShiftStandardPostion(int left, int right) {
        StringBuilder stringBuilder = new StringBuilder();
        if (left < 10) stringBuilder.append(0);
        stringBuilder.append(left);
        stringBuilder.append("->");
        if (right < 10) stringBuilder.append(0);
        stringBuilder.append(right);
        return stringBuilder.toString();
    }

}
