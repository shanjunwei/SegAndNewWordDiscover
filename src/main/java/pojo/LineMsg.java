package pojo;

public class LineMsg {
    public int  lineCount;  // ÿ���к�
    public String  text;  // ÿ������

    public LineMsg() {
    }

    public LineMsg(int lineCount, String text) {
        this.lineCount = lineCount;
        this.text = text;
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "LineMsg{" +
                "lineCount=" + lineCount +
                ", text=" + text +
                '}';
    }
}
