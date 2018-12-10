package serilize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonSerilizable {

    /* ���������л�Ϊ�ַ�������json�ļ��� */
    public static String serilizableForList(Object objList, String OutfilePathName)
            throws IOException {

        String listString = JSON.toJSONString(objList, true);// (maps,CityEntity.class);
        readAndWriteJson.writeFile(OutfilePathName, listString);
        return listString;
    }

    /* ��json�ļ��е����ݶ�ȡ�����������л�Ϊ���� */
    public static <T> List<T> deserilizableForListFromFile(String InputfilePathName, Class<T> clazz)
            throws IOException {

        String listString2 = readAndWriteJson.readFile(InputfilePathName);
        List<T> list2 = JSON.parseArray(listString2, clazz);
        return list2;
    }

    /* ��HashMap���л�Ϊ�ַ�������json�ļ��� */
    public static String serilizableForMap(Object objMap, String OutfilePathName)
            throws IOException {

        String listString = JSON.toJSONString(objMap, true);// (maps,CityEntity.class);
        readAndWriteJson.writeFile(OutfilePathName, listString);
        return listString;
    }

    /* ��json�ļ��е����ݶ�ȡ�����������л�ΪHashMap */
    public static <T, K> HashMap<K, T> deserilizableForMapFromFile(String InputfilePathName,
                                                                   Class<T> clazz) throws IOException {
        String listString2 = readAndWriteJson.readFile(InputfilePathName);
        Map<K, T> map = JSON.parseObject(listString2, new TypeReference<Map<K, T>>() {
        });
        return (HashMap<K, T>) map;
    }

}
