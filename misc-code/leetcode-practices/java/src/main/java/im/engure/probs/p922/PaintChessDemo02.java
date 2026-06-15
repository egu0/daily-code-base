package im.engure.probs.p922;

//“给出一个 m*n 棋盘进行上色，要求相邻格子颜色不能相同，一共有三种颜色，一些格子已经上色，计算所有的涂色方案。”

// R,W,B

// .R...
// .....
// ....B
// .....

// 1931题，“用三种不同颜色为网格涂色”, hard
// https://leetcode-cn.com/problems/painting-a-grid-with-three-different-colors/

public class PaintChessDemo02 {


    static int row;
    static int col;

    // .1...
    // .....
    // 2....

    // 21...
    // .....
    // 2....

    static final char[] colors = {'R', 'W', 'B'};


    // 检索路径。
    //  1. i,j 向外扩散。递归
    //  2. i,j 依赖 <i,<j 的部分。dp


    static boolean process(int i, int j, char[][] chs) {

        return true;
    }

}
