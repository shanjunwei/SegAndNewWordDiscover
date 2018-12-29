package pojo;

public class SegMsg {
    public String seg;
    public int count;

    public SegMsg() {
    }

    public SegMsg(String seg, int count) {
        this.seg = seg;
        this.count = count;
    }

    @Override
    public String toString() {
        return "SegMsg{" +
                "seg='" + seg + '\'' +
                ", count=" + count +
                '}';
    }
}
