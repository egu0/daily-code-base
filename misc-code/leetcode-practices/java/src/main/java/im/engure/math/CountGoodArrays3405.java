package im.engure.math;

import java.util.Stack;

/*
n=4, m=2, k=2
expected=6
[1, 1, 1, 2],
[1, 1, 2, 2],
[1, 2, 2, 2],
[2, 1, 1, 1],
[2, 2, 1, 1],
[2, 2, 2, 1],
 */
public class CountGoodArrays3405 {
    private static final int MOD = 1_000_000_007;
    private static final int MX = 100_000;
    private static final long[] fac = new long[MX]; // fac[i] = i!
    private static final long[] invF = new long[MX]; // invF[i] = i!^-1

    static {
        fac[0] = 1;
        for (int i = 1; i < MX; i++) {
            fac[i] = fac[i - 1] * i % MOD;
        }

        invF[MX - 1] = pow(fac[MX - 1], MOD - 2);
        for (int i = MX - 1; i > 0; i--) {
            invF[i - 1] = invF[i] * i % MOD;
        }
    }

    Stack<Integer> stack = new Stack<>();

    private static long pow(long x, int n) {
        long res = 1;
        for (; n > 0; n /= 2) {
            if (n % 2 > 0) {
                res = res * x % MOD;
            }
            x = x * x % MOD;
        }
        return res;
    }

    // 排列组合方法
    // 参考：https://leetcode.cn/problems/count-the-number-of-arrays-with-k-matching-adjacent-elements/solutions/3033292/chun-shu-xue-ti-pythonjavacgo-by-endless-mxj7/
    public int countGoodArrays(int n, int m, int k) {
        return (int) (comb(n - 1, k) * m % MOD * pow(m - 1, n - k - 1) % MOD);
    }

    private long comb(int n, int m) {
        return fac[n] * invF[m] % MOD * invF[n - m] % MOD;
    }

    // region brute-force

    // 暴力递归
    // 时间复杂度：O(M^N)
    public int countGoodArraysV1(int n, int m, int k) {
        return processV1(n, m, k, -1, 1, 0);
    }

    int processV1(int n, int m, int k, int last, int idx, int equalNum) {
        // 剪枝
        if (equalNum > k) {
            return -1;
        }

        if (idx > n) {
            System.out.printf("%-10d %s %s\n", idx, stack, equalNum == k ? "hit" : "");
            return equalNum == k ? 1 : -1;
        }

        int sum = 0;
        for (int i = 1; i <= m; i++) {
            int res;
            if (last != i) {
                stack.push(i);
                res = processV1(n, m, k, i, idx + 1, equalNum);
                stack.pop();
            } else {
                stack.push(i);
                res = processV1(n, m, k, i, idx + 1, equalNum + 1);
                stack.pop();
            }
            if (res != -1) {
                sum += res;
            }
        }

        return sum;
    }

    // endregion

}
