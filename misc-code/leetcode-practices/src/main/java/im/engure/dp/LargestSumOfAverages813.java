package im.engure.dp;

import java.util.HashMap;
import java.util.Map;

/**
 * @author engure
 */
public class LargestSumOfAverages813 {
    public static void main(String[] args) {
        System.out.println(new LargestSumOfAverages813().largestSumOfAverages(new int[]{1, 2, 3, 4, 5, 6, 7}, 4));
        System.out.println(new LargestSumOfAverages813().largestSumOfAverages1(new int[]{1, 2, 3, 4, 5, 6, 7}, 4));
        System.out.println(new LargestSumOfAverages813().largestSumOfAverages(new int[]{4663, 3020, 7789, 1627, 9668, 1356, 4207, 1133, 8765, 4649, 205, 6455, 8864, 3554, 3916, 5925, 3995, 4540, 3487, 5444, 8259, 8802, 6777, 7306, 989, 4958, 2921, 8155, 4922, 2469, 6923, 776, 9777, 1796, 708, 786, 3158, 7369, 8715, 2136, 2510, 3739, 6411, 7996, 6211, 8282, 4805, 236, 1489, 7698}, 5));
        System.out.println(new LargestSumOfAverages813().largestSumOfAverages1(new int[]{4663, 3020, 7789, 1627, 9668, 1356, 4207, 1133, 8765, 4649, 205, 6455, 8864, 3554, 3916, 5925, 3995, 4540, 3487, 5444, 8259, 8802, 6777, 7306, 989, 4958, 2921, 8155, 4922, 2469, 6923, 776, 9777, 1796, 708, 786, 3158, 7369, 8715, 2136, 2510, 3739, 6411, 7996, 6211, 8282, 4805, 236, 1489, 7698}, 5));
    }

    /**
     * 时间 O(K*N*N), 时间 O(N)
     *
     * @param nums
     * @param k
     * @return
     */
    public double largestSumOfAverages2(int[] nums, int k) {
        double[] dp0 = new double[nums.length + 1], dp1 = new double[nums.length + 1], ptr;
        int sum = 0;
        for (int j = nums.length - 1; j >= 0; j--) {
            sum += nums[j];
            dp0[j] = sum / (double) (nums.length - j);
        }
        for (int i = 2; i <= k; i++) {
            for (int j = nums.length - 1; j >= 0; j--) {
                double max = 0, tmp;
                int cnt = 0;
                for (int m = j; m < nums.length; m++) {
                    cnt += nums[m];
                    tmp = dp0[m + 1] + cnt / (double) (m - j + 1);
                    if (tmp > max) {
                        max = tmp;
                    }
                }
                dp1[j] = max;
            }
            ptr = dp0;
            dp0 = dp1;
            dp1 = ptr;
        }
        return dp0[0];
    }

    /**
     * dp 法，根据备忘录法【变形】而来。
     * 时间 O(K*N*N), 空间 O(N*K)
     * 在 fori 循环中，每次都依赖 i-1 这一列，因此可进一步将空间复杂度降为 O(N)
     *
     * @param nums
     * @param k
     * @return
     */
    public double largestSumOfAverages(int[] nums, int k) {

        double[][] dp = new double[nums.length + 1][k + 1];

        int sum = 0;
        for (int j = nums.length - 1; j >= 0; j--) {
            sum += nums[j];
            dp[j][1] = sum / (double) (nums.length - j);
        }

        for (int i = 2; i <= k; i++) {
            for (int j = nums.length - 1; j >= 0; j--) {
                double max = 0, tmp;
                int cnt = 0;
                for (int m = j; m < nums.length; m++) {
                    cnt += nums[m];
                    tmp = dp[m + 1][i - 1] + cnt / (double) (m - j + 1);
                    if (tmp > max) {
                        max = tmp;
                    }
                }
                dp[j][i] = max;
            }
        }

        return dp[0][k];
    }

    /**
     * recursion + memorization, 93ms
     *
     * @param nums
     * @param k
     * @return
     */
    public double largestSumOfAverages1(int[] nums, int k) {
        return solve(nums, 0, k);
    }

    Map<String, Double> note = new HashMap<>();

    public double solve(int[] arr, int i, int k) {
        if (i >= arr.length) {
            return 0;
        }

        String key = i + ":" + k;
        if (note.containsKey(key)) {
            return note.get(key);
        }

        if (k == 1) {
            int sum = 0;
            for (int j = i; j < arr.length; j++) {
                sum += arr[j];
            }
            double res = sum / (double) (arr.length - i);
            note.put(key, res);
            return res;
        }

        double max = 0, tmp;
        int cnt = 0;
        for (int j = i; j < arr.length; j++) {
            cnt += arr[j];
            tmp = solve(arr, j + 1, k - 1) + cnt / (double) (j - i + 1);
            if (tmp > max) {
                max = tmp;
            }
        }
        note.put(key, max);
        return max;
    }

    /**
     * 理解错题意：最多分成 k 个子数组，而非子数组中最多有 k 个数字
     * 常规的 recursion、memorization、dp
     *
     * @param nums
     * @param k
     * @return
     */
    public double largestSumOfAverages0(int[] nums, int k) {
        return process(nums, 0, k);
    }

    public double process(int[] arr, int i, int k) {
        if (i >= arr.length) {
            return 0;
        }
        double max = 0, tmp = 0;
        int cnt = 0;
        for (int j = 1; j <= k; j++) {
            if (i + j - 1 >= arr.length) {
                break;
            }
            cnt += arr[i + j - 1];
            tmp = process(arr, i + j, k) + cnt / (double) j;
            if (max < tmp) {
                max = tmp;
            }
        }
        if (max < tmp) {
            max = tmp;
        }
        return max;
    }
}
