package im.engure.probs.p922;

/*

用 0 1 代表细胞的死亡和生存。
假如细胞线性排列，即除了两边每个细胞的左右各有一个细胞
经过一次迭代，当死细胞的左边或者右边有且仅有一个活细胞时他会复活。
活细胞永远不会死亡。
给出一个细胞排序序列，经过 m 次迭代，问最终细胞的生存状态。

010000000010, 3
---------------------
111000000111,    1
111100001111,    2
111110011111,    3

 */

import java.util.Arrays;

public class CellsDemo {

    public static void main(String[] args) {
        System.out.println(solve("010000000010", 3));
    }

    //常规做法，O(M*N)
    public static String solve(String s, int m) {

        char[] chs = s.toCharArray();
        char[] tmp;

        while (m-- > 0) {

            tmp = Arrays.copyOf(chs, chs.length);
            for (int i = 0; i < chs.length; i++) {

                if (chs[i] != '1') {
                    boolean left = (i > 0 && chs[i - 1] == '1');
                    boolean right = (i + 1 < chs.length && chs[i + 1] == '1');
                    if ((left && !right) || (!left && right))
                        tmp[i] = '1';
                }

            }
            chs = tmp;

        }
        return new String(chs);
    }

    /*
    换一种思路。

     */
    public static String process(String s, int m) {

        return "";
    }

}
