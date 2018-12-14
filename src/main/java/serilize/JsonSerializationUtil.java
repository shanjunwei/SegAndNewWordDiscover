package serilize;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import computer.Occurrence;
import config.Config;
import pojo.Term;
import seg.PreProcess;

import java.io.IOException;
import java.util.HashMap;

import static config.Config.*;
import static config.Constants.*;
import static config.Constants.segTermMap;

public class JsonSerializationUtil {
    private static Gson gson = new Gson();

    /* 序列化计算结果到文件中,只要调用一次 */
    public static void serilizableStatisticsToFile() {
        PreProcess preProcess = new PreProcess();
        if (NovelTest) {
            preProcess.initNovel();
        } else {
            preProcess.initData();
        }
        Occurrence occurrence = new Occurrence();
        occurrence.addAllSegAndCompute(wcMap);   // 计算统计量

        // Term term = new Term("####", 0, totalMI, totalLE, totalRE);
        Term term = new Term(MAX_KEY, 0, maxMI, maxLE, maxRE);
        segTermMap.put(MAX_KEY, term);
        try {
            JsonSerializationUtil.serilizableForMap(segTermMap, Config.segTermMapPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    /**
     * 反序列化统计量到内存
     */
    public static void deserilizableStatistics() {
        long t1 = System.currentTimeMillis();
        try {
            segTermMap = JsonSerializationUtil.deserilizableForMapFromFile(Config.segTermMapPath);
//            Config.totalRE = segTermMap.get("####").getRe();
//            Config.totalLE = segTermMap.get("####").getLe();
//            Config.totalMI = segTermMap.get("####").getMi();

            if (NovelTest == true) {
                Config.maxRE = segTermMap.get("####").getRe();
                Config.maxLE = segTermMap.get("####").getLe();
                Config.maxMI = segTermMap.get("####").getMi();
            } else {
                maxLE = segTermMap.get(MAX_KEY).getLe();
                maxRE = segTermMap.get(MAX_KEY).getRe();
                maxMI = segTermMap.get(MAX_KEY).getMi();      // 反序列化统计量
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        long t2 = System.currentTimeMillis();
        System.out.println("反序列化文件到内存耗时:  " + (t2 - t1) + " ms");
    }

    /* 将json文件中的内容读取出来，反序列化为HashMap */
    public static HashMap<String, Term> deserilizableForMapFromFile(String InputfilePathName) throws IOException {
        String jsonArr = readAndWriteJson.readFile(InputfilePathName);
        HashMap<String, Term> map = gson.fromJson(jsonArr,
                new TypeToken<HashMap<String, Term>>() {
                }.getType());
        return map;
    }

    /* 将HashMap序列化为字符串存入json文件中 */
    public static String serilizableForMap(Object objMap, String OutfilePathName)
            throws IOException {
        String listString = gson.toJson(objMap);
        readAndWriteJson.writeFile(OutfilePathName, listString);
        return listString;
    }



    /* *//* 将链表序列化为字符串存入json文件中 *//*  fastjson 解析有很多坑,不大好用
    public static String serilizableForList(Object objList, String OutfilePathName)
            throws IOException {

        String listString = JSON.toJSONString(objList, true);// (maps,CityEntity.class);
        readAndWriteJson.writeFile(OutfilePathName, listString);
        return listString;
    }

    *//* 将json文件中的内容读取出来，反序列化为链表 *//*
    public static <T> List<T> deserilizableForListFromFile(String InputfilePathName, Class<T> clazz)
            throws IOException {

        String listString2 = readAndWriteJson.readFile(InputfilePathName);
        List<T> list2 = JSON.parseArray(listString2, clazz);
        return list2;
    }

    *//* 将HashMap序列化为字符串存入json文件中 *//*
    public static String serilizableForMap(Object objMap, String OutfilePathName)
            throws IOException {

        String listString = JSON.toJSONString(objMap, true);// (maps,CityEntity.class);
        readAndWriteJson.writeFile(OutfilePathName, listString);
        return listString;
    }
   *//*  将json文件中的内容读取出来，反序列化为HashMap *//*
    public static <T> HashMap<String, T> deserilizableForMapFromFile(String InputfilePathName,
                                                                     Class<T> clazz) throws IOException {
        String listString2 = readAndWriteJson.readFile(InputfilePathName);
        HashMap<String, T> map = JSON.parseObject(listString2, new TypeReference<HashMap<String, T>>() {
        });
        return map;
        // return (HashMap<K, T>) map;
    }

    */

 /*   public static <T> HashMap<String, T> deserilizableForMapFromFile(String InputfilePathName, Class<T> clazz) throws IOException {
        String listString2 = readAndWriteJson.readFile(InputfilePathName);
        HashMap<String, T> map = JSON.parseObject(listString2, new TypeReference<HashMap<String, T>>() {
        });
        return map;  // return (HashMap<K, T>) map;
    }*/


    public static void main(String[] args) {

    }
}
