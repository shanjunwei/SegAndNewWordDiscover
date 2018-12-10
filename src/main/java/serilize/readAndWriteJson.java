package serilize;

import java.io.*;

public class readAndWriteJson {

    /**
     * ���ַ���д���ļ�
     */
    public static void writeFile(String filePath, String input) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        PrintWriter out = new PrintWriter(fw);
        out.write(input);
        out.println();
        fw.close();
        out.close();
    }
    /**
     * ��ȡ�ı��ļ�����
     */
    public static String readFile(String filePath) throws IOException {
        StringBuffer sb = new StringBuffer();
        readToBuffer(sb, filePath);
        return sb.toString();
    }

    /**
     * ���ı��ļ��е����ݶ��뵽buffer��
     */
    public static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        String line; // ��������ÿ�ж�ȡ������
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // ��ȡ��һ��
        while (line != null) { // ��� line Ϊ��˵��������
            buffer.append(line); // ��������������ӵ� buffer ��
            buffer.append("\n"); // ��ӻ��з�
            line = reader.readLine(); // ��ȡ��һ��
        }
        reader.close();
        is.close();
    }
}
