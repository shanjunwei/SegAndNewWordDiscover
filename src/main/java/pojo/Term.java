package pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static config.Constants.*;

/**
 * 存储每个切分 和他们的一些统计信息
 */
public class Term implements Serializable {
    public String seg;   // 切分片段
    public int count;   // 频次

    public int leftBound;  // 左边界  在原来短句中的起始索引位置
    public int rightBound;  // 右边界
    /**
     * 互信息值
     */
    public float mi;
    /**
     * 左信息熵
     */
    public float le;
    /**
     * 右信息熵
     */
    public float re;
    /**
     * 分数 最终的归一化分数
     */
    public float score;

    public Term() {
    }

    public Term(String seg, int count) {
        this.seg = seg;
        this.count = count;
    }

    public Term(String seg, int count, float mi, float le, float re) {
        this.seg = seg;
        this.count = count;
        this.mi = mi;
        this.le = le;
        this.re = re;
    }

    public Term(String seg, int count, float mi, float le, float re, float score) {
        this.seg = seg;
        this.count = count;
        this.mi = mi;
        this.le = le;
        this.re = re;
        this.score = score;
    }

    public Term(String seg, int count, int leftBound, int rightBound, float mi, float le, float re, float score) {
        this.seg = seg;
        this.count = count;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.mi = mi;
        this.le = le;
        this.re = re;
        this.score = score;
    }

    public Term(String seg, int leftBound, int rightBound) {
        this.seg = seg;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }

    public String getSeg() {
        return seg;
    }

    public void setSeg(String seg) {
        this.seg = seg;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getMi() {
        return mi;
    }

    public void setMi(float mi) {
        this.mi = mi;
    }

    public float getLe() {
        return le;
    }

    public void setLe(float le) {
        this.le = le;
    }

    public float getRe() {
        return re;
    }

    public void setRe(float re) {
        this.re = re;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getLeftBound() {
        return leftBound;
    }

    public void setLeftBound(int leftBound) {
        this.leftBound = leftBound;
    }

    public int getRightBound() {
        return rightBound;
    }

    public void setRightBound(int rightBound) {
        this.rightBound = rightBound;
    }

    /**
     * Term  转 Map对象
     */
    public Map<String, String> convertToMap() {
        Map<String, String> termMap = new HashMap();
        termMap.put(SEG, this.seg);
        termMap.put(COUNT, String.valueOf(this.count));
        termMap.put(MI, String.valueOf(this.mi));
        termMap.put(LE, String.valueOf(this.le));
        termMap.put(RE, String.valueOf(this.re));
        termMap.put(SCORE, String.valueOf(this.score));
        return termMap;
    }

    /**
     * Map对象  转 Term
     */
    public static Term getTermObjectFromMap(Map<String, String> termMap) {
        Term term = new Term();
        term.setSeg(termMap.get(SEG));
        term.setCount(Integer.valueOf(termMap.get(COUNT)));
        term.setMi(Float.valueOf(termMap.get(MI)));
        term.setLe(Float.valueOf(termMap.get(LE)));
        term.setRe(Float.valueOf(termMap.get(RE)));
        term.setScore(Float.valueOf(termMap.get(SCORE)));
        return term;
    }


    @Override
    public String toString() {
        return seg;
    }

    public String toTotalString() {
        return "Term{" +
                "seg='" + seg + '\'' +
                ", count=" + count +
                ", leftBound=" + leftBound +
                ", rightBound=" + rightBound +
                ", mi=" + mi +
                ", le=" + le +
                ", re=" + re +
                ", score=" + score +
                '}';
    }
}
