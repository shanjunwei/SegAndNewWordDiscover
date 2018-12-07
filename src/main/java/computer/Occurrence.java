package computer;

import org.apache.commons.lang.StringUtils;
import pojo.Term;
import util.HanUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static config.Config.MIN_PROBABILITY;
import static config.Constants.*;
import static seg.Segment.debug_Info;

/**
 * �ʹ��� ͳ��������,���� ����Ϣ,������
 */
public class Occurrence {

    /**
     * ȫ�� �з� ����
     */
    double totalTerm;

    /**
     * �зֶ� ȥ�غ�Ƶ���ۼƺ�
     */
    static double totalCount;

    /**
     * �зֶ�ȥ�غ� �ܻ���Ϣ
     */
    static double totalMI;


    /**
     * �зֶ�ȥ�غ�  ������
     */
    static double totalLE;

    /**
     * �зֶ�ȥ�غ�  ������
     */
    static double totalRE;


    public Occurrence() {
    }

    /**
     * ��������зִ�  ���㻥��Ϣ ��Ϣ�ص�ͳ���� ,�����ṩ��һ����ڼ���
     */
    public void addAllSegAndCompute(Set<String> segSet, Map<String, Integer> wcMap) {
        System.out.println("����ͳ������-->");
        int count = 0;
        long t1 = System.currentTimeMillis();

        totalTerm = segSet.size();
        for (String seg : segSet) {
            trieRight.put(seg, wcMap.get(seg));
            trieLeft.put(HanUtils.reverseString(seg), wcMap.get(seg));
            totalCount = totalCount + wcMap.get(seg);    // �����ܴ�Ƶ
        }
        for (String seg : segSet) {
            if (seg.equals("����")) {
                System.out.println("=============");
            }
            // 1. ������Ϣ��
            double rightEntropy = computeRightEntropy(seg);
            totalRE = totalRE + rightEntropy;
            //  debug_Info.append(seg + "   :������Ϣ��->  " + rightEntropy + "\n");
            double leftEntropy = computeLeftEntropy(seg);
            totalLE = totalLE + leftEntropy;
            // debug_Info.append(seg + "   :������Ϣ��->  " + leftEntropy + "\n"); // double entropy  =  Math.min(rightEntropy,leftEntropy);
            // 2. ���㻥��Ϣ
            double mi = computeMutualInformation(seg);
            totalMI = totalMI + mi;
            Term term = new Term(seg, wcMap.get(seg), mi, leftEntropy, rightEntropy);
            segTermMap.put(seg, term);

            count++;
            if (count % 1000 == 0) {
                System.out.print("=====");
            }

            if (count == 10000) {
                System.out.println();
                count = 0;
                continue;
            }
        }

        System.out.println("ͳ���������ܺ�ʱ: " + (System.currentTimeMillis() - t1) + "ms");
    }

    /**
     * ����зִ�
     */
    public void addSeg(String seg, Integer count) {
        if (StringUtils.isNotBlank(seg)) {
            trieLeft.put(seg, count);
        }
    }

    /**
     * ����������  ������ȷ
     */
    public double computeLeftEntropy(String prefix) {
        Set<Map.Entry<String, Integer>> entrySet = trieLeft.prefixSearch(HanUtils.reverseString(prefix));
        return computeEntropy(entrySet);
    }

    /**
     * ����������
     */
    public double computeRightEntropy(String prefix) {
        Set<Map.Entry<String, Integer>> entrySet = trieRight.prefixSearch(prefix);
        return computeEntropy(entrySet);
    }


    /**
     * ��Ϣ�ؼ���
     */
    private double computeEntropy(Set<Map.Entry<String, Integer>> entrySet) {
        double totalFrequency = 0;
        for (Map.Entry<String, Integer> entry : entrySet) {
            totalFrequency += entry.getValue();
        }
        double le = 0;
        for (Map.Entry<String, Integer> entry : entrySet) {
            double p = entry.getValue() / totalFrequency;
            le += -p * Math.log(p);
        }
        return le;
    }

    /**
     * ���㻥��Ϣ ��ٵ�ǰ�зֵ����п�����ϣ�������Ϣȫ������,Ȼ��ȡ��Сֵ
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

    // �ı�˼·,һ��ʼ����ȫ�����

    private double computeMI(String co_occurrence, String x, String y) {
        double p_xy = Math.max(MIN_PROBABILITY, wcMap.get(co_occurrence) / totalCount);
        int x_count = x.length() == 1 ? singWordCountMap.get(x) : wcMap.get(x);
        double p_x = Math.max(MIN_PROBABILITY, x_count / totalCount);
        int y_count = y.length() == 1 ? singWordCountMap.get(y) : wcMap.get(y);
        double p_y = Math.max(MIN_PROBABILITY, y_count / totalCount);
        return p_xy * (Math.log(p_xy / (p_x * p_y)) / Math.log(2)) * 1e6;
    }

    /**
     * �õ���һ��ֵ
     */
    public double getNormalizedScore(String seg) {
        Term term = segTermMap.get(seg);


        term.score = term.mi / totalMI + term.le / totalLE + term.re / totalRE;   // ��һ��

        debug_Info.append(seg + "   mi->   " + term.mi + " le->" + term.le + " re->" + term.re + " wc->" + term.count + " score->" + term.score + "\n");
        System.out.println(seg + "   mi->   " + term.mi + " le->" + term.le + " re->" + term.re + " wc->" + term.count + " score->" + term.score + "\n");
        //  term.score *= totalTerm;  // ����Ȳ��ӿ������
        return term.score;
    }
}
