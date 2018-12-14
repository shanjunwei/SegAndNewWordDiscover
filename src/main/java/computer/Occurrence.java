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
    float totalTerm;

    /**
     * �зֶ� ȥ�غ�Ƶ���ۼƺ�
     */
    static float totalCount;
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
            float rightEntropy = computeRightEntropy(seg);
            RElist.add(rightEntropy);
            //totalRE = totalRE + rightEntropy;
            maxRE = Math.max(maxRE, rightEntropy);  // ���������Ϣ��
            float leftEntropy = computeLeftEntropy(seg);
            LElist.add(leftEntropy);
            // totalLE = totalLE + leftEntropy;
            maxLE = Math.max(maxLE, rightEntropy);  // ���������Ϣ��
            // 2. ���㻥��Ϣ
            float mi = computeMutualInformation(seg);
            MI_list.add(mi);
            //totalMI = totalMI + mi;
            maxMI = Math.max(maxMI, mi);   // ���������Ϣ
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
    public float computeLeftEntropy(String prefix) {
        Set<Map.Entry<String, Integer>> entrySet = trieLeft.prefixSearch(HanUtils.reverseString(prefix));
        return computeEntropy(entrySet, prefix);
    }

    /**
     * ����������
     */
    public float computeRightEntropy(String prefix) {
        Set<Map.Entry<String, Integer>> entrySet = trieRight.prefixSearch(prefix);
        return computeEntropy(entrySet, prefix);
    }


    /**
     * ��Ϣ�ؼ���
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
     * ���㻥��Ϣ ��ٵ�ǰ�зֵ����п�����ϣ�������Ϣȫ������,Ȼ��ȡ��Сֵ
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
     * ���㻥��Ϣ
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
     * �õ���һ��ֵ
     */
    public float getNormalizedScore(Term term) {
        if (term == null) {
            return 0f;
        }
        // Term term = segTermMap.get(seg);
        //term.score = term.mi / totalMI + term.le / totalLE + term.re / totalRE;   // ��һ��
        //term.score = term.mi / totalMI + Math.min(term.le / totalLE, term.re / totalRE);   // 01������һ������ -> ȡ��������Сֵ
        // ��log �������й�һ��,�ο� http://www.cnblogs.com/pejsidney/p/8031250.html
        float normalizedMi = (float) (Math.log(term.mi) / Math.log(maxMI))*2;  // ��һ����Ϣ��
        float normalizedEntropy = (float) (Math.log(Math.min(term.le, term.re)) / Math.log(term.le < term.re ? maxLE : maxRE));  // ��һ��������
        term.score = normalizedMi + normalizedEntropy + getEntropyRate(term.le, term.re);          // ���������ؽ��̶ܳȵĶ���
        if (DEBUG_MODE)
            System.out.println(term.seg + "   mi->   " + term.mi + "->" + normalizedMi + " le->" + term.le + " re->" + term.re + "->" + normalizedEntropy + " ER->" + getEntropyRate(term.le, term.re) + " score->" + term.score + "\n");
        return term.score;
    }

    /**
     * ��Ϣ�ع���
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
     * �����ر�ֵ
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
     * ����Ϣ����
     */
    public static boolean MutualInformationFilter(float mi) {
        if (mi < MI_THRESHOLD_VALUE) return true;
        return false;
    }

    /**
     * ��׼��ƫ��λ�ò�ֵ,Ŀ����Ϊ���ò����ֵ�ĳ��ȴ�С�̶�
     * TODO: ���������Ӳ�����,�����100����
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
