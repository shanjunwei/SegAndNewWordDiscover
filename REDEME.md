# SegAndNewWordDiscover
���ڻ���Ϣ���ڽ���Ϣ�ص����ķִʺ��´ʷ���


����о������޴ʵ�ִʣ�����һЩ���ĺͲ������£�����ѡȡ�˻��� ����Ϣ�������ڽ���Ϣ�صķ��������ִʣ��������ձ������������ϴﵽ��һ���ȽϺõ�Ч�������ֻ����������ȡ�����´ʷ���Ч������������ִʸ��á�

## ��������

### ����Ϣ

�����ص����������� H��X��Y����H��X����H��Y|X����H��Y����H��X|Y�� ��ˣ� H��X��-H��X|Y����H��Y��-H��Y|X�� ��������X��Y�Ļ���Ϣ��mutual information, MI��������I��X��Y���� ���߶���Ϊ�������X��Y����p��x, y������X��Y֮��Ļ���ϢI��X�� Y����H��X��-H��X|Y���� I��X��Y����ӳ������֪����Y��ֵ�Ժ�X�Ĳ�ȷ���Եļ��������� �����ΪY��ֵ͸¶�˶��ٹ���X����Ϣ����

![����Ϣ����֮��Ĺ�ϵ](https://upload-images.jianshu.io/upload_images/4941834-7dbdf367424c7aac.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![����Ϣ���Ƶ��ͼ���](https://upload-images.jianshu.io/upload_images/4941834-18a8d593ccc9a25a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

�����������֮��Ļ���Ϣ�ܹ���ӳ�������������֮������۶ȣ����Ժ������Ƿ����ڲ��ɴʡ��磺"���ϴ�ѧ"�����ܵ������ {�У��ϴ�ѧ}��{���ϣ���ѧ}��{���ϴ�ѧ}

���ִ� "���ϴ�ѧ"�Ļ���ϢΪ ���п�����ϵ��ۼƺ͡�


### ��Ϣ��
1948 �꣬��ũ����ˡ���Ϣ�ء�(Shannon entropy/Information entropy) �ĸ���Ž���˶���Ϣ�������������⡣һ����Ϣ����Ϣ����С�����Ĳ�ȷ������ֱ�ӵĹ�ϵ������˵������Ҫ�����һ���ǳ��ǳ���ȷ�����£���������һ����֪�����飬����Ҫ�˽��������Ϣ���෴��������Ƕ�ĳ�����Ѿ����˽϶���˽⣬���ǲ���Ҫ̫�����Ϣ���ܰ�������������ԣ�������Ƕȣ����ǿ�����Ϊ����Ϣ���Ķ����͵��ڲ�ȷ���ԵĶ��١�

##H(x) = E[I(xi)] = E[ log(2,1/p(xi)) ] = -��p(xi)log(2,p(xi)) (i=1,2,..n)

ͨ�������ѡ�ַ���������������Ϣ�ؿ��Դ��ⲿ�жϸ��ַ��������ɶȣ����ɶ�Խ�ߣ�������Ǹ��ַ������ⲿ�ַ��Ķ����̶ȣ����ɳ̶�Խ�ߣ��ú�ѡ��Խ������һ���ʣ�������Ϊ����߽�Ķ�����

>��������Ϣ��������һ���ı�Ƭ�ε������ּ��Ϻ������ּ����ж������������ôһ�仰�������Ѳ�������Ƥ�������ѵ�������Ƥ���������ѡ�һ�ʳ������ĴΣ����������ֱַ�Ϊ {��, ��, ��, ��} �������ֱַ�Ϊ {��, Ƥ, ��, Ƥ} �����ݹ�ʽ�������ѡ�һ�ʵ������ֵ���Ϣ��Ϊ �C (1/2) �� log(1/2) �C (1/2) �� log(1/2) �� 0.693 �����������ֵ���Ϣ����Ϊ �C (1/2) �� log(1/2) �C (1/4) �� log(1/4) �C (1/4) �� log(1/4) �� 1.04 ���ɼ�������������У������ѡ�һ�ʵ������ָ��ӷḻһЩ��

��������������У����ѵ���������Ϣ������ȡ������Ϣ�غ�������Ϣ�ص���Сֵ0.693��


### trie��

trie���ֽ��ֵ�����ǰ׺���������ķִʵ���������õ������ݽṹ���ֵ����ж���ʵ�֣�˫�����ֵ����ﵽ��ʱ����ռ��һ��ƽ�⡣���ֵ�����Ҫ������˽���Բο� [С����� Trie ��](https://segmentfault.com/a/1190000008877595)���Ҿ��ý��ú�͸���ˡ�


## ���ʵս

���γ�ʴӵ����ڲ����۶Ⱥ��ⲿ���ɶȽǶȽ��к�ѡ�ִ���ɸѡ�������ѡ�ʴ��Ļ���Ϣ(ָʾ�˺�ѡ�ִ����ڲ����۶�)�������ѡ�ִ�����������Ϣ��(ָʾ�˺�ѡ�ִ����ⲿ���ɶ�)��ͨ������Ϣ�� ��Ϣ����ֵ���˵����Բ�����Ҫ��ĺ�ѡ�ʴ�����ʣ�µĺ�ѡ�ʴ���ͨ����һ�����ֶεõ����ǵķ��������ͨ�������Ӵ�С���򣬵ó����յĳ�ʽ����

����ĳ�ʲ��裺

##### 1.�Դ�����ı�����Ԥ����(�������ı�Խ��Ч��Խ�ã���Ϊ���Ĳ��õ��ǻ���ͳ�Ƶķ���)��ȥ���������ַ������º�ѡ��Ƭ�Ρ�

����ԭ���ӣ�����������CEO�ĵ�ɭ12��������δ��������������������г�Ͷ��70����Ԫ
������ʹ�����µ����ڵ�Ʒ�Ƶ�2018��ʵ�������100������Ŀ�ꡣ�����������
>��������������
���ĵ�ɭ����
������������
��δ��������������������г�Ͷ�ʡ���
������Ԫ����
������ʹ�����µ����ڵ�Ʒ�Ƶ�����
����ʵ�����������
��������Ŀ�ꡱ��

�õ�������ʾ�İ˸���ѡ�̾䡣

##### 2.�Ժ�ѡ�̾���nGram ��ʽ����FMM����ȡ�ʣ���ͳ�ƺ�ѡ�ʵĴ�Ƶ

```
// �зִ�  FMM �㷨
    public static void FMMSegment(String text, boolean countWordFrequency) {
        // ����ͳ�Ƶ����ֵĴ�Ƶ
        wordCountSingleWord(text);
        if (text.length() == 1) {
            return;
        }
        int temp_max_len = Math.min(text.length() + 1, MAX_WORD_LEN);
        int p = 0;
        while (p < text.length()) {
            int q = 1;
            while (q < temp_max_len) {  // ����ȡ�ʵĳ���
                if (q == 1) {
                    q++;
                    continue;  // ����Ϊ1�Թ�,�������ֲ����зִ�����
                }
                // ȡ�ʴ�  p --> p+q
                if (p + q > text.length()) {
                    break;
                }
                String strChar = text.substring(p, p + q);
                // ͳ�ƴʴ��Ĵ�Ƶ
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

���ѡ�̾䣺**δ��������������������г�Ͷ��**
ͨ��FMM �㷨ȡ�ʡ������ȡ�ʳ���Ϊ5���õ�����54����ѡ��

>[δ��, δ����, δ������, δ�������, ����, ������, �������, ���������, ����, �����, �������, ���������, ���, �����, �������, ���������, ����, ������, ��������, ����������, ����, ������, ��������, ����������, ����, ������, ��������, ��������, ����, ������, ������, ��������, ����, ����, ������, ��������, ��, ����, ������, �����г�, ����, ������, �����г�, �����г�Ͷ, ����, ���г�, ���г�Ͷ, ���г�Ͷ��, �г�, �г�Ͷ, �г�Ͷ��, ��Ͷ, ��Ͷ��, Ͷ��]

##### 3. ͳ�Ƽ���ÿ����ѡ�ʴ��Ļ���Ϣ����������Ϣ�أ���������Ϣ��

������Ϣ����Ҫ�õ������ݽṹ����˫�����ֵ�����ͨ���ֵ���ȡ���Ե�ǰ��Ϊǰ׺��һ���ʡ�Ϊʹ����Ϣ�ؾ��пɶԱ��ԣ���ǰ��ѡ����չһ���ֵ�����Ϣ���ۼƺͣ�����һ���ֵĲ��㡣����������Ϣ��ʱ������ѡ�ַ���������һ��СС���ɡ�

```
/**
     * ��������зִ�  ���㻥��Ϣ ��Ϣ�ص�ͳ����
     */
    public void addAllSegAndCompute(Map<String, Integer> wcMap) {
        for (String seg : wcMap.keySet()) {
            trieRight.put(seg, wcMap.get(seg));     // ��ǰ׺�ֵ���
            trieLeft.put(HanUtils.reverseString(seg), wcMap.get(seg));  // ��ǰ׺�ֵ���
            totalCount = totalCount + wcMap.get(seg);    // �����ܴ�Ƶ
        }
        for (String seg : wcMap.keySet()) {
            // 1. ������Ϣ��
            float rightEntropy = computeRightEntropy(seg);
            maxRE = Math.max(maxRE, rightEntropy);  // ���������Ϣ��   //totalRE = totalRE + rightEntropy;
            float leftEntropy = computeLeftEntropy(seg);
            maxLE = Math.max(maxLE, leftEntropy);  // ���������Ϣ��    // totalLE = totalLE + leftEntropy;
            // 2. ���㻥��Ϣ
            float mi = computeMutualInformation(seg);
            maxMI = Math.max(maxMI, mi);   // ���������Ϣ  //totalMI = totalMI + mi;
            Term term = new Term(seg, wcMap.get(seg), mi, leftEntropy, rightEntropy);  // ����û�취�����÷�
            // ��map����redis��
            /**********************  redis��ȡ **************************/
            redis.hmset(seg, term.convertToMap());
            /**********************  redis��ȡ **************************/
        }
        wcMap.clear();   // �ͷ����õ��ڴ�
        Term max_term = new Term(MAX_KEY, 0, maxMI, maxLE, maxRE);
        redis.hmset(MAX_KEY, max_term.convertToMap());    // �������ֵ
        System.out.println("ͳ���������ܺ�ʱ: " + (System.currentTimeMillis() - t1) + "ms");
    }
```

��Ϣ�ؼ��㣺
```
/**
     * ����������
     */
    public float computeLeftEntropy(String prefix) {
        Set<Map.Entry<String, Integer>> entrySet = trieLeft.prefixSearch(HanUtils.reverseString(prefix));
        return computeEntropy(entrySet, prefix);
    }

    /**
     * ����������
     */
    public float computeRightEntropy(String prefix) {
        Set<Map.Entry<String, Integer>> entrySet = trieRight.prefixSearch(prefix);
        return computeEntropy(entrySet, prefix);
    }


    /**
     * ��Ϣ�ؼ���
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

##### 4. ����ѡ�ʴ���ͬһλ�ôʿ�ͷ����Ϊ�����顣ÿ����е�һ��ɸѡ������İ취Ϊÿ���ʽ��й�һ����ֲ��������������ٻص÷���ߵġ���ɸѡ�Ĺ����н��л���Ϣ����Ϣ����ֵ�Ĺ��ˣ����Ե�һ��ɸѡҲ����û�к�ѡ���ٻء�

���ѡ�̾䣺**δ��������������������г�Ͷ��**
��Ϊ���������飺
>[[δ��, δ����, δ������, δ�������],
 [����, ������, �������, ���������],
[����, �����, �������, ���������],
 [���, �����, �������, ���������],
 [����, ������, ��������, ����������],
 [����, ������, ��������, ����������],
 [����, ������, ��������, ��������],
[����, ������, ������, ��������],
[����, ����, ������, ��������],
 [��, ����, ������, �����г�],
 [����, ������, �����г�, �����г�Ͷ],
[����, ���г�, ���г�Ͷ, ���г�Ͷ��],
 [�г�, �г�Ͷ, �г�Ͷ��],
 [��Ͷ, ��Ͷ��],
 [Ͷ��]]
##### 5. �Ե�һ��ɸѡ�ٻصĽ�����еڶ���ɸѡ������취���һ�����ƣ�ͨ���Ժ�ѡ���������򣬲����˵���ѡ��֮����λ���ص���ϵ�ģ����յõ���ʽ����

##### 6. ����ڴ˻�������Ҫ���ִʣ���ԭ���Գ�ʽ���������������Ϊʣ�µ�Ҳ�ܴ�����Ǵʡ��ִ�Ч���ڿ�Դ�����ձ�������������ִ�׼ȷ�ʵõ��˻��㲻���Ч�����������õ������ǰ����ֻ�������´ʷ��֣�Ȼ�󲹳��û��ʵ䣬�����ʵ�ִʡ�


![��ȡ�����ձ����ﲿ�ֽ��](https://upload-images.jianshu.io/upload_images/4941834-b01d96714980cfa9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![��ȡ�����ձ����ﲿ�ֽ��](https://upload-images.jianshu.io/upload_images/4941834-af9945ad5bf7ef10.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


![�����ձ������ϳ�ʵ�Ч��](https://upload-images.jianshu.io/upload_images/4941834-11403e8c896b33ee.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![�����ձ������ϳ�ʵ�Ч��](https://upload-images.jianshu.io/upload_images/4941834-2ad1c5297b1bd890.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



����ʵ�ֿ�Դ�� github��https://github.com/shanjunwei/SegAndNewWordDiscover

##�ο���

#####[������ʱ�����������ѧ������SNS���ı������ھ�](http://www.matrix67.com/blog/archives/5044)

#####[һ��û�������ֵ�ķִʷ���](https://blog.csdn.net/ygrx/article/details/8926274)

#####[����Ϣ��ʽ������](http://www.omegaxyz.com/2018/08/02/mi/)
##### [Aho Corasick�Զ������DoubleArrayTrie���ٶ�ģʽƥ��](http://www.hankcs.com/program/algorithm/aho-corasick-double-array-trie.html)

#####[��Ϣ�أ���ũ�أ�����](http://www.omegaxyz.com/2018/05/07/information_entropy/)

#####[������Ϣ�ص����ֵ�ִ��㷨](https://blog.csdn.net/zxh19800626/article/details/50190803)

#####[Java��Դ��Ŀcws_evaluation�����ķִ����ִ�Ч�������Ա�](https://github.com/shanjunwei/cws_evaluation)
