package im.engure.array;

public class SumOfGoodNumbers3452 {
    public int sumOfGoodNumbers(int[] nums, int k) {
        int n = nums.length, sum = 0;
        for (int i = 0; i < n; i++) {
            int ik1 = i - k;
            int ik2 = i + k;
            int v1 = (ik1 < 0 || ik1 >= n) ? nums[i] - 1 : nums[ik1];
            int v2 = (ik2 < 0 || ik2 >= n) ? nums[i] - 1 : nums[ik2];
            if (nums[i] > v1 && nums[i] > v2) {
                System.out.println("hit at " + i + ", with value " + nums[i]);
                sum += nums[i];
            }
        }
        return sum;
    }
}
