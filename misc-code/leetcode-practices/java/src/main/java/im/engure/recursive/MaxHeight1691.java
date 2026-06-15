package im.engure.recursive;

import im.engure.util.MyAssertions;

import java.util.Arrays;
import java.util.Comparator;
import java.util.OptionalInt;

/**
 * @author Engure
 */
public class MaxHeight1691 {

    public static void main(String[] args) {
        MaxHeight1691 o = new MaxHeight1691();
        MyAssertions.assertEqual(o.maxHeight(new int[][]{{50, 45, 20}, {95, 37, 53}, {45, 23, 12}}), 190);
        MyAssertions.assertEqual(o.maxHeight(new int[][]{{38, 25, 45}, {76, 35, 3}}), 76);
        MyAssertions.assertEqual(o.maxHeight(new int[][]{{7, 11, 17}, {7, 17, 11}, {11, 7, 17}, {11, 17, 7}, {17, 7, 11}, {17, 11, 7}}), 102);
    }

    /**
     * 暴力法 O(N^2)
     *
     * @param cuboids 长方体尺寸信息
     */
    public int maxHeight(int[][] cuboids) {
        // 方块内长宽高小到大旋转，方块间按长宽高和小到大排列
        for (int[] cuboid : cuboids) {
            Arrays.sort(cuboid);
        }

        Arrays.sort(cuboids, Comparator.comparingInt(c -> Arrays.stream(c).sum()));
        //等价于
        //Arrays.sort(cuboids, (o1, o2) -> Arrays.stream(o1).sum() - Arrays.stream(o2).sum());

        int len = cuboids.length;
        int[] dp = new int[len];

        // 计算第 i 个方块放在最下面时的最大高度
        for (int i = 0; i < len; i++) {
            dp[i] = cuboids[i][2];
            for (int j = 0; j < i; j++) {
                if (cuboids[i][0] >= cuboids[j][0]
                        && cuboids[i][1] >= cuboids[j][1]
                        && cuboids[i][2] >= cuboids[j][2]
                        && dp[j] + cuboids[i][2] > dp[i]) {
                    // dp[i] = max(dp[j]) + cuboids[i][2]
                    dp[i] = dp[j] + cuboids[i][2];
                }
            }
        }

        OptionalInt op = Arrays.stream(dp).max();
        if (op.isPresent()) {
            return op.getAsInt();
        } else {
            throw new RuntimeException();
        }
    }
}
