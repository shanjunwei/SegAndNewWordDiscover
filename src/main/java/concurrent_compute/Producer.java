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
 * ��Ϣ������
 */
public class Producer {
    public static void produce(String inputPath) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), "utf-8"))) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void produceNovel(String inputPath) {
        // ��ȡС˵�ı�
        String novel = FileUtils.readFileToString(inputPath);
        String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(novel);  // ȥ���������ַ�   ���û�ж���
        // �ٲ��ͣ�ô�
        for (int i = 0; i < replaceNonChinese.length; i++) {
            String textDS = replaceNonChinese[i];   // ����û�ж���
            if (StringUtils.isNotBlank(textDS) && textDS.length() != 1) {
             /*   List<String> list = HanUtils.getFMMList(textDS, true);    // ���ŶȱȽϵ����������ֵ
                transferQueue.addAll(list);*/
                transferQueue.add(textDS);
            }
        }
        System.out.println("�������������е�����" + transferQueue.size() );
        Constants.QUEUE_SIZE =  transferQueue.size();   // ��ֵ
    }


    public static void main(String[] args) {
        Producer.produceNovel("D:\\HanLP\\novel\\�����˲�.txt");
    }

}
