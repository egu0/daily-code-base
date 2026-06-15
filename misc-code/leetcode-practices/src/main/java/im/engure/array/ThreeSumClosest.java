package im.engure.array;

import java.util.Arrays;

/**
 * 与target最接近的三数之和
 */

public class ThreeSumClosest {
    public static void main(String[] args) {
        /*
            [-55,-24,-18,-11,-7,-3,4,5,6,9,11,23,33]
            0
         */
        int[] arr = {-55, -24, -18, -11, -7, -3, 4, 5, 6, 9, 11, 23, 33};
        int target = 0;
        System.out.println(new ThreeSumClosest().threeSumClosest1(arr, target));
        System.out.println(new ThreeSumClosest().threeSumClosest2(arr, target));
        System.out.println(new ThreeSumClosest().threeSumClosest3(arr, target));
    }

    /*

        [-55, -24, -18, -11, -7, -3, 4, 5, 6, 9, 11, 23, 33]
        0

     */

    /**
     * https://leetcode-cn.com/problems/3sum-closest/solution/java-dai-ma-jian-ji-si-lu-qing-xi-by-ven-cfhb/
     *
     * @param nums
     * @param target
     * @return
     */
    public int threeSumClosest3(int[] nums, int target) {

        Arrays.sort(nums);
        int res = Integer.MAX_VALUE - target;

        for (int i = 0; i < nums.length - 2; i++) {

            int l = i + 1, r = nums.length - 1;

            while (l < r) {
                int sum = nums[i] + nums[l] + nums[r];
                if (sum == target) {
                    return sum;
                } else if (sum > target) {
                    if (Math.abs(sum - target) < Math.abs(res - target))
                        res = sum;
                    r--;
                } else {
                    if (Math.abs(sum - target) < Math.abs(res - target))
                        res = sum;
                    l++;
                }
            }

        }
        return res;
    }

    /**
     * 思路不正确：
     * 1. 计算每一组的偏移量，区间是 [left, right]
     *
     * @param nums
     * @param target
     * @return
     */
    public int threeSumClosest2(int[] nums, int target) {

        int temp = getValue(nums, 0, 1, 2);
        Arrays.sort(nums);//对数组排序
        /*System.out.println(Arrays.toString(nums));*/

        int left = 0, right = nums.length - 1;
        while (left < right) {

            if (left + 1 == right) break;

            //这一组的最小偏移量
            int temp_off_of_left_right = getValue(nums, left, left + 1, right);

            for (int i = left + 2; i <= right - 1; i++) {
                int v = getValue(nums, left, i, right);
                if (v == target) return v;
                int off_i = Math.abs(target - v);
                int off_temp = Math.abs(target - temp_off_of_left_right);
                if (off_i <= off_temp) temp_off_of_left_right = v;
            }

            /*System.out.println(Arrays.toString(Arrays.copyOfRange(nums, left, right + 1)) +
                    ", temp of this group = " + temp_off_of_left_right +
                    ", temp = " + temp);*/

            if (temp_off_of_left_right < target) left++;
            else right--;

            int off_this = Math.abs(target - temp_off_of_left_right);
            int off_temp = Math.abs(target - temp);
            if (off_this <= off_temp) temp = temp_off_of_left_right;

        }

        return temp;
    }

    /**
     * O(N^3)，暴力解法不推荐
     */
    public int threeSumClosest1(int[] nums, int target) {
        int temp = getValue(nums, 0, 1, 2);
        for (int i = 0; i < nums.length - 2; i++) {
            for (int j = i + 1; j < nums.length - 1; j++) {
                for (int k = j + 1; k < nums.length; k++) {
                    int v = getValue(nums, i, j, k);
                    if (v == target) return v;
                    int off1 = Math.abs(target - temp);
                    int off2 = Math.abs(target - v);
                    if (off2 < off1) temp = v;
                }
            }
        }
        return temp;
    }

    public int getValue(int[] nums, int i, int j, int k) {
        return nums[i] + nums[j] + nums[k];
    }
}
