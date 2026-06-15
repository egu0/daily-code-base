package im.engure.string;

import java.util.LinkedList;
import java.util.List;

/**
 * 情感丰富的文字
 *
 * @author Administrator
 * 输入：
 * s = "heeellooo"
 * words = ["hello", "hi", "helo"]
 * 输出：
 * 1
 */
public class ExpressiveWords809 {
    public static void main(String[] args) {
        System.out.println(new ExpressiveWords809().expressiveWords2("aaa", new String[]{"aaaaa"}));
    }

    /**
     * “并行判断” words[j] 是否匹配 s[i]
     *
     * @param s
     * @param words
     * @return
     */
    public int expressiveWords2(String s, String[] words) {
        if (s.length() < 3) {
            return 0;
        }
        char[] src = s.toCharArray();
        int len = words.length;
        List<char[]> chs = new LinkedList<>();
        for (String word : words) {
            chs.add(word.toCharArray());
        }
        int[] indexes = new int[len];
        int i = 0;
        while (i < src.length) {
            int newI = idxOfEndChar(src, src[i], i + 1);
            for (int j = 0; j < len; j++) {
                //idx记录当前字符串 words[j] 的遍历位置
                int idx = indexes[j];
                if (idx < 0) {
                    continue;
                }
                //toCharArray利用 System.arrayCopy(), 此处待优化
                char[] dst = chs.get(j);
                if (idx == dst.length || src[i] != dst[idx]) {
                    indexes[j] = -1;
                } else {
                    int newIdx = idxOfEndChar(dst, dst[idx], idx + 1);
                    if ((newI - i > newIdx - idx && newI - i + 1 < 3) || newI - i < newIdx - idx) {
                        indexes[j] = -1;
                    } else {
                        indexes[j] = newIdx + 1;
                    }
                }
            }
            i = newI + 1;
        }
        //output
        int ans = 0;
        for (int j = 0; j < len; j++) {
            if (indexes[j] == words[j].length()) {
                ans++;
            }
        }
        return ans;
    }

    /**
     * “双指针”法
     *
     * @param s
     * @param words
     * @return
     */
    public int expressiveWords(String s, String[] words) {
        if (s.length() < 3) {
            return 0;
        }
        char[] src = s.toCharArray();
        int ans = 0;
        for (String word : words) {
            char[] dst = word.toCharArray();
            int i = 0, j = 0;
            while (true) {
                if (src[i] != dst[j]) {
                    break;
                } else {
                    int _i = idxOfEndChar(src, src[i], i + 1);
                    int _j = idxOfEndChar(dst, src[i], j + 1);
                    if ((_i - i > _j - j && _i - i + 1 < 3) || _i - i < _j - j) {
                        break;
                    }
                    j = _j + 1;
                    i = _i + 1;
                }

                if (i == src.length && j == dst.length) {
                    ans++;
                    break;
                } else if (i == src.length || j == dst.length) {
                    break;
                }
            }
        }
        return ans;
    }

    private int idxOfEndChar(char[] src, char ch, int start) {
        int i;
        for (i = start; i < src.length; i++) {
            if (src[i] != ch) {
                return i - 1;
            }
        }
        return src.length - 1;
    }
}
