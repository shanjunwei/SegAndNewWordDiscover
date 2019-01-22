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
 * ȫ�ֱ���
 */
public class Constans {
    //  �ִʺ������
    public static final Map<Integer, String> EXTRACT_WORD_MAP = new ConcurrentHashMap<>();
    // դ��
    public static final CountDownLatch countDownLatch = new CountDownLatch(SEG_THREAD_NUM);  // դ�������Ʋ����߳�ȫ������֮��ŷ������߳�

    public static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static final JedisPool pool = new JedisPool(new JedisPoolConfig(), REDIS_HOST,REDIS_PORT);   // �����ڶ���߳���ʹ��һ��redisʵ��

    public static int  QUEUE_SIZE = 1;
}
