package concurrent_compute.extract.queue;

import pojo.LineMsg;

import java.util.concurrent.LinkedTransferQueue;

/**
 * 消息生产消费的模型
 */
public class MyBlockingQueue {
    public static LinkedTransferQueue<LineMsg> fairQueue = new LinkedTransferQueue();

    // 消息生产
    public  static void produce(LineMsg msg) {
        fairQueue.add(msg);
    }

    // 消息消费
    public static LineMsg consume() {
        return fairQueue.poll();
    }
}



