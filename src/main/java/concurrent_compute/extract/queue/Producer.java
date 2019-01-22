package concurrent_compute.extract.queue;

import pojo.LineMsg;
import util.FileUtils;
import util.HanUtils;

import static config.Config.NOVEL_INPUT_PATH;

/**
 * 消息生产者
 */
public class Producer {

    public static void produceNovel() {
        MyBlockingQueue myBlockingQueue = new MyBlockingQueue();
        // 读取小说文本
        String novel = FileUtils.readFileToString(NOVEL_INPUT_PATH);
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(novel);  // 去掉非中文字符   里边没有逗号
        // 再拆分停用词
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // 这里没有逗号
            LineMsg lineMsg = new LineMsg(i + 1, textDS);
            myBlockingQueue.produce(lineMsg);
        }
    }
}
