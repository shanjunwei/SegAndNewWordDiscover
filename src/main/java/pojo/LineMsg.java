package pojo;

public class LineMsg {
    public int  lineCount;  // 每行行号
    public String  text;  // 每行内容

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
