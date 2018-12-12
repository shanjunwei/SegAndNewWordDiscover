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
            //System.out.println("====������Ϣ��-->"+seg);
            // 1. ������Ϣ��
            double rightEntropy = computeRightEntropy(seg);
            RElist.add(rightEntropy);
            totalRE = totalRE + rightEntropy;
            //  debug_Info.append(seg + "   :������Ϣ��->  " + rightEntropy + "\n");
            double leftEntropy = computeLeftEntropy(seg);
            LElist.add(leftEntropy);
            totalLE = totalLE + leftEntropy;
            // debug_Info.append(seg + "   :������Ϣ��->  " + leftEntropy + "\n"); // double entropy  =  Math.min(rightEntropy,leftEntropy);
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


    public void serilizableStatisticsToFile() {
        //  ������ͳ�����ֱ�����ļ��б��ڷ���
      /*  FileUtils.writeFileToPath(Config.segTermMapPath, RElist);
        FileUtils.writeFileToPath(Config.segTermMapPath, LElist);
        FileUtils.writeFileToPath("D:\\HanLP\\����Ч��\\MI_list", MI_list);*/
        // ��������ͳ����Ϣ
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
     * �����л�ͳ�������ڴ�
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
        System.out.println("�����л��ļ����ڴ��ʱ:  "+(t2-t1) +" ms");
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

    // �ı�˼·,һ��ʼ����ȫ�����

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
     * �õ���һ��ֵ
     */
    public double getNormalizedScore(String seg) {
        //Term term = JSON.parseObject(String.valueOf(segTermMap.get(seg)),new TypeReference<Term>() {});
        Term term = segTermMap.get(seg);
        if(term == null){
            System.out.println("�������в�����->  "+seg);
            return 0;
        }
        term.score = term.mi / totalMI + term.le / totalLE + term.re / totalRE;   // ��һ��
        //������һ������
        term.score  =  term.mi / totalMI + Math.min(term.le / totalLE,term.re / totalRE);
        if(DEBUG_MODE) System.out.println(seg + "   mi->   " + term.mi + " le->" + term.le + " re->" + term.re + " wc->" + term.count + " score->" + term.score + "\n");
        //  term.score *= totalTerm;  // ����Ȳ��ӿ������
        return term.score;
    }
}
