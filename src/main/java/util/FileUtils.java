package util;


import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class FileUtils {
    public static void writeFileToPath(String outPutPath, LinkedHashSet<String> stringSet) {
        try {
            FileOutputStream writer = new FileOutputStream(outPutPath);
            OutputStreamWriter bw = new OutputStreamWriter(writer, "UTF-8");          // 以utf-8写结果
            stringSet.forEach(it -> {
                if (StringUtils.isNotBlank(it)) {
                    String result = it + " ";
                    try {
                        //   System.out.println(result);
                        bw.write(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            bw.close();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static void writeFileToPath(String outPutPath, List<Double> stringSet) {
        try {
            FileOutputStream writer = new FileOutputStream(outPutPath);
            OutputStreamWriter bw = new OutputStreamWriter(writer, "UTF-8"); // 以utf-8写结果
            stringSet.forEach(it -> {
                String result = it + "\n";
                try {
                    bw.write(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            bw.close();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static void writeFileToPath(String outPutPath, List<String> list, Map<String, Integer> wcMap) {
        try {
            FileOutputStream writer = new FileOutputStream(outPutPath);

            OutputStreamWriter bw = new OutputStreamWriter(writer, "UTF-8");          // 以utf-8写结果
            list.forEach(it -> {
                if (StringUtils.isNotBlank(it) && wcMap.get(it) >= 4) {
                    try {
                        bw.write(it + " -> " + wcMap.get(it) + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            bw.close();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    // 按行读取进集合
    public static HashSet readFileByLineToHashSet(String inputFilePath) {
        HashSet set = new HashSet();
        try {
            // 以utf-8读取文件
            FileInputStream fis = new FileInputStream(inputFilePath);
            InputStreamReader reader = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(reader);
            String str = null;
            while ((str = br.readLine()) != null) {
                set.add(str);
            }
            br.close();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return set;
    }
    // 按行读取进集合
    public static void writeStringToFile(String outPutPath, String text) {
        try {
            FileOutputStream writer = new FileOutputStream(outPutPath);
            OutputStreamWriter bw = new OutputStreamWriter(writer, "UTF-8");          // 以utf-8写结果
            bw.write(text);
            bw.close();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
