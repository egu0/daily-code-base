package im.engure.math;

import java.math.BigInteger;

/**
 * 6234. 最小公倍数为 K 的子数组数目，<a href="https://leetcode.cn/problems/number-of-subarrays-with-lcm-equal-to-k/">problem addr</a>
 *
 * @author engure
 */
public class SubarrayLCM6234 {
    public static void main(String[] args) {
    }

    public int subarrayLCM(int[] nums, int k) {

        return 0;
    }

    /**
     * least common multiple，即最小公倍数，满足 lcm % n1 == 0 && lcm % n2 == 0 且 lcm 最小
     *
     * @param n1
     * @param n2
     * @return
     */
    public static int lcm(int n1, int n2) {
        // 通过gcd求lcm
        return (n1 * n2) / SubarrayGCD2447.gcd(n1, n2);
    }
}
