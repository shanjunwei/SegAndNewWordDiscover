package serilize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonSerilizable {

    /* 将链表序列化为字符串存入json文件中 */
    public static String serilizableForList(Object objList, String OutfilePathName)
            throws IOException {

        String listString = JSON.toJSONString(objList, true);// (maps,CityEntity.class);
        readAndWriteJson.writeFile(OutfilePathName, listString);
        return listString;
    }

    /* 将json文件中的内容读取出来，反序列化为链表 */
    public static <T> List<T> deserilizableForListFromFile(String InputfilePathName, Class<T> clazz)
            throws IOException {

        String listString2 = readAndWriteJson.readFile(InputfilePathName);
        List<T> list2 = JSON.parseArray(listString2, clazz);
        return list2;
    }

    /* 将HashMap序列化为字符串存入json文件中 */
    public static String serilizableForMap(Object objMap, String OutfilePathName)
            throws IOException {

        String listString = JSON.toJSONString(objMap, true);// (maps,CityEntity.class);
        readAndWriteJson.writeFile(OutfilePathName, listString);
        return listString;
    }

    /* 将json文件中的内容读取出来，反序列化为HashMap */
    public static <T, K> HashMap<K, T> deserilizableForMapFromFile(String InputfilePathName,
                                                                   Class<T> clazz) throws IOException {
        String listString2 = readAndWriteJson.readFile(InputfilePathName);
        Map<K, T> map = JSON.parseObject(listString2, new TypeReference<Map<K, T>>() {
        });
        return (HashMap<K, T>) map;
    }

}
