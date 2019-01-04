##  环境
### 1.JDK 1.8

详细过程参考：
##### [JDK安装与配置详细图文教程](https://www.cnblogs.com/tiankong101/p/4226559.html)
https://www.cnblogs.com/tiankong101/p/4226559.html

注意：代码使用Jdk 1.8 编写，使用到了Java8的一些特性如 Lambda 表达式,因此必须安装JDK 1.8以上版本。


### 2. Redis

Redis是一个开源的使用ANSI C语言编写、遵守BSD协议、支持网络、可基于内存亦可持久化的日志型、Key-Value数据库，并提供多种语言的API。
它通常被称为数据结构服务器，因为值（value）可以是 字符串(String), 哈希(Map), 列表(list), 集合(sets) 和 有序集合(sorted sets)等类型。

这里使用redis 的原意是将内存的压力转移到外部存储同时利用 redis的高效数据结构进行查找操作。

redis的安装详情参见:http://www.runoob.com/redis/redis-install.html

将redis 安装好后运行 redis 服务，运行redis服务的方法上面链接里有说明


segment.properties
可以自己修改部分参数调整运行的效果，如果不清楚参数的意义请保持默认值,基本上只需要自己修改需要抽词的文本路径 NOVEL_INPUT_PATH 和结果输出路径 EXTRACT_OUTPUT即可
```
# 语料输入路径,默认为"data\\test-text.txt"
CORPUS_INPUT_PATH=data/test-text.txt
NOVEL_INPUT_PATH=data/GameOfThrones.txt
#  抽词结果输出
EXTRACT_OUTPUT=data/result.txt
###############################################################################
# 互信息过滤阈值   默认1.0f
MI_THRESHOLD_VALUE=0.9f
# 左右信息熵比率阈值  默认0.4f
ENTROPY_THETA=0.4f
# 候选串切取最大长度  实际为所赋值减一 如6,实际的最大长度为5,默认5
MAX_WORD_LEN=6
# 左邻字信息熵 最小阈值 默认 0.01f
MIN_LEFT_ENTROPY=0.01f
# 右邻字信息熵 最小阈值 默认 0.01f
MIN_RIGHT_ENTROPY=0.01f
################################  redis相关  ################################
# redis 服务地址
REDIS_HOST=localhost
# redis 端口地址
REDIS_PORT= 8860
# redis 密码验证 默认为 root
REDIS_AUTH_PASSWORD=root
###############################################################################
# 是否开启调试，true会输出调试信息,默认为false
DEBUG_MODE=false
################################  多线程并发相关  ################################
# 并发词频统计线程数
WC_THREAD_NUM=20
COMPUTE_THREAD_NUM=20
# 并发抽词线程数
SEG_THREAD_NUM=3
```

![点击run.bat即可运行程序](https://upload-images.jianshu.io/upload_images/4941834-82bc55876fd9145f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

说明下，本次会打出三个包，可以选择运行并发统计量计算包和串行统计量计算包。默认运行并发统计量计算包，如果需要替换，通过编辑器打开run.bat将运行jar包的名字替换成co_compute.jar即可。统计计算时，应保证可用内存在10G以上。


如果不尝试自己打包，以下环境安装可以忽略----->
### 3.maven

包依赖工具使用Maven，需要提前安装好Maven
详细过程参考：
#####Maven安装与配置
https://www.jianshu.com/p/68bfcf9daeb7

 下载源码后直接用IDEA 打开,并打包成jar包。

### 4. IDEA

IDEA 全称IntelliJ IDEA，是用于java语言开发的集成环境（也可用于其他语言），IntelliJ在业界被公认为最好的java开发工具之一，尤其在智能代码助手、代码自动提示、重构、J2EE支持、Ant、JUnit、CVS整合、代码审查、 创新的GUI设计等方面的功能可以说是超常的。
这里使用Idea的原因是与maven 集成良好，当然也可以使用 eclipse，不过需要自己配置maven插件。

![下载Idea社区版](https://upload-images.jianshu.io/upload_images/4941834-4449d73f741d49a4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

IDEA 有企业版和社区版，企业版正版需要收费，社区版即开源免费版本，词语提取的代码是Java appliacation 程序不是 Java web 程序，因而社区版就够用了。

##  使用

### 1.用idea打开程序项目
![](https://upload-images.jianshu.io/upload_images/4941834-f5b79664d3d226eb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 2. 根据自己的需要设置相关的参数调整算法的准确率和效果
![image.png](https://upload-images.jianshu.io/upload_images/4941834-213f92509b6ff7b4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

安装好redis后，将redis服务的服务地址和密码填写在配置文件里


### 3. 根据自己的需要打包相应的Jar包

抽词程序分为 词频统计、 统计量计算、抽词大概这么三个步骤。
因为一些定制化的因素，我分为了 词频统计与统计量计算  和 抽词这么两个步骤。这两个步骤可以一起执行可以分开执行。


####  执行词频统计和统计量计算
直接在 SegTest类中 Main方法里调用你想要执行的方法，如

```

public static void main(String[] args) {
         ConCompute();
        //Compute();
    }
    /**
     * 并发统计量计算--多线程 词频统计以及计算
     */
    public static void ConCompute() {
        long t1 = System.currentTimeMillis();
        //  词频统计 保存结果到内存
        WordCountConCompute wordCountConCompute = new WordCountConCompute();
        wordCountConCompute.compute();
        System.out.println("  词频统计里 wcMap 的容量:" + wcMap.size());
        // 并发计算统计量
        MIERConCompute mierConCompute = new MIERConCompute();
        mierConCompute.compute();
        System.out.println("总耗时" + (System.currentTimeMillis() - t1) + " ms");
    }

    /**
     * 统计量计算-- 单线程 词频统计与计算
     */
    public static void Compute() {
        JsonSerializationUtil.saveCalculateResultToRedis();
    }
```

```
    /**
     * 并发统计量计算--多线程 词频统计以及计算
     */
    public static void ConExtractWords() {
        long t1 = System.currentTimeMillis();
        ExtractWordsConCompute  extractWordsConCompute = new ExtractWordsConCompute();
        extractWordsConCompute.compute();
        System.out.println("总耗时" + (System.currentTimeMillis() - t1) + " ms");
    }
```

打包成 jar 包，打包的方法如下：

![](https://upload-images.jianshu.io/upload_images/4941834-c3911ecf766b17c1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![](https://upload-images.jianshu.io/upload_images/4941834-a6102572f3cb077c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![](https://upload-images.jianshu.io/upload_images/4941834-782ac0c3b4f9f9ac.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

配置好了之后开始打包

![编译打包成jar包](https://upload-images.jianshu.io/upload_images/4941834-6a72bb014a2a2c53.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

