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
    public double mi;
    /**
     * 左信息熵
     */
    public double le;
    /**
     * 右信息熵
     */
    public double re;
    /**
     * 分数 最终的归一化分数
     */
    public double score;

    public Term() {
    }

    public Term(String seg, int count, double mi, double le, double re) {
        this.seg = seg;
        this.count = count;
        this.mi = mi;
        this.le = le;
        this.re = re;
    }

    public Term(String seg, int count, double mi, double le, double re, double score) {
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

    public double getMi() {
        return mi;
    }

    public void setMi(double mi) {
        this.mi = mi;
    }

    public double getLe() {
        return le;
    }

    public void setLe(double le) {
        this.le = le;
    }

    public double getRe() {
        return re;
    }

    public void setRe(double re) {
        this.re = re;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
