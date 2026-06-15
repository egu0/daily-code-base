package im.engure.array;

/*
输入：nums = [2,1,4,3], left = 2, right = 3
输出：3
解释：满足条件的三个子数组：[2], [2, 1], [3]
 */
public class NumSubarrayBoundedMax795 {
    public static void main(String[] args) {
//        System.out.println(new NumSubarrayBoundedMax795().numSubarrayBoundedMax(new int[]{876, 880, 482, 260, 132, 421, 732, 703, 795, 420, 871, 445, 400, 291, 358, 589, 617, 202, 755, 810, 227, 813, 549, 791, 418, 528, 835, 401, 526, 584, 873, 662, 13, 314, 988, 101, 299, 816, 833, 224, 160, 852, 179, 769, 646, 558, 661, 808, 651, 982, 878, 918, 406, 551, 467, 87, 139, 387, 16, 531, 307, 389, 939, 551, 613, 36, 528, 460, 404, 314, 66, 111, 458, 531, 944, 461, 951, 419, 82, 896, 467, 353, 704, 905, 705, 760, 61, 422, 395, 298, 127, 516, 153, 299, 801, 341, 668, 598, 98, 241},
//                658, 719));
//        System.out.println(new NumSubarrayBoundedMax795().numSubarrayBoundedMax(new int[]{73, 55, 36, 5, 55, 14, 9, 7, 72, 52}, 32, 69));
//        System.out.println(new NumSubarrayBoundedMax795().numSubarrayBoundedMax(new int[]{5, 1, 2, 1, 1}, 2, 3));
        System.out.println(new NumSubarrayBoundedMax795().numSubarrayBoundedMax(new int[]{409, 96, 729, 843, 328, 855}
                , 542, 772));

    }

    /**
     * 设 f(x) 表示最大元素在 x 范围内的子数组个数，那么满足 f(left, right) = f(-∞, right) - f(-∞, left)
     * 因此可以转换为求"最大元素不超过 e 的子数组个数"
     *
     * @param nums
     * @param left
     * @param right
     * @return
     */
    public int numSubarrayBoundedMax(int[] nums, int left, int right) {
        return f(nums, right) - f(nums, left - 1);
    }

    private int f(int[] nums, int x) {
        int cnt = 0, t = 0;
        for (int v : nums) {
            t = v > x ? 0 : t + 1;
            cnt += t;
        }
        return cnt;
    }

    /*
     4ms: 从后往前遍历 nums 依次计算 nums[i] 作为子数组首个元素时子数组的个数，最终进行累加
     */
    public int numSubarrayBoundedMax2(int[] nums, int left, int right) {
        int len = nums.length;
        int[] dp = new int[len];
        //从后往前计算 i 位置上的数字有 dp[i] 种组合
        for (int i = nums.length - 1; i >= 0; i--) {
            if (left <= nums[i] && nums[i] <= right) {
                if (i + 1 < nums.length) {
                    if (nums[i + 1] < left) {
                        dp[i] = 2;
                    } else if (left <= nums[i + 1] && nums[i + 1] <= right) {
                        dp[i] = dp[i + 1] + 1;
                    } else if (nums[i + 1] > right) {
                        dp[i] = 1;
                    }
                } else {
                    dp[i] = 1;
                }
            } else if (nums[i] < left) {
                //如果遇到 <left 的数字，则向前找，直到找到 >=left 的数字
                int j = i - 1;
                while (j >= 0) {
                    if (left <= nums[j]) {
                        break;
                    }
                    j--;
                }
                if (j >= 0) {//找到 >=left 的数字
                    if (left <= nums[j] && nums[j] <= right) {
                        if (i + 1 < nums.length) {
                            if (left <= nums[i + 1] && nums[i + 1] <= right) {
                                dp[j] = i - j + 1 + dp[i + 1];
                                for (int k = i; k >= j + 1; k--) {
                                    dp[k] = dp[k + 1];
                                }
                            } else {
                                dp[j] = i - j + 1;
                            }
                        } else {
                            dp[j] = i - j + 1;
                        }
                    } else {
                        if (i + 1 < nums.length) {
                            if (left <= nums[i + 1] && nums[i + 1] <= right) {
                                for (int k = i; k >= j + 1; k--) {
                                    dp[k] = dp[k + 1];
                                }
                            }
                        }
                    }
                } else { // 没找到就结束了，则需要计算 [0, i] 区间上的"升序"组合数
                    for (int k = i; k >= 0; k--) {
                        dp[k] = dp[k + 1];
                    }
                }
                i = j;
            }
        }
        //output
        int ans = 0;
        for (int num : dp) {
            ans += num;
        }
        return ans;
    }
}
