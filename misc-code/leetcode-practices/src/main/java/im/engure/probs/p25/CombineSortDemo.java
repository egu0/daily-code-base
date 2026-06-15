package im.engure.probs.p25;

import java.util.*;

/*

题目 blablabla
将一个二维数组排序成一维数组，倒序输出。

--> [[3,1,2],[4,5,6],[9,8,7]]
<-- [9,8,7,6,5,4,3,2,1]

-------------------

使用求总排序的方法 和 下边归并的方法都是通过率 20%
推测要么理解错误题目，要么给的答案错误

 */


public class CombineSortDemo {

    public static void main(String[] args) {
        int[] res = new CombineSortDemo().rec(new int[][]{{1, 3, 2}, {4, 5, 6}, {9, 8, 7}});
        System.out.println(Arrays.toString(res));
    }

    int[][] arr;

    public int[] rec(int[][] results) {
        // 归并排序
        arr = results;
        return process(0, results.length - 1);
    }

    public int[] process(int l, int r) {
        if (l > r) return new int[]{};
        else if (l == r) {
            Arrays.sort(arr[l]);
            int[] res = new int[arr[l].length];
            for (int i = 0; i < res.length; i++) {
                res[i] = arr[l][res.length - i - 1];
            }
            return res;
        } else {
            int mid = (r - l) / 2 + l;
            int[] a1 = process(l, mid);
            int[] a2 = process(mid + 1, r);
            int i = 0, j = 0;
            int len = a1.length + a2.length;
            int[] res = new int[len];
            int index = 0;

            while (i < a1.length || j < a2.length) {
                if (i == a1.length) {
                    for (; index < len; index++) res[index] = a2[j++];
                    break;
                } else if (j == a2.length) {
                    for (; index < len; index++) res[index] = a1[i++];
                    break;
                } else {
                    if (a1[i] < a2[j]) {
                        res[index++] = a2[j++];
                    } else {
                        res[index++] = a1[i++];
                    }
                }
            }
            return res;
        }
    }
}



