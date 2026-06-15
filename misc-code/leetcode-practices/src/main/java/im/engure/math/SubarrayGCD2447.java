package im.engure.math;


/**
 * 题目：2447. 最大公因数等于 K 的子数组数目
 * <p>
 * 给你一个整数数组 nums 和一个整数 k ，请你统计并返回 nums 的子数组中元素的最大公因数等于 k 的子数组数目。
 * <p>
 * 子数组 是数组中一个连续的非空序列。
 * <p>
 * 数组的最大公因数 是能整除数组中所有元素的最大整数。
 * <p>
 *
 * <a href="https://leetcode.cn/problems/number-of-subarrays-with-gcd-equal-to-k/">link</a>
 *
 * @author engure
 */
public class SubarrayGCD2447 {

    // 对于区间 [i, j]
    // if i==j: gcd=arr[i]
    // else: gcd=gcd(nums[i],nums[i+1],..,nums[j])

    // 如何求多个数的最小公倍数? 利用“传递性”
    // gcd = BigInteger.ZERO  // java.math.BigInteger
    // for item in arr:
    //    gcd = gcd.gcd(i)

    /**
     * 暴力 + 辗转相除gcd
     *
     * @param nums
     * @param k
     * @return
     */
    public int subarrayGCD(int[] nums, int k) {
        int ans = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] % k == 0) {
                int t = 0;
                for (int j = i; j < nums.length; j++) {
                    t = gcd(t, nums[j]);
                    if (t == k) ans++;
                }
            }
        }
        return ans;
    }

    /**
     * Greatest Common Divisor，即最大公因数，同时整除 n1 和 n2 的最大因子
     * 满足 n1 % i == 0 && n2 % i == 0 且 i 最大
     * <p>
     * 如果 gcd(n1, n2) = 1，那么我们称 n1 和 n2 互素（其中 n1,n2 >= 2 ）
     * <p>
     * 辗转相除法 gcd(m,n) = gcd(n,m%n), m>=n
     * 复杂度 O(logN)
     *
     * @param n1
     * @param n2
     * @return
     */
    public static int gcd(int n1, int n2) {
        //n1>=n2
        int i = Math.max(n1, n2);
        n2 = n1 + n2 - i;
        n1 = i;

        if (n2 == 0 || n1 == n2) {
            return n1;
        }

        while ((i = n1 % n2) != 0) {
            n1 = n2;
            n2 = i;
        }
        return n2;
    }
}
