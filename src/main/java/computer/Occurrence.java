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
    long totalTerm;

    /**
     * 切分段 去重后频数累计和
     */
    static long totalCount;


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
        for (Map.Entry<String, Integer> entry : wcMap.entrySet()) {
            trieRight.put(entry.getKey(), entry.getValue());     // 右前缀字典树
            trieLeft.put(HanUtils.reverseString(entry.getKey()), entry.getValue());  // 左前缀字典树
            totalCount = totalCount + entry.getValue();    // 计算总词频
        }
        for (Map.Entry<String, Integer> entry : wcMap.entrySet()) {
            String seg = entry.getKey();
            int seg_count = entry.getValue();
            // 1. 计算信息熵
            float rightEntropy = computeRightEntropy(seg,seg_count);
           // maxRE = Math.max(maxRE, rightEntropy);  // 求最大右信息熵   //totalRE = totalRE + rightEntropy;
            float leftEntropy = computeLeftEntropy(seg,seg_count);
        //    maxLE = Math.max(maxLE, leftEntropy);  // 求最大左信息熵    // totalLE = totalLE + leftEntropy;
            // 2. 计算互信息
            float mi = computeMutualInformation(seg);
        //    maxMI = Math.max(maxMI, mi);   // 计算最大互信息  //totalMI = totalMI + mi;
            Term term = new Term(seg, seg_count, mi, leftEntropy, rightEntropy);  // 这里没办法算最后得分
            // 将map存入redis中
            /**********************  redis存取 **************************/
            redis.hmset(seg, term.convertToMap());
            /**********************  redis存取 **************************/
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
        wcMap.clear();   // 释放无用的内存
        Term max_term = new Term(MAX_KEY, 0, maxMI, maxLE, maxRE);
        redis.hmset(MAX_KEY, max_term.convertToMap());    // 保存最大值
        System.out.println("统计量计算总耗时: " + (System.currentTimeMillis() - t1) + "ms");
    }

    /**
     * 计算左邻熵
     */
    public float computeLeftEntropy(String prefix, int prefix_count) {
        Set<Map.Entry<String, Integer>> entrySet = trieLeft.prefixSearch(HanUtils.reverseString(prefix));
        return computeEntropy(entrySet, prefix, prefix_count);
    }

    /**
     * 计算右邻熵
     */
    public float computeRightEntropy(String prefix, int prefix_count) {
        Set<Map.Entry<String, Integer>> entrySet = trieRight.prefixSearch(prefix);
        return computeEntropy(entrySet, prefix, prefix_count);
    }

    /**
     * 信息熵计算
     */
    private float computeEntropy(Set<Map.Entry<String, Integer>> entrySet, String prefix, int prefix_count) {
        float totalFrequency = 0;
      /*  for (Map.Entry<String, Integer> entry : entrySet) {
            if (entry.getKey().length() != prefix.length() + 1) {
                continue;
            }
            totalFrequency += entry.getValue();
        }*/
        totalFrequency = prefix_count;
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
            result += computeMI(co_occurrence, x, y);   // 累加
        }
        return result;
    }

    /**
     * 计算互信息
     */
    private float computeMI(String co_occurrence, String x, String y) {
        double p_xy = Math.max(MIN_PROBABILITY, (double) wcMap.get(co_occurrence) / (double) totalCount);
        int x_count = x.length() == 1 ? singWordCountMap.get(x) : wcMap.get(x);
        double p_x = Math.max(MIN_PROBABILITY, (double) x_count / (double) totalCount);
        int y_count = y.length() == 1 ? singWordCountMap.get(y) : wcMap.get(y);
        double p_y = Math.max(MIN_PROBABILITY, (double) y_count / (double) totalCount);
        return (float) (p_xy * (Math.log(p_xy / (p_x * p_y)) / Math.log(2)) * 1e5);   //return  (Math.log(p_xy / (p_x * p_y)) / Math.log(2)) * 10;
    }

    /**
     * 得到归一化值
     */
    public float getNormalizedScore(Term term) {
        if (term == null) {
            return 0f;
        }

        if (DEBUG_MODE)
            System.out.println("   maxMI->   " + maxMI + "maxLE->" + maxLE + " maxRE->" + maxRE);
        //term.score = term.mi / totalMI + term.le / totalLE + term.re / totalRE;   // 归一化
        //term.score = term.mi / totalMI + Math.min(term.le / totalLE, term.re / totalRE);   // 01更换归一化策略 -> 取左右熵最小值
        // 用log 函数进行归一化,参考 http://www.cnblogs.com/pejsidney/p/8031250.html
        float normalizedMi = (float) (Math.log(term.mi) / Math.log(maxMI)) * 2;  // 归一化信息熵
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
        if (min / max < ENTROPY_THETA) return true;

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
    public static String getShiftStandardPosition(int left, int right) {
        StringBuilder stringBuilder = new StringBuilder();
        if (left < 10) stringBuilder.append(0);
        stringBuilder.append(left);
        stringBuilder.append("->");
        if (right < 10) stringBuilder.append(0);
        stringBuilder.append(right);
        return stringBuilder.toString();
    }
}
/*  *//**
 * 信息熵计算
 *//*
    private float computeEntropy2(Set<Map.Entry<String, Term>> entrySet, String prefix) {
        float totalFrequency = 0;
        for (Map.Entry<String, Term> entry : entrySet) {
            if (entry.getKey().length() != prefix.length() + 1) {
                continue;
            }
            totalFrequency += entry.getValue().getCount();
        }
        float le = 0;
        for (Map.Entry<String, Term> entry : entrySet) {
            if (entry.getKey().length() != prefix.length() + 1) {
                continue;
            }
            float p = entry.getValue().getCount() / totalFrequency;
            le += -p * Math.log(p);
        }
        return le;
    }
*/