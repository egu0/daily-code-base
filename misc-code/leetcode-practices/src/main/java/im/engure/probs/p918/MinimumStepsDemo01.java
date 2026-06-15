package im.engure.probs.p918;

//题目：https://i.loli.net/2021/09/19/Fnx1GuDij6w2YZf.jpg

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


// 第一版

public class MinimumStepsDemo01 {
    static char[] chs;//all capital
    static int m;

    public static void main(String[] args) {

        //简化操作，scanner输入

        chs = "ADDA".toCharArray();
        m = 2;//m >= 0

        System.out.println(process(0, false, 0) + chs.length);
    }

    static int process(int index, boolean using, int rest) {

        if (index == chs.length - 1) {
            return (using && rest > 0) ? rest : 0;
        }

        int steps = a2b(chs[index], chs[index + 1]);

        if (using) {
            //使用了
            if (rest > 0)
                return process(index + 1, true, --rest) + 1;
            else {
                return process(index + 1, true, 0) + steps;
            }
        } else {
            //没使用
            int n1 = process(index + 1, false, 0) + steps;
            int n2 = process(index + 1, true, m - 1) + 1;
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








