package SegmentTest;

import config.Constants;
import org.apache.commons.lang.StringUtils;
import seg.Segment;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static config.Constants.DEBUG_MODE;

public class SegTest {


    public static void main(String[] args) {
        Constants.NovelTest = true;
        testSingleSentenceSeg(args);
        //testRepeatRegx(args);
    }


    /**
     *  ���Ե�������
     */
    public static void testSingleSentenceSeg(String[] args) {
        if(args.length <1)  System.exit(0);
        String test_text = args[0];     // �����ı�ͨ����׼���봫��
        if (StringUtils.isBlank(test_text)) System.exit(0);
        //  ����Ϣ�� ����Ϣ��ͳ�������뵽���˾��߻�����
        Segment segment = new Segment();
        //NovelTest = true;
        DEBUG_MODE = true;
        List<String> result = segment.segment(test_text);
        System.out.println("\n*************************�ִʽ����" + result + "*************************\n");
    }

    /**
     *  ���� ���� ����ƽ ��ƽ
     */
    public static void testRepeatRegx(String[] args) {
        String regEx = "��ƽ";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(args[0]);
        while (m.find()) {
            String find = m.group();
            System.out.println(find + " ["+m.start() + "-"+m.end()+"]");
        }
    }
}
