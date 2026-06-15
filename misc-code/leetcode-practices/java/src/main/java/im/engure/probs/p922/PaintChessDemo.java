package im.engure.probs.p922;


/*

给一个 n * m 的矩阵，对其进行上色。要求相邻的格子颜色不能相同。
'.' 表示为上色。可以使用红色或白色上色。
'R' 表示为白色，'W' 表示为红色。

给出的矩阵中可能已经有上色的格子。

如果可以对矩阵进行上色，返回一个 一维字符串数组。第一个元素为 'YES'，后边元素为矩阵。
如果不可以，同样返回字符串数组，第一个元素为 "NO";

.R...
.....
.....
.W...

YES,
WRWRW
RWRWR
WRWRW
RWRWR

 */

public class PaintChessDemo {

    public static void main(String[] args) {
        String[] ss = {".R...", ".....", ".....", ".W..."};
        int m = ss.length, n = ss[0].length();
        char[][] chs1 = new char[m][n];

        for (int i = 0; i < ss.length; i++) {
            for (int j = 0; j < ss[i].length(); j++) {
                chs1[i][j] = ss[i].charAt(j);
            }
        }

        for (int i = 0; i < chs1.length; i++) {
            System.out.println(new String(chs1[i]));
        }

        System.out.println("------------");

        //chs1[0][0] = 'W';
        chs1[0][0] = 'R';
        boolean res1 = process(chs1);
        if (res1) {
            for (int i = 0; i < chs1.length; i++) {
                System.out.println(new String(chs1[i]));
            }
        } else {
            System.out.println("failed");
        }

    }

    /*
    常规思路。
     */
    public static String[] solve(String[] chess) {
        return null;
    }

    // 9.22 向 下、左、右 三个方向找，考虑掉头死循环。
    // 9.23 因为只涉及两种颜色，重新考虑从 0,0 开始即可，因此只用向下、向右找
    public static boolean process(int i, int j, char[][] chs) {
        return true;
    }

    static int row;
    static int col;

    // 从 0,0 开始向下、向右找
    // 前提是 0,0 位置没有上色。
    public static boolean process(char[][] chs) {

        row = chs.length;
        col = chs[0].length;

        for (int k = 1; k < col; k++) {
            if (chs[0][k] != '.' && chs[0][k] == chs[0][k - 1]) return false;
            else chs[0][k] = (chs[0][k - 1] == 'R' ? 'W' : 'R');
        }

        for (int k = 1; k < row; k++) {
            if (chs[k][0] != '.' && chs[k][0] == chs[k - 1][0]) return false;
            else chs[k][0] = (chs[k - 1][0] == 'R' ? 'W' : 'R');
        }

        for (int R = 1; R < row; R++) {
            for (int C = 1; C < col; C++) {
                if (chs[R - 1][C] != chs[R][C - 1]) return false;
                else chs[R][C] = (chs[R - 1][C] == 'W' ? 'R' : 'W');
            }
        }

        return true;
    }


    /*

    假设 0,0 已上色，此时是否可以从 0,0 开始？
        W....
        .....
        .....
        .W...
    可以。因为最终棋盘要么是R要么是W，如果初始棋盘没有上色那么才有两种方案。
    其余的情况最多只有一种涂色方案。

    如果有三种或三种以上的颜色，该怎么解决？
    “给出一个棋盘进行上色，要求相邻格子颜色不能相同，一共有三种颜色，一些格子已经上色，计算所有的涂色方案。”

    PaintChessDemo02

     */

}
