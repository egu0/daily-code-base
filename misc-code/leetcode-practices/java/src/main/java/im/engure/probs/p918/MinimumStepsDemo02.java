package im.engure.probs.p918;

//  A,B,,,,Z     26个大写字母构成了一个键盘
// 输入一段字符串（都是大写字母），通过移动移动光标将他打印出来。
// 其中包括：
//  - 左移或右移。右移比如A->B，左移比如A->Z，每次只能移动一个距离。
//  - 确认键。打印出光标所在的字母。
//  - 魔法键。使用一次可以移动任意长度，但必须连着使用魔法键m次。
// 问最少要按键或移动多少次才可以打印出字符串。

//魔法键。要没事用 m 次，要么连续使用 0 次。
// [index ... N-1]上
//     1. 在 index 上使用魔法键
//     2. 不在 index 上使用魔法键

public class MinimumStepsDemo02 {
    static char[] chs;//all capital
    static int m;

    public static void main(String[] args) {

        //简化操作，scanner输入

        chs = "ADDA".toCharArray();
        m = 2;//m >= 0

        System.out.println(process1(0, -1) + chs.length);

    }


    // 第二版

    // index... 上还有 rest 步。此时 index 位置至少需要多少步。

    //  len = chs.length
    //  m
    //  dp[len][m+1]，rest可能取值 -1，0，1，2，，，

    // dp[i][j], from dp[len][m]
    //      = dp[i+1][j-1] + 1                                       j>0
    //      = dp[i+1][0] + a2b(i,i+1)                                j=0
    //      = min(dp[i+1][-1] + a2b(i,i+1), dp[i+1][m-1] + 1)        j=-1

    // rest：-1表示未使用，>=0表示使用中
    static int process1(int index, int rest) {

        if (index == chs.length - 1) {
            return rest == -1 ? 0 : rest;
        }

        int steps = a2b(chs[index], chs[index + 1]);

        if (rest > 0) {
            return process1(index + 1, --rest) + 1;
        } else if (rest == 0) {
            return process1(index + 1, 0) + steps;
        } else {
            int n1 = process1(index + 1, -1) + steps;//not use
            int n2 = process1(index + 1, m - 1) + 1;//start use
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








