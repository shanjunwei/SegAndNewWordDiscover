package SegmentTest;

import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.Jedis;
import util.HanUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import static config.Config.*;

public class WordCountTest {

    public static void main(String[] args) {
        commonWordCount();
    }

    //  单线程统计词频，并扔redis
    public static void commonWordCount() {
        long t1 = System.currentTimeMillis();
        Jedis jedis = new Jedis(REDIS_HOST);
        jedis.auth(REDIS_AUTH_PASSWORD);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(NOVEL_INPUT_PATH), "utf-8"))) {
            String str = null;
            while ((str = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(str)) {
                    String[] replaceNonChinese = HanUtils.replaceNonChineseCharacterAsBlank(str);
                    for (int i = 0; i < replaceNonChinese.length; i++) {
                        String textDS = replaceNonChinese[i];
                        if (StringUtils.isNotBlank(textDS)) {
                            System.out.println("===="+textDS);
                            HanUtils.FMMAndSaveWCToRedis(textDS, jedis);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("总计耗时:  " + (System.currentTimeMillis() - t1) + " ms");
    }

}
