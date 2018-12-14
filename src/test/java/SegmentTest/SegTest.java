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
     *  测试单个句子
     */
    public static void testSingleSentenceSeg(String[] args) {
        if(args.length <1)  System.exit(0);
        String test_text = args[0];     // 测试文本通过标准输入传入
        if (StringUtils.isBlank(test_text)) System.exit(0);
        //  将信息熵 互信息等统计量加入到过滤决策机制中
        Segment segment = new Segment();
        //NovelTest = true;
        DEBUG_MODE = true;
        List<String> result = segment.segment(test_text);
        System.out.println("\n*************************分词结果集" + result + "*************************\n");
    }

    /**
     *  测试 例子 孙少平 少平
     */
    public static void testRepeatRegx(String[] args) {
        String regEx = "少平";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(args[0]);
        while (m.find()) {
            String find = m.group();
            System.out.println(find + " ["+m.start() + "-"+m.end()+"]");
        }
    }
}
