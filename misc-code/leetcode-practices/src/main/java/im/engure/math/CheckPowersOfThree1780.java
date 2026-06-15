package im.engure.math;

import java.util.Stack;

/**
 * @author engure
 */
public class CheckPowersOfThree1780 {

    public static void main(String[] args) {
        System.out.println(new CheckPowersOfThree1780().checkPowersOfThree(123));
        System.out.println(new CheckPowersOfThree1780().checkPowersOfThree(188));
        System.out.println(new CheckPowersOfThree1780().checkPowersOfThree(569));
        System.out.println(new CheckPowersOfThree1780().checkPowersOfThree(1));
        System.out.println(new CheckPowersOfThree1780().checkPowersOfThree(2));
        System.out.println(new CheckPowersOfThree1780().checkPowersOfThree(3));
        System.out.println(new CheckPowersOfThree1780().checkPowersOfThree(4));
        System.out.println(new CheckPowersOfThree1780().checkPowersOfThree(8888888));
        System.out.println(new CheckPowersOfThree1780().checkPowersOfThree(37));
    }

    /**
     * 时间空间都为 O(1)
     * <a href="https://leetcode.cn/problems/check-if-number-is-a-sum-of-powers-of-three/solution/shi-yao-by-engure-raf0/">...</a>
     *
     * @param n [1, 10^7]
     * @return n is power of three or not
     */
    public boolean checkPowersOfThree(int n) {
        //ready
        int sum = 1;
        int[] arr = new int[16];
        for (int i = 0; i < 16; i++) {
            arr[i] = sum;
            sum *= 3;
        }
        //check
        for (int i = 15; i >= 1; i--) {
            if (arr[i - 1] <= n && n < arr[i]) {
                n -= arr[i - 1];
                if (n == 0) {
                    return true;
                }
            }
        }
        return n == 0;
    }

    public boolean checkPowersOfThree2(int n) {
        return convert(n, 3).indexOf('2') == -1;
    }

    public static String convert(int num, int base) {
        StringBuilder sb = new StringBuilder();
        String all = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = all.substring(0, base);
        Stack<Character> s = new Stack<Character>();
        while (num != 0) {
            s.push(digits.charAt(num % base));
            num /= base;
        }
        while (!s.isEmpty()) {
            sb.append(s.pop());
        }
        return sb.toString();
    }
}
