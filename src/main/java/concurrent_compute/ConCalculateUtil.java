package concurrent_compute;

import redis.clients.jedis.Jedis;
import util.HanUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static config.CommonValue.segTotalCount;
import static config.Config.MIN_PROBABILITY;
import static config.Constants.trieLeft;
import static config.Constants.trieRight;

public class ConCalculateUtil {


    /**
     * ���㻥��Ϣ ��ٵ�ǰ�зֵ����п�����ϣ�������Ϣȫ������,Ȼ��ȡ��Сֵ
     */
    public static float computeMutualInformation(String co_occurrence, Jedis jedis) {
        List<List<String>> possibleCombines = HanUtils.getPossibleCombination(co_occurrence);
        float result = 0.0f;
        for (List<String> combine : possibleCombines) {
            String x = combine.get(0);
            String y = combine.get(1);
            result += computeMI(co_occurrence, x, y, jedis);   // �ۼ�
        }
        return result;
    }


    /**
     * ���㻥��Ϣ,��redis�л�ȡ����Դ
     */
    public static float computeMI(String co_occurrence, String x, String y, Jedis jedis) {
        double p_xy = Math.max(MIN_PROBABILITY, Double.valueOf(jedis.get(co_occurrence)) / (double) segTotalCount);
        int x_count = Integer.valueOf(jedis.get(x));
        double p_x = Math.max(MIN_PROBABILITY, (double) x_count / (double) segTotalCount);
        int y_count = Integer.valueOf(jedis.get(y));
        double p_y = Math.max(MIN_PROBABILITY, (double) y_count / (double) segTotalCount);
        return (float) (p_xy * (Math.log(p_xy / (p_x * p_y)) / Math.log(2)) * 1e5);   //return  (Math.log(p_xy / (p_x * p_y)) / Math.log(2)) * 10;
    }


    /**
     * ����������
     */
    public static float computeRightEntropy(String prefix, int prefix_count) {
        Set<Map.Entry<String, Integer>> entrySet = trieRight.prefixSearch(prefix);
        return computeEntropy(entrySet, prefix, prefix_count);
    }

    /**
     * ����������
     */
    public static float computeLeftEntropy(String prefix, int prefix_count) {
        Set<Map.Entry<String, Integer>> entrySet = trieLeft.prefixSearch(HanUtils.reverseString(prefix));
        return computeEntropy(entrySet, prefix, prefix_count);
    }

    /**
     * ��Ϣ�ؼ���
     */
    public static float computeEntropy(Set<Map.Entry<String, Integer>> entrySet, String prefix, int prefix_count) {
        //  float totalFrequency = 0;
      /*  for (Map.Entry<String, Integer> entry : entrySet) {
            if (entry.getKey().length() != prefix.length() + 1) {
                continue;
            }
            totalFrequency += entry.getValue();
        }*/
        // totalFrequency = prefix_count;
        float le = 0;
        for (Map.Entry<String, Integer> entry : entrySet) {
            if (entry.getKey().length() != prefix.length() + 1) {
                continue;
            }
            //System.out.println("====="+(float)entry.getValue() / (float) prefix_count);
            float p = (float)entry.getValue() / (float) prefix_count;
            le += -p * (Math.log(p) / Math.log(2));
        }
       // System.out.println(prefix + "��Ϣ��:" + le);
        return le;
    }



}
