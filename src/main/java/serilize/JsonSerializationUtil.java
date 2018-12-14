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

    /* ���л����������ļ���,ֻҪ����һ�� */
    public static void serilizableStatisticsToFile() {
        PreProcess preProcess = new PreProcess();
        if (NovelTest) {
            preProcess.initNovel();
        } else {
            preProcess.initData();
        }
        Occurrence occurrence = new Occurrence();
        occurrence.addAllSegAndCompute(wcMap);   // ����ͳ����

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
     * �����л�ͳ�������ڴ�
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
                maxMI = segTermMap.get(MAX_KEY).getMi();      // �����л�ͳ����
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        long t2 = System.currentTimeMillis();
        System.out.println("�����л��ļ����ڴ��ʱ:  " + (t2 - t1) + " ms");
    }

    /* ��json�ļ��е����ݶ�ȡ�����������л�ΪHashMap */
    public static HashMap<String, Term> deserilizableForMapFromFile(String InputfilePathName) throws IOException {
        String jsonArr = readAndWriteJson.readFile(InputfilePathName);
        HashMap<String, Term> map = gson.fromJson(jsonArr,
                new TypeToken<HashMap<String, Term>>() {
                }.getType());
        return map;
    }

    /* ��HashMap���л�Ϊ�ַ�������json�ļ��� */
    public static String serilizableForMap(Object objMap, String OutfilePathName)
            throws IOException {
        String listString = gson.toJson(objMap);
        readAndWriteJson.writeFile(OutfilePathName, listString);
        return listString;
    }



    /* *//* ���������л�Ϊ�ַ�������json�ļ��� *//*  fastjson �����кܶ��,�������
    public static String serilizableForList(Object objList, String OutfilePathName)
            throws IOException {

        String listString = JSON.toJSONString(objList, true);// (maps,CityEntity.class);
        readAndWriteJson.writeFile(OutfilePathName, listString);
        return listString;
    }

    *//* ��json�ļ��е����ݶ�ȡ�����������л�Ϊ���� *//*
    public static <T> List<T> deserilizableForListFromFile(String InputfilePathName, Class<T> clazz)
            throws IOException {

        String listString2 = readAndWriteJson.readFile(InputfilePathName);
        List<T> list2 = JSON.parseArray(listString2, clazz);
        return list2;
    }

    *//* ��HashMap���л�Ϊ�ַ�������json�ļ��� *//*
    public static String serilizableForMap(Object objMap, String OutfilePathName)
            throws IOException {

        String listString = JSON.toJSONString(objMap, true);// (maps,CityEntity.class);
        readAndWriteJson.writeFile(OutfilePathName, listString);
        return listString;
    }
   *//*  ��json�ļ��е����ݶ�ȡ�����������л�ΪHashMap *//*
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
