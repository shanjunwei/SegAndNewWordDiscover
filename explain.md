##  ����
### 1.JDK 1.8

��ϸ���̲ο���
##### [JDK��װ��������ϸͼ�Ľ̳�](https://www.cnblogs.com/tiankong101/p/4226559.html)
https://www.cnblogs.com/tiankong101/p/4226559.html

ע�⣺����ʹ��Jdk 1.8 ��д��ʹ�õ���Java8��һЩ������ Lambda ���ʽ,��˱��밲װJDK 1.8���ϰ汾��


### 2. Redis

Redis��һ����Դ��ʹ��ANSI C���Ա�д������BSDЭ�顢֧�����硢�ɻ����ڴ���ɳ־û�����־�͡�Key-Value���ݿ⣬���ṩ�������Ե�API��
��ͨ������Ϊ���ݽṹ����������Ϊֵ��value�������� �ַ���(String), ��ϣ(Map), �б�(list), ����(sets) �� ���򼯺�(sorted sets)�����͡�

����ʹ��redis ��ԭ���ǽ��ڴ��ѹ��ת�Ƶ��ⲿ�洢ͬʱ���� redis�ĸ�Ч���ݽṹ���в��Ҳ�����

redis�İ�װ����μ�:http://www.runoob.com/redis/redis-install.html

��redis ��װ�ú����� redis ��������redis����ķ���������������˵��


segment.properties
�����Լ��޸Ĳ��ֲ����������е�Ч�����������������������뱣��Ĭ��ֵ,������ֻ��Ҫ�Լ��޸���Ҫ��ʵ��ı�·�� NOVEL_INPUT_PATH �ͽ�����·�� EXTRACT_OUTPUT����
```
# ��������·��,Ĭ��Ϊ"data\\test-text.txt"
CORPUS_INPUT_PATH=data/test-text.txt
NOVEL_INPUT_PATH=data/GameOfThrones.txt
#  ��ʽ�����
EXTRACT_OUTPUT=data/result.txt
###############################################################################
# ����Ϣ������ֵ   Ĭ��1.0f
MI_THRESHOLD_VALUE=0.9f
# ������Ϣ�ر�����ֵ  Ĭ��0.4f
ENTROPY_THETA=0.4f
# ��ѡ����ȡ��󳤶�  ʵ��Ϊ����ֵ��һ ��6,ʵ�ʵ���󳤶�Ϊ5,Ĭ��5
MAX_WORD_LEN=6
# ��������Ϣ�� ��С��ֵ Ĭ�� 0.01f
MIN_LEFT_ENTROPY=0.01f
# ��������Ϣ�� ��С��ֵ Ĭ�� 0.01f
MIN_RIGHT_ENTROPY=0.01f
################################  redis���  ################################
# redis �����ַ
REDIS_HOST=localhost
# redis �˿ڵ�ַ
REDIS_PORT= 8860
# redis ������֤ Ĭ��Ϊ root
REDIS_AUTH_PASSWORD=root
###############################################################################
# �Ƿ������ԣ�true�����������Ϣ,Ĭ��Ϊfalse
DEBUG_MODE=false
################################  ���̲߳������  ################################
# ������Ƶͳ���߳���
WC_THREAD_NUM=20
COMPUTE_THREAD_NUM=20
# ��������߳���
SEG_THREAD_NUM=3
```

