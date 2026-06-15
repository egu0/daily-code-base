package im.engure.string;

import im.engure.util.MyAssertions;

import java.util.Arrays;

/**
 * @author Administrator
 */
public class BeautySum1781 {

    public static void main(String[] args) {
        BeautySum1781 o = new BeautySum1781();
        MyAssertions.assertEqual(o.beautySum("a"), 0);
        MyAssertions.assertEqual(o.beautySum("ab"), 0);
        MyAssertions.assertEqual(o.beautySum("aab"), 1);
        MyAssertions.assertEqual(o.beautySum("aabcb"), 5);
        MyAssertions.assertEqual(o.beautySum("aabcbaa"), 17);
    }

    /**
     * “剪枝”，O(N^2)，24ms
     * ref: <a href="https://leetcode.cn/problems/sum-of-beauty-of-all-substrings/solution/by-feng-li-luo-hua-jg3b/">...</a>
     */
    public int beautySum(String s) {
        int len = s.length();
        char[] chs = s.toCharArray();
        int res = 0, maxCharIdx, minCharIdx, charIdx;
        int[] cnt = new int[26];

        for (int i = 0; i < len - 1; i++) {
            Arrays.fill(cnt, 0);
            minCharIdx = maxCharIdx = chs[i] - 'a';
            cnt[maxCharIdx]++;

            for (int j = i + 1; j < len; j++) {
                charIdx = chs[j] - 'a';
                cnt[charIdx]++;


                //确定出现最少次的字母索引 minCharIdx
                if (cnt[charIdx] < cnt[minCharIdx]) {
                    minCharIdx = charIdx;
                } else if (cnt[charIdx] == cnt[minCharIdx]) {
                    for (int k = 0; k < 26; k++) {
                        if (cnt[k] != 0 && cnt[k] < cnt[minCharIdx]) {
                            minCharIdx = k;
                            break;
                        }
                    }
                }

                //确定出现最多次的字母索引 maxCharIdx
                if (cnt[charIdx] > cnt[maxCharIdx]) {
                    maxCharIdx = charIdx;
                }

                res += (cnt[maxCharIdx] - cnt[minCharIdx]);
            }
        }

        return res;
    }

    /**
     * 前缀和，O(N^2)，60ms
     */
    public int beautySum2(String s) {
        int length = s.length();
        char[] chs = s.toCharArray();
        int[][] cnt = new int[length + 1][26];
        cnt[1][chs[0] - 'a']++;

        for (int i = 2; i < length + 1; i++) {
            cnt[i][chs[i - 1] - 'a']++;
            for (int j = 0; j < 26; j++) {
                cnt[i][j] += cnt[i - 1][j];
            }
        }

        int res = 0;
        for (int i = 0; i < length + 1; i++) {
            for (int j = i + 2; j < length + 1; j++) {
                //cnt[j], cnt[i]
                int max = 0;
                int min = 501;
                for (int k = 0; k < 26; k++) {
                    int val = cnt[j][k] - cnt[i][k];
                    max = Math.max(val, max);
                    if (val != 0 && val < min) {
                        min = val;
                    }
                }
                res += (max - min);
            }
        }

        return res;
    }

    /**
     * O(N^2)、70ms
     */
    public int beautySum1(String s) {
        int len = s.length();
        char[] chs = s.toCharArray();
        int res = 0;
        int[] cnt = new int[26];

        for (int i = 0; i < len - 1; i++) {
            Arrays.fill(cnt, 0);
            cnt[chs[i] - 'a']++;
            cnt[chs[i + 1] - 'a']++;

            for (int j = i + 2; j < len; j++) {
                cnt[chs[j] - 'a']++;

                int max = 0;
                int min = 501;
                for (int k = 0; k < 26; k++) {
                    max = Math.max(max, cnt[k]);
                    if (cnt[k] != 0) {
                        min = Math.min(min, cnt[k]);
                    }
                }
                res += (max - min);
            }
        }

        return res;
    }
}
