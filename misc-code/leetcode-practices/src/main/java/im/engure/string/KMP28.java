package im.engure.string;

import java.util.Arrays;

public class KMP28 {
    public static void main(String[] args) {
        System.out.println(new Solution28().strStr("mississippi", "issip"));
        System.out.println(Solution28_2.indexOf("mississippi", "issip"));
    }
}

/**
 * 阿三哥的 KMP 教程，干货！https://www.bilibili.com/video/BV18k4y1m7Ar?p=1
 * 时间：82%
 * <p>
 */

//不把偏移量放在next中，模式串匹配时进行偏移 感觉较为简单，本代码使用此种，也是阿三哥讲的方法
//ababxab
//ababaab
//0012312

//将偏移量放在next中，模式串匹配时不用进行偏移
//ababxab
//ababaab
//0001231

//本质上都是KMP算法，思想一样，主串指针不回溯，
//遇到不匹配时找失配位置左侧的最长相等前后缀，将模式串指针指向前缀 0..k 的下一个位置 k-1，继续与主串比较

class Solution28 {
    public int strStr(String haystack, String needle) {
        if (needle == null || needle.length() == 0) return 0;

        int[] next = getPrefixTable(needle.toCharArray());
        char[] str = haystack.toCharArray();
        char[] ps = needle.toCharArray();

        System.out.println(Arrays.toString(next));

        int j = 0, i = 0;

        while (i < str.length) {

            if (str[i] == ps[j]) {
                j++;
                i++;
                if (j == next.length) { /* j指针到头，匹配成功 */
                    return i - j;
                }
            } else {
                if (j >= 1) {
                    j = next[j - 1];/*找 str[0...i] */
                } else {
                    i++;/* next[0] != str[i] */
                }
            }

        }

        return -1;
    }

    /**
     * 计算next数组，next[0]的设置和偏移量的设置都无所谓，思想都是kmp，即不回溯主串
     * <p>
     * abx... 与 ab...abc... 匹配
     *
     * @param ps 模式串
     */
    public int[] getPrefixTable(char[] ps) {

        int LEN;
        if (ps == null || (LEN = ps.length) == 0) return new int[0];
        int[] next = new int[LEN];
        next[0] = 0;
        int i = 1, k = 0;

        while (i < LEN) {

            if (ps[i] == ps[k]) {/*相同，增加长度*/
                next[i++] = ++k;
            } else {
                if (k >= 1) { /* k-1>=0,且不同，就继续计算*/
                    k = next[k - 1];
                } else { /* k=0，不相等，到头了，结束 */
                    next[i++] = k;
                }
            }

        }

        return next;
    }

}

/*
 * next数组的缺陷：
 * 特殊情况：
 * aaaaaaaaaaaaaaaaaaaab
 * aaab
 *
 * https://www.jianshu.com/p/0267b76368d1
 */
class Solution28_2 {
    public static int indexOf(String target, String pattern) {

        int i = 0, j = 0;

        int[] next = getNext(pattern);

        System.out.println(Arrays.toString(next));

        while (i < target.length()) {

            if (j == -1 || target.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            } else {
                j = next[j];
            }
            if (j == pattern.length()) {
                return i - j;
            }
        }
        return -1;
    }

    private static int[] getNext(String pattern) {

        int j = 0, k = -1;
        int[] next = new int[pattern.length()];
        next[0] = -1;
        while (j < pattern.length() - 1) {
            if (k == -1 || pattern.charAt(j) == pattern.charAt(k)) {
                j++;
                k++;
                //改进next数组
                if (pattern.charAt(j) != pattern.charAt(k)) {
                    next[j] = k;
                } else {
                    next[j] = next[k];
                }
            } else {
                k = next[k];
            }
        }
        return next;
    }
}


