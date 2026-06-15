package im.engure.busipro;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MaximumScore1753 {

    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        System.out.println(new MaximumScore1753().maximumScore4(581, 102, 494));
        System.out.println(new MaximumScore1753().maximumScore4(1079, 597, 575));
        System.out.println(new MaximumScore1753().maximumScore4(2, 4, 6));
        long t2 = System.currentTimeMillis();
        System.out.println("ms: " + (t2 - t1));
    }

    /**
     * 找规律
     */
    public int maximumScore(int a, int b, int c) {
        if (a + b < c) return a + b;
        if (a + c < b) return a + c;
        if (c + b < a) return b + c;
        return (a + b + c) / 2;
    }

    /**
     * 排序法
     */
    public int maximumScore5(int a, int b, int c) {
        int[] nums = new int[]{a, b, c};
        Arrays.sort(nums);
        int res = 0;
        while (nums[1] > 0) {
            nums[2]--;
            nums[1]--;
            res++;
            Arrays.sort(nums);
        }
        return res;
    }

    /**
     * dp，进一步优化空间为 O(a*b)，【【超时】】
     *
     * @param a
     * @param b
     * @param c
     * @return
     */
    public int maximumScore4(int a, int b, int c) {
        c++;
        int[][] dp = new int[++a][++b];
        int[][] tmp = new int[a][b];
        int[][] ptr;
        for (int i = 1; i < a; i++) {
            for (int j = 1; j < b; j++) {
                dp[i][j] = 1 + dp[i - 1][j - 1];
            }
        }
        for (int k = 1; k < c; k++) {
            for (int i = 1; i < a; i++) {
                tmp[i][0] = 1 + dp[i - 1][0];
            }
            for (int j = 1; j < b; j++) {
                tmp[0][j] = 1 + dp[0][j - 1];
            }
            for (int i = 1; i < a; i++) {
                for (int j = 1; j < b; j++) {
                    tmp[i][j] = 1 + Math.max(tmp[i - 1][j - 1], Math.max(dp[i][j - 1], dp[i - 1][j]));
                }
            }
            ptr = dp;
            dp = tmp;
            tmp = ptr;
        }
        return dp[a - 1][b - 1];
    }

    /**
     * dp[a][b][c] - 【【内存超限】】
     * 空间：O(a*b*c)
     *
     * @param a
     * @param b
     * @param c
     * @return
     */
    public int maximumScore3(int a, int b, int c) {
        a++;
        b++;
        c++;
        int[][][] dp = new int[a][b][c];
        // case - 1, three planes
        for (int i = 1; i < a; i++) {
            for (int j = 1; j < b; j++) {
                dp[i][j][0] = 1 + dp[i - 1][j - 1][0];
            }
        }
        for (int i = 1; i < b; i++) {
            for (int j = 1; j < c; j++) {
                dp[0][i][j] = 1 + dp[0][i - 1][j - 1];
            }
        }
        for (int i = 1; i < a; i++) {
            for (int j = 1; j < c; j++) {
                dp[i][0][j] = 1 + dp[i - 1][0][j - 1];
            }
        }
        // case - 2, 3d space
        for (int i = 1; i < a; i++) {
            for (int j = 1; j < b; j++) {
                for (int k = 1; k < c; k++) {
                    dp[i][j][k] = 1 + Math.max(dp[i - 1][j - 1][k], Math.max(dp[i][j - 1][k - 1], dp[i - 1][j][k - 1]));
                }
            }
        }
        return dp[a - 1][b - 1][c - 1];
    }

    Map<String, Integer> note = new HashMap<>();

    /**
     * memorization，【【超时】】
     *
     * @param a
     * @param b
     * @param c
     * @return
     */
    public int maximumScore2(int a, int b, int c) {
        // 存在两个或三个为空
        if (a + b == 0 || a + c == 0 || b + c == 0) {
            return 0;
        }
        String key = a + ":" + b + ":" + c;
        if (note.containsKey(key)) return note.get(key);
        int n = 0;
        // 当且只有一个为 0
        if (a == 0 || b == 0 || c == 0) {
            n = (a == 0) ? maximumScore(0, b - 1, c - 1) : (b == 0 ? maximumScore(a - 1, 0, c - 1) : maximumScore(a - 1, b - 1, 0));
            note.put(key, n + 1);
            return n + 1;
        }
        // 都不为 0
        int max = Math.max(Math.max(maximumScore(a - 1, b - 1, c), maximumScore(a - 1, b, c - 1)), maximumScore(a, b - 1, c - 1));
        note.put(key, max + 1);
        return max + 1;
    }
}
