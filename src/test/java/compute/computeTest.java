package compute;

import computer.Occurrence;
import seg.PreProcess;

/**
 *  单侧计算值
 */
public class computeTest {


    public static void main(String[] args) {
        // 数据预处理
        PreProcess preProcess = new PreProcess();
        preProcess.initData();

        // 将所有切分片段 加载进 字典树，方便进行信息熵的计算
        Occurrence occurrence = new Occurrence();


    }

}
