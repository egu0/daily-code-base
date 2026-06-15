package im.engure.array;

public class FirstMissingPositive41 {
    /**
     * 遍历一次数组把大于等于1的和小于数组大小的值放到原数组对应位置，然后再遍历一次数组查当前下标是否和值对应，
     * 如果不对应那这个下标就是答案，否则遍历完都没出现那么答案就是数组长度加1。
     */
    public int firstMissingPositive(int[] nums) {
        boolean[] arr = new boolean[nums.length + 2];
        for (int num : nums) {
            if (num > 0 && num <= nums.length) {
                arr[num] = true;
            }
        }
        for (int i = 1; i <= nums.length + 1; i++) {
            if (!arr[i]) {
                return i;
            }
        }
        return 0;
    }
}
