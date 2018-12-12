package computer;

import pojo.Term;
import util.HanUtils;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static config.Config.*;
import static config.Constants.*;

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


    public Occurrence() {
    }

    /**
     * ��������зִ�  ���㻥��Ϣ ��Ϣ�ص�ͳ���� ,�����ṩ��һ����ڼ���
     */
    public void addAllSegAndCompute(Map<String, Integer> wcMap) {
        System.out.println("����ͳ������-->");
        int count = 0;
        long t1 = System.currentTimeMillis();

        totalTerm = wcMap.size();
        for (String seg : wcMap.keySet()) {
            trieRight.put(seg, wcMap.get(seg));     // ��ǰ׺�ֵ���
            trieLeft.put(HanUtils.reverseString(seg), wcMap.get(seg));  // ��ǰ׺�ֵ���
            totalCount = totalCount + wcMap.get(seg);    // �����ܴ�Ƶ
        }
        for (String seg : wcMap.keySet()) {
            // 1. ������Ϣ��
            double rightEntropy = computeRightEntropy(seg);
            RElist.add(rightEntropy);
            totalRE = totalRE + rightEntropy;
            double leftEntropy = computeLeftEntropy(seg);
            LElist.add(leftEntropy);
            totalLE = totalLE + leftEntropy;
            // 2. ���㻥��Ϣ
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
        System.out.println("ͳ���������ܺ�ʱ: " + (System.currentTimeMillis() - t1) + "ms");
    }
    /**
     * ����������  ������ȷ
     */
    public double computeLeftEntropy(String prefix) {
        Set<Map.Entry<String, Integer>> entrySet = trieLeft.prefixSearch(HanUtils.reverseString(prefix));
        return computeEntropy(entrySet, prefix);
    }

    /**
     * ����������
     */
    public double computeRightEntropy(String prefix) {
        Set<Map.Entry<String, Integer>> entrySet = trieRight.prefixSearch(prefix);
        return computeEntropy(entrySet, prefix);
    }


    /**
     * ��Ϣ�ؼ���
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
    /**
     *  ���㻥��Ϣ
     */
    private double computeMI(String co_occurrence, String x, String y) {
        double p_xy = Math.max(MIN_PROBABILITY, wcMap.get(co_occurrence) / totalCount);
        int x_count = x.length() == 1 ? singWordCountMap.get(x) : wcMap.get(x);
        double p_x = Math.max(MIN_PROBABILITY, x_count / totalCount);
        int y_count = y.length() == 1 ? singWordCountMap.get(y) : wcMap.get(y);
        double p_y = Math.max(MIN_PROBABILITY, y_count / totalCount);
        return p_xy * (Math.log(p_xy / (p_x * p_y)) / Math.log(2)) * 1e5;   //return  (Math.log(p_xy / (p_x * p_y)) / Math.log(2)) * 10;
    }
    /**
     * �õ���һ��ֵ
     */
    public double getNormalizedScore(String seg) {
        Term term = segTermMap.get(seg);
        if(term == null){
            System.out.println("�������в�����->  "+seg);
            return 0;
        }
        term.score = term.mi / totalMI + term.le / totalLE + term.re / totalRE;   // ��һ��
        //������һ������
        term.score  =  term.mi / totalMI + Math.min(term.le / totalLE,term.re / totalRE);
        if(DEBUG_MODE) System.out.println(seg + "   mi->   " + term.mi + " le->" + term.le + " re->" + term.re + " wc->" + term.count + " score->" + term.score + "\n");
        return term.score;
    }
}
