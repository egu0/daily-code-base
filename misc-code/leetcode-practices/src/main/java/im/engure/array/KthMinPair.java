package im.engure.array;

/*

“第 k 小的数值对”

长度为 N 的数组 arr，一定可以组成 N^2 个数值对。例如 arr = [3，1，2]，
数值对有 (3，3)(3，1)(3，2)(1，3)(1，1)(1，2)(2，3)(2，1)(2，2)，也就是任意两个数都有数值对，而且自己和自己也算数值对。
数值对怎么排序？规定，第一维数据从小到大，第一维数据一样的，第二维数组也从小到天。所以上面的数值对排序的结果为∶
(1，1)(1，2)(1，3)(2，1)(2，2)(2，3)(3，1)(3，2)(3，3)
给定一个数组 arr，和整数 k，返回第 k 小的数值对。

 */

import java.util.Arrays;
import java.util.Comparator;

public class KthMinPair {

    static class Pair {
        public int x, y;

        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Pair{" + "x=" + x + ", y=" + y + '}';
        }
    }

    static class PairComparator implements Comparator<Pair> {
        @Override
        public int compare(Pair o1, Pair o2) {
            return o1.x != o2.x ? o1.x - o2.x : o1.y - o2.y;
        }
    }

    /**
     * 暴力解法, O(N^2 * log(N^2))
     */
    static Pair kthMinPair(int[] arr, int k) {
        int LEN = arr.length;
        if (k > LEN * LEN) return null;

        Pair[] all = new Pair[LEN * LEN];
        int index = 0;
        for (int i = 0; i < LEN; i++) {
            for (int j = 0; j < LEN; j++) {
                all[index++] = new Pair(arr[i], arr[j]);
            }
        }
        Arrays.sort(all, new PairComparator());
        return all[k - 1];
    }

    /**
     * 观察数值对排序后的规律，只用对初始数组排序即可 O(NlogN)
     */
    static Pair kthMinPair2(int[] arr, int k) {
        int len = arr.length;
        if (k > len * len) {
            return null;
        }
        Arrays.sort(arr);

        //定位第一个数
        int first = arr[(k - 1) / len];

        //定位第二个数
        int lessFirst = 0, equalFirst = 0;
        for (int i = 0; i < len && arr[i] <= first; i++) {
            if (arr[i] == first) {
                equalFirst++;
            } else {
                lessFirst++;
            }
        }

        int rest = k - lessFirst * len;
        int second = arr[(rest - 1) / equalFirst];
        return new Pair(first, second);
    }

    /**
     * O(N) 做法
     * 解法 2 中对数组进行排序，目的就是计算数组中第 x 小的数字
     * 可以借助 bfprt 算法计算数组中第 x 小的数字
     *
     * @param arr
     * @param k
     * @return
     */
    static Pair kthMinPair3(int[] arr, int k) {
        int len = arr.length;
        if (k > len * len) {
            return null;
        }

        //int first = arr[(k - 1) / LEN];
        // 第 x 小的元素（x = 1...）
        int first = BfprtUtil.bfprt(arr, 0, arr.length - 1, (k - 1) / len + 1);

        int lessFirst = 0, equalFirst = 0;
        for (int i = 0; i < len; i++) {
            if (arr[i] == first) {
                equalFirst++;
            } else if (arr[i] < first) {
                lessFirst++;
            }
        }

        int rest = k - lessFirst * len;
        int second = BfprtUtil.bfprt(arr, 0, arr.length - 1, (rest - 1) / equalFirst + 1);
        return new Pair(first, second);
    }


    public static void main(String[] args) {
        int[] arr = new int[]{1, 1, 2, 3, 3, 4, 5, 5, 5, 6};
        System.out.println(kthMinPair(arr, 82));
        System.out.println(kthMinPair2(arr, 82));
        System.out.println(kthMinPair3(arr, 82));
    }

}
