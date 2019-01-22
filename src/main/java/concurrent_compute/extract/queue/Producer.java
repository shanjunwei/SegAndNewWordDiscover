package concurrent_compute.extract.queue;

import pojo.LineMsg;
import util.FileUtils;
import util.HanUtils;

import static config.Config.NOVEL_INPUT_PATH;

/**
 * ��Ϣ������
 */
public class Producer {

    public static void produceNovel() {
        MyBlockingQueue myBlockingQueue = new MyBlockingQueue();
        // ��ȡС˵�ı�
        String novel = FileUtils.readFileToString(NOVEL_INPUT_PATH);
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(novel);  // ȥ���������ַ�   ���û�ж���
        // �ٲ��ͣ�ô�
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // ����û�ж���
            LineMsg lineMsg = new LineMsg(i + 1, textDS);
            myBlockingQueue.produce(lineMsg);
        }
    }
}
