package pojo;

import java.io.Serializable;

/**
 * �洢ÿ���з� �����ǵ�һЩͳ����Ϣ
 */
public class Term implements Serializable {
    public String seg;   // �з�Ƭ��
    public int count;   // Ƶ��

    public int leftBound;  // ��߽�  ��ԭ���̾��е���ʼ����λ��
    public int rightBound;  // �ұ߽�
    /**
     * ����Ϣֵ
     */
    public float mi;
    /**
     * ����Ϣ��
     */
    public float le;
    /**
     * ����Ϣ��
     */
    public float re;
    /**
     * ���� ���յĹ�һ������
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
