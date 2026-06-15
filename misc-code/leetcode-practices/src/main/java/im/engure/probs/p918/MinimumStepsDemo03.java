package im.engure.probs.p918;

// 第三版

public class MinimumStepsDemo03 {

    static char[] chs;//all capital
    static int m;

    //
    // 改进：dp[len][m+1]，使用 j==m 表示未使用。
    //
    // dp[i][j]，when i < len-1
    //      = dp[i+1][0] + a2b(i,i+1)                                j=0
    //      = dp[i+1][j-1] + 1                                       0<j<m
    //      = min(dp[i+1][m] + a2b(i,i+1), dp[i+1][m-1] + 1)         j=m

    static int solve() {
        int len = chs.length;
        int[][] res = new int[len][m + 1];// [0....len-1][0....m]

        //    0 1 2 3 4 5 6
        //  0
        //  1
        //  2
        //  3 

        for (int i = len - 2; i >= 0; i--) {
            for (int j = m; j >= 0; j--) {

            }
        }

        return 1;
    }


    static int process2(int index, int rest) {

        if (index == chs.length - 1) {
            return rest == m ? 0 : rest;
        }

        int steps = a2b(chs[index], chs[index + 1]);

        if (rest > 0) {
            return process2(index + 1, --rest) + 1;
        } else if (rest == 0) {
            return process2(index + 1, 0) + steps;
        } else {
            int n1 = process2(index + 1, m) + steps;//not use
            int n2 = process2(index + 1, m - 1) + 1;//start use
            return Math.min(n1, n2);
        }
    }

    //将 a 移动到 b 至少需要多少步
    static int a2b(char a, char b) {
        int off1 = Math.abs(b - a);
        int off2 = Math.abs(a + 26 - b);
        return Math.min(off1, off2);
    }

}
