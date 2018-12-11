package pojo;

import java.io.Serializable;

/**
 *   �洢ÿ���з� �����ǵ�һЩͳ����Ϣ
 */
public class Term implements Serializable {
    public   String  seg;   // �з�Ƭ��
    public   int  count;   // Ƶ��
    /**
     * ����Ϣֵ
     */
    public double mi;
    /**
     * ����Ϣ��
     */
    public double le;
    /**
     * ����Ϣ��
     */
    public double re;
    /**
     * ���� ���յĹ�һ������
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