![���run.bat�������г���](https://upload-images.jianshu.io/upload_images/4941834-82bc55876fd9145f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

˵���£����λ���������������ѡ�����в���ͳ����������ʹ���ͳ�����������Ĭ�����в���ͳ����������������Ҫ�滻��ͨ���༭����run.bat������jar���������滻��co_compute.jar���ɡ�ͳ�Ƽ���ʱ��Ӧ��֤�����ڴ���10G���ϡ�


����������Լ���������»�����װ���Ժ���----->
### 3.maven

����������ʹ��Maven����Ҫ��ǰ��װ��Maven
��ϸ���̲ο���
#####Maven��װ������
https://www.jianshu.com/p/68bfcf9daeb7

 ����Դ���ֱ����IDEA ��,�������jar����

### 4. IDEA

IDEA ȫ��IntelliJ IDEA��������java���Կ����ļ��ɻ�����Ҳ�������������ԣ���IntelliJ��ҵ�类����Ϊ��õ�java��������֮һ�����������ܴ������֡������Զ���ʾ���ع���J2EE֧�֡�Ant��JUnit��CVS���ϡ�������顢 ���µ�GUI��Ƶȷ���Ĺ��ܿ���˵�ǳ����ġ�
����ʹ��Idea��ԭ������maven �������ã���ȻҲ����ʹ�� eclipse��������Ҫ�Լ�����maven�����

![����Idea������](https://upload-images.jianshu.io/upload_images/4941834-4449d73f741d49a4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

IDEA ����ҵ��������棬��ҵ��������Ҫ�շѣ������漴��Դ��Ѱ汾��������ȡ�Ĵ�����Java appliacation ������ Java web �������������͹����ˡ�

##  ʹ��

### 1.��idea�򿪳�����Ŀ
![](https://upload-images.jianshu.io/upload_images/4941834-f5b79664d3d226eb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 2. �����Լ�����Ҫ������صĲ��������㷨��׼ȷ�ʺ�Ч��
![image.png](https://upload-images.jianshu.io/upload_images/4941834-213f92509b6ff7b4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

��װ��redis�󣬽�redis����ķ����ַ��������д�������ļ���


### 3. �����Լ�����Ҫ�����Ӧ��Jar��

��ʳ����Ϊ ��Ƶͳ�ơ� ͳ�������㡢��ʴ����ô�������衣
��ΪһЩ���ƻ������أ��ҷ�Ϊ�� ��Ƶͳ����ͳ��������  �� �����ô�������衣�������������һ��ִ�п��Էֿ�ִ�С�


####  ִ�д�Ƶͳ�ƺ�ͳ��������
ֱ���� SegTest���� Main�������������Ҫִ�еķ�������

```

public static void main(String[] args) {
         ConCompute();
        //Compute();
    }
    /**
     * ����ͳ��������--���߳� ��Ƶͳ���Լ�����
     */
    public static void ConCompute() {
        long t1 = System.currentTimeMillis();
        //  ��Ƶͳ�� ���������ڴ�
        WordCountConCompute wordCountConCompute = new WordCountConCompute();
        wordCountConCompute.compute();
        System.out.println("  ��Ƶͳ���� wcMap ������:" + wcMap.size());
        // ��������ͳ����
        MIERConCompute mierConCompute = new MIERConCompute();
        mierConCompute.compute();
        System.out.println("�ܺ�ʱ" + (System.currentTimeMillis() - t1) + " ms");
    }

    /**
     * ͳ��������-- ���߳� ��Ƶͳ�������
     */
    public static void Compute() {
        JsonSerializationUtil.saveCalculateResultToRedis();
    }
```

```
    /**
     * ����ͳ��������--���߳� ��Ƶͳ���Լ�����
     */
    public static void ConExtractWords() {
        long t1 = System.currentTimeMillis();
        ExtractWordsConCompute  extractWordsConCompute = new ExtractWordsConCompute();
        extractWordsConCompute.compute();
        System.out.println("�ܺ�ʱ" + (System.currentTimeMillis() - t1) + " ms");
    }
```

����� jar ��������ķ������£�

![](https://upload-images.jianshu.io/upload_images/4941834-c3911ecf766b17c1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![](https://upload-images.jianshu.io/upload_images/4941834-a6102572f3cb077c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![](https://upload-images.jianshu.io/upload_images/4941834-782ac0c3b4f9f9ac.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

���ú���֮��ʼ���

![��������jar��](https://upload-images.jianshu.io/upload_images/4941834-6a72bb014a2a2c53.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

