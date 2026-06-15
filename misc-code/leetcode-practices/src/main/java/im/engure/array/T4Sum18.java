package im.engure.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class T4Sum18 {
    public static void main(String[] args) {
        int[] arr = {1, -2, -5, -4, -3, 3, 3, 5};
        int target = -11;
        System.out.println(new Solution().fourSum(arr, target));
    }
}


class Solution {
    public List<List<Integer>> fourSum(int[] nums, int target) {

        List<List<Integer>> res = new ArrayList<>();
        Arrays.sort(nums);
        int LEN = nums.length;
        if (LEN < 4) return res;

        // -5,-4,-3,-2,1,3,3,5
        // -11

        for (int i = 0; i < LEN; i++) {
            for (int j = i + 1; j < LEN; j++) {
                int l = j + 1, r = LEN - 1;

                while (l < r) {
                    int sum = nums[i] + nums[j] + nums[l] + nums[r];

                    if (sum == target) {
                        res.add(Arrays.asList(nums[i], nums[j], nums[l], nums[r]));
                        while (l < LEN - 1 && nums[l] == nums[l + 1]) l++;
                        while (r > 0 && nums[r] == nums[r - 1]) r--;
                        r--;
                        l++;
                    } else if (sum > target) {
                        while (r > 0 && nums[r] == nums[r - 1]) r--;
                        r--;
                    } else {
                        while (l < LEN - 1 && nums[l] == nums[l + 1]) l++;
                        l++;
                    }
                }

                while (j < LEN - 1 && nums[j] == nums[j + 1]) j++;
            }
            while (i < LEN - 1 && nums[i] == nums[i + 1]) i++;
        }
        return res;
    }
}
