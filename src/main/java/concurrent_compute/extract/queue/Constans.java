package concurrent_compute.extract.queue;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static config.Config.*;

/**
 * 全局变量
 */
public class Constans {
    //  分词后存这里
    public static final Map<Integer, String> EXTRACT_WORD_MAP = new ConcurrentHashMap<>();
    // 栅栏
    public static final CountDownLatch countDownLatch = new CountDownLatch(SEG_THREAD_NUM);  // 栅栏，控制并发线程全部结束之后才返回主线程

    public static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static final JedisPool pool = new JedisPool(new JedisPoolConfig(), REDIS_HOST,REDIS_PORT);   // 不能在多个线程中使用一个redis实例

    public static int  QUEUE_SIZE = 1;
}
