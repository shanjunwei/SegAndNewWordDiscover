package concurrent_compute;

import config.Constants;
import org.apache.commons.lang.StringUtils;
import util.FileUtils;
import util.HanUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import static config.Constants.transferQueue;

/**
 * 消息生产者
 */
public class Producer {
    public static void produce(String inputPath) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), "utf-8"))) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void produceNovel(String inputPath) {
        // 读取小说文本
        String novel = FileUtils.readFileToString(inputPath);
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(novel);  // 去掉非中文字符   里边没有逗号
        // 再拆分停用词
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // 这里没有逗号
            if (StringUtils.isNotBlank(textDS) && textDS.length() != 1) {
             /*   List<String> list = HanUtils.getFMMList(textDS, true);    // 置信度比较的是这里面的值
                transferQueue.addAll(list);*/
                transferQueue.add(textDS);
            }
        }
        System.out.println("生产后阻塞队列的数量" + transferQueue.size() );
        Constants.QUEUE_SIZE =  transferQueue.size();   // 赋值
    }


    public static void main(String[] args) {
        Producer.produceNovel("D:\\HanLP\\novel\\天龙八部.txt");
    }

}
