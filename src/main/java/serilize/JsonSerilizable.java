package serilize;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pojo.Term;

import java.io.IOException;
import java.util.HashMap;

public class JsonSerilizable {
    private static Gson gson = new Gson();

    //  fastjson 解析有很多坑,不大好用
    /* *//* 将链表序列化为字符串存入json文件中 *//*
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

    /* ************************************ 以下为 Google Gson 序列化解决方案 ****************************************/

    /* 将HashMap序列化为字符串存入json文件中 */
    public static String serilizableForMap(Object objMap, String OutfilePathName)
            throws IOException {
        String listString = gson.toJson(objMap);
        readAndWriteJson.writeFile(OutfilePathName, listString);
        return listString;
    }

    // 将json文件中的内容读取出来，反序列化为HashMap
    public static HashMap<String, Term> deserilizableForMapFromFile(String InputfilePathName) throws IOException {
        String jsonArr = readAndWriteJson.readFile(InputfilePathName);
        HashMap<String, Term> map = gson.fromJson(jsonArr,
                new TypeToken<HashMap<String, Term>>() {
                }.getType());
        return map;
    }
    public static void main(String[] args) {

    }
}
