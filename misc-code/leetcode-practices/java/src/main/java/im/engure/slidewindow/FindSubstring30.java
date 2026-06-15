package im.engure.slidewindow;

import java.util.*;

/**
 * @author engure
 */
public class FindSubstring30 {
    public static void main(String[] args) {
//        System.out.println(new Solution30().findSubstring("aaaaaaaaaaaaaa", new String[]{"aa", "aa"}));
//        System.out.println(new Solution30().findSubstring("wordgoodgoodgoodbestword", new String[]{"word", "good", "best", "word"}));
//        System.out.println(new Solution30().findSubstring("barfoofoobarthefoobarman", new String[]{"foo", "bar", "the"}));
//        System.out.println(new Solution30().findSubstring("barfoothefoobarman", new String[]{"bar", "foo"}));
        System.out.println(new Solution30().findSubstring("ababaab", new String[]{"ab", "ba", "ba"}));

    }
}
/*
统计结果
ab - 1
ba - 2
==========================
                n-1 n
##	a b a b a a b
ab	1 0 1 0 0 1 0   0
ba	0 1 0 1 0 0 0   0

*/

class Solution30 {

    /**
     * 此种方法耗时 23ms
     *
     * @param s
     * @param words
     * @return
     */
    public List<Integer> findSubstring(String s, String[] words) {
        //1- 去重&统计：计数、记录
        Map<String, Integer> count = new HashMap<>();
        Map<String, Integer> idx = new HashMap<>();
        List<String> ls = new LinkedList<>();
        int j = 0;
        for (String word : words) {
            if (count.containsKey(word)) {
                count.put(word, count.get(word) + 1);
            } else {
                ls.add(word);
                idx.put(word, j++);
                count.put(word, 1);
            }
        }

        int strLen = s.length();
        int wordCount = count.keySet().size();
        int wordLen = words[0].length();
        //2- 统计每个子串在原串中的位置，比如arr[i]表示子串ls.get(i)在原串中的位置
        int[][] arr = new int[wordCount][strLen + 1];
        for (j = strLen - 1; j >= 0; j--) {
            if (j + wordLen <= strLen) {
                String sub0 = s.substring(j, j + wordLen);
                if (count.containsKey(sub0)) {
                    arr[idx.get(sub0)][j] = 1;
                }
            }
        }

        //输出arr表
        System.out.print(ls.get(0).replaceAll("[a-z]", "#") + "\t");
        for (int i = 0; i < s.length(); i++) {
            System.out.print(s.charAt(i) + " ");
        }
        System.out.println();
        for (int i = 0; i < wordCount; i++) {
            System.out.print(ls.get(i) + "\t");
            for (int k = 0; k <= strLen; k++) {
                System.out.print(arr[i][k] + " ");
            }
            System.out.println();
        }

        List<Integer> res = new LinkedList<>();
        int segLen = words.length;
        for (int i = 0; i < strLen; i++) {
            //按子串长度截取的子串不在words中
            if (i + wordLen < strLen) {
                String sub0 = s.substring(i, i + wordLen);
                if (!count.containsKey(sub0)) {
                    continue;
                }
            }
            //当前位置i后的长度不小于所有子串总长度
            if (i + (segLen - 1) * wordLen < strLen) {
                int[] r = new int[wordCount];
                boolean ok = true;
                //按偏移量截取"子串"，利用数组 r 统计每个子串的出现次数
                for (int k = 0; k < segLen; k++) {
                    for (int l = 0; l < wordCount; l++) {
                        if (arr[l][i + k * wordLen] == 1) {
                            r[l]++;
                            break;
                        }
                    }
                }
                //计算每个子串次数是否与统计的一致，记录结果
                for (int l = 0; l < wordCount && ok; l++) {
                    if (r[l] != count.get(ls.get(l))) {
                        ok = false;
                    }
                }
                if (ok) {
                    res.add(i);
                }
            }
        }

        return res;
    }

