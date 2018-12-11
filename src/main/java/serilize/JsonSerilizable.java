package serilize;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pojo.Term;

import java.io.IOException;
import java.util.HashMap;

public class JsonSerilizable {
    private static Gson gson = new Gson();

    //  fastjson �����кܶ��,�������
    /* *//* ���������л�Ϊ�ַ�������json�ļ��� *//*
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

    /* ************************************ ����Ϊ Google Gson ���л�������� ****************************************/

    /* ��HashMap���л�Ϊ�ַ�������json�ļ��� */
    public static String serilizableForMap(Object objMap, String OutfilePathName)
            throws IOException {
        String listString = gson.toJson(objMap);
        readAndWriteJson.writeFile(OutfilePathName, listString);
        return listString;
    }

    // ��json�ļ��е����ݶ�ȡ�����������л�ΪHashMap
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
