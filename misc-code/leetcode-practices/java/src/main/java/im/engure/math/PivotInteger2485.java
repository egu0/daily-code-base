package im.engure.math;

public class PivotInteger2485 {
    // sum( [1,2,,x] ) = sum( [x,x+1,,n] )
    // 1 <= n <= 1000
    public int pivotInteger(int n) {
        if (n == 1) {
            return 1;
        }

        // sum([1, 2, 3,, n-1])
        int leftSum = 0;
        for (int i = 1; i <= n - 1; i++) {
            leftSum += i;
        }

        // sum([n-1, n])
        int rightSum = 2 * n - 1;

        if (rightSum == leftSum) {
            return n - 1;
        }

        // 第二遍
        for (int i = n - 1; i >= 1; i--) {
            leftSum -= i;
            rightSum += i;
            rightSum--;

            if (leftSum < rightSum) {
                break;
            }
            if (rightSum == leftSum) {
                return i - 1;
            }
        }

        return -1;
    }

}
