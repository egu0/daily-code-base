package im.engure.greedy;

import im.engure.util.MyAssertions;

public class MinElements1785 {

    public static void main(String[] args) {
        MinElements1785 o = new MinElements1785();
        MyAssertions.assertEqual(o.minElements(new int[]{1, -1, 1}, 3, -4), 2);
        MyAssertions.assertEqual(o.minElements(new int[]{1, -10, 9, 1}, 100, 0), 1);
    }

    /**
     * 1ms
     */
    public int minElements(int[] nums, int limit, int goal) {
        long sum = 0;
        for (int n : nums) {
            sum += n;
        }

        return (int) ((Math.abs(sum - goal) + limit - 1) / limit);
    }

    /**
     * 2ms
     */
    public int minElements1(int[] nums, int limit, int goal) {
        // abs(goal) <= 10^9, limit >= 1
        long sum = 0;
        for (int n : nums) {
            sum += n;
        }

        if (sum == goal) {
            return 0;
        } else {
            long val = Math.abs(sum - goal);
            return (int) (val % limit == 0 ? val / limit : val / limit + 1);
        }
    }
}