    /**
     * 耗时 2ms
     *
     * @param s
     * @param words
     * @return
     */
    public List<Integer> findSubstring3(String s, String[] words) {
        // idMap[str] 记录字符串 str 对应的 id。
        Map<String, Integer> idMap = new HashMap<>();
        for (String word : words) {
            idMap.putIfAbsent(word, idMap.size());
        }
        // cntMap[i] 记录字符串 i 在 words 中出现的次数。
        int[] cntMap = new int[idMap.size()];
        for (String word : words) {
            cntMap[idMap.get(word)]++;
        }
        List<Integer> indices = new ArrayList<>();
        char[] str = s.toCharArray();
        int n = str.length, m = words.length;
        int wordLen = words[0].length(), totalLen = wordLen * m;
        for (int i = 0; i < wordLen; i++) {
            for (int j = i; j <= n - totalLen; j += wordLen) {
                // wndCntMap[i] 记录字符串 i 在当前滑动窗口内出现的次数。
                int[] wndCntMap = new int[idMap.size()];
                // 从右往左遍历当前区间的每一个单词。
                for (int k = m - 1; k >= 0; k--) {
                    // 当前单词的起始位置。
                    int begin = j + k * wordLen;
                    String word = new String(str, begin, wordLen);
                    Integer id = idMap.get(word);
                    if (id == null || wndCntMap[id]++ == cntMap[id]) {
                        j = begin;
                        // 当前区间出现 words 中不存在的单词，直接跳过。
                        break;
                    }
                    if (k == 0) {
                        indices.add(j);
                    }
                }
            }
        }
        return indices;
    }

    /*
    经典滑动窗口。有点情况走不通
     */
    public List<Integer> findSubstring2(String s, String[] words) {
        //get ready
        Map<String, Integer> ss = new HashMap<>();
        for (String s0 : words) {
            ss.put(s0, ss.getOrDefault(s0, 0) + 1);
        }
        int N = ss.keySet().size();
        int[][] arr = new int[N][2];
        Map<String, Integer> idx = new HashMap<>();
        int j = 0;
        for (String s0 : ss.keySet()) {
            idx.put(s0, j);//记录元素下标
            arr[j][0] = ss.get(s0);//应满足的元素个数
            j++;
        }
        //slide window
        List<Integer> res = new LinkedList<>();
        int len = words[0].length();
        boolean special = false;
        if (ss.size() == 1) {
            String s0 = ss.keySet().iterator().next();
            char[] chs = s0.toCharArray();
            if (s0.lastIndexOf(chs[0]) == chs.length - 1) {
                special = true;
            }
        }
        int left = 0, right = 0;
        while (right + len <= s.length()) {
            String substr = s.substring(right, right + len);
            if (ss.keySet().contains(substr)) {
                j = idx.get(substr);
                arr[j][1]++;

                //检查对应关系
                int output = 0;//0满足条件，1缺少子串，2多了子串
                for (int i = 0; i < N && (output == 0 || output == 1); i++) {
                    if (arr[i][0] > arr[i][1]) {
                        output = 1;
                    } else if (arr[i][0] < arr[i][1]) {
                        output = 2;
                    }
                }

                if (output == 0) {
                    res.add(left);
                    //删掉最左边的子串
                    String sub1 = s.substring(left, left + len);
                    arr[idx.get(sub1)][1]--;
                    if ((right + len) <= s.length() - 1 && s.charAt(right + len) == s.charAt(left + 1) && special) {
                        left++;
                        right++;
                    } else {
                        left += len;
                        right += len;
                    }
                } else if (output == 2) {
                    //删掉最左边的substr子串
                    while (left <= right) {
                        String sub1 = s.substring(left, left + len);
                        arr[idx.get(sub1)][1]--;
                        left += len;
                        if (substr.equals(sub1)) {
                            break;
                        }
                    }
                    right += len;
                } else {
                    right += len;
                }
            } else {
                for (int i = 0; i < N; i++) {
                    arr[i][1] = 0;
                }
                right++;
                left = right;
            }
        }
        return res;
    }
}

