package pojo;

import java.io.Serializable;

/**
 *   存储每个切分 和他们的一些统计信息
 */
public class Term implements Serializable {
    public   String  seg;   // 切分片段
    public   int  count;   // 频次
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
}
