package im.engure.dp;

/**
 * 多米诺和托米诺平铺
 * <p>
 * 状态转移方程：f(n) = 2f(n-1) + f(n-3)
 * <p>
 * 公式推导：<a href="https://leetcode.cn/problems/domino-and-tromino-tiling/solution/by-endlesscheng-umpp/">...</a>
 *
 * @author engure
 */
public class NumTilings790 {

    private static final long MOD = (long) 1e9 + 7;
    private static final long[] f = new long[1001];

    static {
        f[0] = f[1] = 1;
        f[2] = 2;
        for (int i = 3; i <= 1000; ++i) {
            f[i] = (f[i - 1] * 2 + f[i - 3]) % MOD;
        }
    }

    public int numTilings(int n) {
        return (int) f[n];
    }
}
