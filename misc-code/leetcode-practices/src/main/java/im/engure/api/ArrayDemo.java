package im.engure.api;

import java.util.Arrays;

/**
 * @author Administrator
 */
public class ArrayDemo {
    public static void main(String[] args) {
        int[] a = new int[]{1, 2};
        int[] b = new int[2];
        System.arraycopy(a, 0, b, 0, a.length);
        //System.out.println();

        int mod = 10 ^ 3 + 7;
        System.out.println(mod);

        stringDemo();

        new ArrayDemo().getIntersection(new int[]{1, 2, 3, 4}, new int[]{2, 4});
    }

    private static void stringDemo() {
        System.out.println("------------------------");
        String a = "ab", b = a + "", c = "a" + "b";
        System.out.println(a == b);
        System.out.println(a == c);
    }

    /**
     * 有两个已经排好序(从小到大)的整数数组，现要求取出这两个数组的交集(即两个数组中都存在的整数值)。
     * 请设计一检索算法实现该功能（要求只能使用基本类型与数组）。
     *
     * @param aArray
     * @param bArray
     * @return
     */

    // 时间复杂度 O(M+N), 空间复杂度 O(N)
    public int[] getIntersection(int[] aArray, int[] bArray) {

        int minLen = Math.min(aArray.length, bArray.length);
        int[] res = new int[minLen];
        int index = 0;

        int i = 0, j = 0;
        while (i < aArray.length && j < bArray.length) {
            if (aArray[i] == bArray[j]) {
                res[index++] = aArray[i];
                i++;
                j++;
            } else if (aArray[i] > bArray[j]) {
                j++;
            } else {
                i++;
            }
        }

        return Arrays.copyOf(res, index);
    }

}
