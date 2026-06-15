package im.engure.probs.p912;

/**
 * 笔试题。
 * 给出一个数组，从左到右，对于每个元素可选可不选，过程中需要保证所有选择数字的和为非负值。
 * 请返回最多能选取多少个数
 * <p>
 * - 暴力递归，可以解出题解
 * - 备忘录，需要使用二维数组。（看暴力递归的动态参数呀！！给忘了 ┭┮﹏┭┮）
 * - 动态规划。二维数组。
 */

public class SelectMoreNum {

    static Integer[] notes;

    public static void main(String[] args) {
        System.out.println(solve(new int[]{4, -4, 1, -3, 1, -3}));
        System.out.println(solve(new int[]{0, 0, 0, 0}));
    }

    public static int solve(int[] arr) {
        notes = new Integer[arr.length];
        int res = process(0, arr, 0);
//        System.out.println("---> " + Arrays.toString(notes));?
        return res;
    }

    /**
     * 从左到右，在 i 位置及之前位置的值和为非负，那么 i 不一定被选中
     * <p>
     * 问题：
     * - 向下找不是最优情况导致备忘录过早被赋值
     */
    public static int process(int index, int[] arr, int now) {
        if (index >= arr.length) return 0;

        int n1 = 0;
        if (now + arr[index] >= 0) //不一定选
            n1 = process(index + 1, arr, now + arr[index]) + 1;

        int n2 = process(index + 1, arr, now);

        return Math.max(n1, n2);
    }

    /*
     * 贪心思路。
     *  - 累加所有正数。将所有负数按从小到大排序。
     *  - 按从大到小遍历并加入负数。
     *
     *  - 合理吗？
     *  - 比如 【4 -5 1 2 3】
     */


}




/*
6
4 -4 1 -3 1 -3

5

--------

5
1 2 3 4 5

5
 */
