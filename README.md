# SegAndNewWordDiscover
基于互信息和邻接信息熵的中文分词和新词发现


最近研究了下无词典分词，看了一些论文和博客文章，最终选取了基于 互信息和左右邻接信息熵的方案来做分词，在人民日报的数据评测上达到了一个比较好的效果，如果只是做词语提取或者新词发现效果会比用来做分词更好。

## 理论依据

### 互信息

根据熵的连锁规则，有 H（X，Y）＝H（X）＋H（Y|X）＝H（Y）＋H（X|Y） 因此， H（X）-H（X|Y）＝H（Y）-H（Y|X） 这个差叫做X和Y的互信息（mutual information, MI），记作I（X；Y）。 或者定义为：如果（X，Y）～p（x, y），则X，Y之间的互信息I（X； Y）＝H（X）-H（X|Y）。 I（X；Y）反映的是在知道了Y的值以后X的不确定性的减少量。可 以理解为Y的值透露了多少关于X的信息量。

![互信息和熵之间的关系](https://upload-images.jianshu.io/upload_images/4941834-7dbdf367424c7aac.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![互信息的推导和计算](https://upload-images.jianshu.io/upload_images/4941834-18a8d593ccc9a25a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

两个汉字组合之间的互信息能够反映这两个汉字组合之间的凝聚度，可以衡量其是否能内部成词。如："中南大学"，可能的组合有 {中，南大学}、{中南，大学}、{中南大，学}

汉字串 "中南大学"的互信息为 所有可能组合的累计和。


### 信息熵
1948 年，香农提出了“信息熵”(Shannon entropy/Information entropy) 的概念，才解决了对信息的量化度量问题。一条信息的信息量大小和它的不确定性有直接的关系。比如说，我们要搞清楚一件非常非常不确定的事，或是我们一无所知的事情，就需要了解大量的信息。相反，如果我们对某件事已经有了较多的了解，我们不需要太多的信息就能把它搞清楚。所以，从这个角度，我们可以认为，信息量的度量就等于不确定性的多少。

##H(x) = E[I(xi)] = E[ log(2,1/p(xi)) ] = -∑p(xi)log(2,p(xi)) (i=1,2,..n)

通过计算候选字符串的左右邻字信息熵可以从外部判断该字符串的自由度，自由度越高，代表的是该字符串与外部字符的独立程度，自由程度越高，该候选串越可能是一个词，可以作为词语边界的度量。

>我们用信息熵来衡量一个文本片段的左邻字集合和右邻字集合有多随机。考虑这么一句话“吃葡萄不吐葡萄皮不吃葡萄倒吐葡萄皮”，“葡萄”一词出现了四次，其中左邻字分别为 {吃, 吐, 吃, 吐} ，右邻字分别为 {不, 皮, 倒, 皮} 。根据公式，“葡萄”一词的左邻字的信息熵为 – (1/2) · log(1/2) – (1/2) · log(1/2) ≈ 0.693 ，它的右邻字的信息熵则为 – (1/2) · log(1/2) – (1/4) · log(1/4) – (1/4) · log(1/4) ≈ 1.04 。可见，在这个句子中，“葡萄”一词的右邻字更加丰富一些。

在上面这个例子中，葡萄的左右邻信息熵最终取左邻信息熵和右邻信息熵的最小值0.693。


### trie树

trie树又叫字典树，前缀树。是中文分词等任务中最常用到的数据结构，字典树有多种实现，双数组字典树达到了时间与空间的一种平衡。对字典树想要更多的了解可以参看 [小白详解 Trie 树](https://segmentfault.com/a/1190000008877595)，我觉得讲得很透彻了。


## 抽词实战

本次抽词从单词内部凝聚度和外部自由度角度进行候选字串的筛选，计算候选词串的互信息(指示了候选字串的内部凝聚度)，计算候选字串的左右邻信息熵(指示了候选字串的外部自由度)，通过互信息及 信息熵阈值过滤掉明显不符合要求的候选词串。在剩下的候选词串中通过归一化的手段得到他们的分数，最后通过分数从大到小排序，得出最终的抽词结果。

具体的抽词步骤：

##### 1.对待抽词文本进行预处理(待处理文本越大效果越好，因为本文采用的是基于统计的方法)，去掉非中文字符，留下候选字片段。

比如原句子：“大众汽车CEO文德森12日宣布，未来五年大众汽车将向北美市场投资70亿美元
，力争使包括奥迪在内的品牌到2018年实现年产量100万辆的目标。”，处理完后
>“大众汽车”，
“文德森”，
“日宣布”，
“未来五年大众汽车将向北美市场投资”，
“亿美元”，
“力争使包括奥迪在内的品牌到”，
“年实现年产量”，
“万辆的目标”。

得到如上所示的八个候选短句。

##### 2.对候选短句以nGram 方式进行FMM滑动取词，并统计候选词的词频

```
// 切分词  FMM 算法
    public static void FMMSegment(String text, boolean countWordFrequency) {
        // 额外统计单个字的词频
        wordCountSingleWord(text);
        if (text.length() == 1) {
            return;
        }
        int temp_max_len = Math.min(text.length() + 1, MAX_WORD_LEN);
        int p = 0;
        while (p < text.length()) {
            int q = 1;
            while (q < temp_max_len) {  // 控制取词的长度
                if (q == 1) {
                    q++;
                    continue;  // 长度为1略过,单个汉字不具有分词意义
                }
                // 取词串  p --> p+q
                if (p + q > text.length()) {
                    break;
                }
                String strChar = text.substring(p, p + q);
                // 统计词串的词频
                if (countWordFrequency) {
                    if (wcMap.containsKey(strChar)) {
                        wcMap.put(strChar, wcMap.get(strChar) + 1);
                    } else {
                        wcMap.put(strChar, 1);
                    }
                }
                q++;
            }
            p++;
        }
    }
```

如候选短句：**未来五年大众汽车将向北美市场投资**
通过FMM 算法取词。设最大取词长度为5，得到以下54个候选词

>[未来, 未来五, 未来五年, 未来五年大, 来五, 来五年, 来五年大, 来五年大众, 五年, 五年大, 五年大众, 五年大众汽, 年大, 年大众, 年大众汽, 年大众汽车, 大众, 大众汽, 大众汽车, 大众汽车将, 众汽, 众汽车, 众汽车将, 众汽车将向, 汽车, 汽车将, 汽车将向, 汽车将向北, 车将, 车将向, 车将向北, 车将向北美, 将向, 将向北, 将向北美, 将向北美市, 向北, 向北美, 向北美市, 向北美市场, 北美, 北美市, 北美市场, 北美市场投, 美市, 美市场, 美市场投, 美市场投资, 市场, 市场投, 市场投资, 场投, 场投资, 投资]

##### 3. 统计计算每个候选词串的互信息，左邻字信息熵，右邻字信息熵

计算信息熵主要用到的数据结构就是双数组字典树，通过字典树取到以当前字为前缀的一批词。为使得信息熵具有可对比性，当前候选词扩展一个字的算信息熵累计和，超过一个字的不算。算左邻字信息熵时，将候选字符串逆置是一个小小技巧。

```
/**
     * 添加所有切分词  计算互信息 信息熵等统计量
     */
    public void addAllSegAndCompute(Map<String, Integer> wcMap) {
        for (String seg : wcMap.keySet()) {
            trieRight.put(seg, wcMap.get(seg));     // 右前缀字典树
            trieLeft.put(HanUtils.reverseString(seg), wcMap.get(seg));  // 左前缀字典树
            totalCount = totalCount + wcMap.get(seg);    // 计算总词频
        }
        for (String seg : wcMap.keySet()) {
            // 1. 计算信息熵
            float rightEntropy = computeRightEntropy(seg);
            maxRE = Math.max(maxRE, rightEntropy);  // 求最大右信息熵   //totalRE = totalRE + rightEntropy;
            float leftEntropy = computeLeftEntropy(seg);
            maxLE = Math.max(maxLE, leftEntropy);  // 求最大左信息熵    // totalLE = totalLE + leftEntropy;
            // 2. 计算互信息
            float mi = computeMutualInformation(seg);
            maxMI = Math.max(maxMI, mi);   // 计算最大互信息  //totalMI = totalMI + mi;
            Term term = new Term(seg, wcMap.get(seg), mi, leftEntropy, rightEntropy);  // 这里没办法算最后得分
            // 将map存入redis中
            /**********************  redis存取 **************************/
            redis.hmset(seg, term.convertToMap());
            /**********************  redis存取 **************************/
        }
        wcMap.clear();   // 释放无用的内存
        Term max_term = new Term(MAX_KEY, 0, maxMI, maxLE, maxRE);
        redis.hmset(MAX_KEY, max_term.convertToMap());    // 保存最大值
        System.out.println("统计量计算总耗时: " + (System.currentTimeMillis() - t1) + "ms");
    }
```

信息熵计算：
```
/**
     * 计算左邻熵
     */
    public float computeLeftEntropy(String prefix) {
        Set<Map.Entry<String, Integer>> entrySet = trieLeft.prefixSearch(HanUtils.reverseString(prefix));
        return computeEntropy(entrySet, prefix);
    }

    /**
     * 计算右邻熵
     */
    public float computeRightEntropy(String prefix) {
        Set<Map.Entry<String, Integer>> entrySet = trieRight.prefixSearch(prefix);
        return computeEntropy(entrySet, prefix);
    }


    /**
     * 信息熵计算
     */
    private float computeEntropy(Set<Map.Entry<String, Integer>> entrySet, String prefix) {
        float totalFrequency = 0;
        for (Map.Entry<String, Integer> entry : entrySet) {
            if (entry.getKey().length() != prefix.length() + 1) {
                continue;
            }
            totalFrequency += entry.getValue();
        }
        float le = 0;
        for (Map.Entry<String, Integer> entry : entrySet) {
            if (entry.getKey().length() != prefix.length() + 1) {
                continue;
            }
            float p = entry.getValue() / totalFrequency;
            le += -p * Math.log(p);
        }
        return le;
    }
```

##### 4. 将候选词串以同一位置词开头划分为若干组。每组进行第一轮筛选，具体的办法为每个词进行归一化打分并逆序排序，最终召回得分最高的。在筛选的过程中进行互信息和信息熵阈值的过滤，所以第一轮筛选也可能没有候选词召回。

如候选短句：**未来五年大众汽车将向北美市场投资**
分为如下若干组：
>[[未来, 未来五, 未来五年, 未来五年大],
 [来五, 来五年, 来五年大, 来五年大众],
[五年, 五年大, 五年大众, 五年大众汽],
 [年大, 年大众, 年大众汽, 年大众汽车],
 [大众, 大众汽, 大众汽车, 大众汽车将],
 [众汽, 众汽车, 众汽车将, 众汽车将向],
 [汽车, 汽车将, 汽车将向, 汽车将向北],
[车将, 车将向, 车将向北, 车将向北美],
[将向, 将向北, 将向北美, 将向北美市],
 [向北, 向北美, 向北美市, 向北美市场],
 [北美, 北美市, 北美市场, 北美市场投],
[美市, 美市场, 美市场投, 美市场投资],
 [市场, 市场投, 市场投资],
 [场投, 场投资],
 [投资]]
##### 5. 对第一轮筛选召回的结果进行第二轮筛选，具体办法与第一轮类似，通过对候选词逆序排序，并过滤掉候选词之间有位置重叠关系的，最终得到抽词结果。

##### 6. 如果在此基础上需要做分词，将原句以抽词结果间隔开，我们认为剩下的也很大可能是词。分词效果在开源人民日报数据上评测其分词准确率得到了还算不错的效果。不过更好的做法是把这个只用来做新词发现，然后补充用户词典，辅助词典分词。


![抽取人民日报词语部分结果](https://upload-images.jianshu.io/upload_images/4941834-b01d96714980cfa9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![抽取人民日报词语部分结果](https://upload-images.jianshu.io/upload_images/4941834-af9945ad5bf7ef10.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


![人民日报数据上抽词的效果](https://upload-images.jianshu.io/upload_images/4941834-11403e8c896b33ee.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![人民日报数据上抽词的效果](https://upload-images.jianshu.io/upload_images/4941834-2ad1c5297b1bd890.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



代码实现开源在 github：https://github.com/shanjunwei/SegAndNewWordDiscover

##参考：

#####[互联网时代的社会语言学：基于SNS的文本数据挖掘](http://www.matrix67.com/blog/archives/5044)

#####[一种没有语料字典的分词方法](https://blog.csdn.net/ygrx/article/details/8926274)

#####[互信息公式及概述](http://www.omegaxyz.com/2018/08/02/mi/)
##### [Aho Corasick自动机结合DoubleArrayTrie极速多模式匹配](http://www.hankcs.com/program/algorithm/aho-corasick-double-array-trie.html)

#####[信息熵（香农熵）概述](http://www.omegaxyz.com/2018/05/07/information_entropy/)

#####[基于信息熵的无字典分词算法](https://blog.csdn.net/zxh19800626/article/details/50190803)

#####[Java开源项目cws_evaluation：中文分词器分词效果评估对比](https://github.com/shanjunwei/cws_evaluation)
