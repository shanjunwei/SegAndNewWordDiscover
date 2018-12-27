package concurrent_compute;

import redis.clients.jedis.Jedis;

import java.util.concurrent.LinkedTransferQueue;

import static config.Config.REDIS_AUTH_PASSWORD;
import static config.Config.REDIS_HOST;
import static config.Constants.REDIS_WC_KEY;

public class MIERConCompute implements ConCompute {
    public static LinkedTransferQueue<String> transferQueue = new LinkedTransferQueue();    // 阻塞队列,用来实现并发加速框架


    @Override
    public void consumer() {

    }

    @Override
    public void produce() {
        Jedis jedis = new Jedis(REDIS_HOST);
        jedis.auth(REDIS_AUTH_PASSWORD);
        jedis.zrevrangeWithScores(REDIS_WC_KEY, 0, 100000000).forEach(it -> {   //  两个参数是下标
            System.out.println(it.getElement() + " -> " + it.getScore());
            transferQueue.add(it.getElement());
        });
    }
}
