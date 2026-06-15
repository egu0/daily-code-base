package im.engure.dfsndbfs;

import java.util.Scanner;
import java.util.Stack;

/*
迷宫问题
https://www.nowcoder.com/practice/cf24906056f4488c9ddb132f317e03bc

==============
描述
定义一个二维数组 N*M ，如 5 × 5 数组下所示：

int maze[5][5] = {
0, 1, 0, 0, 0,
0, 1, 1, 1, 0,
0, 0, 0, 0, 0,
0, 1, 1, 1, 0,
0, 0, 0, 1, 0,
};

它表示一个迷宫，其中的1表示墙壁，0表示可以走的路，只能横着走或竖着走，不能斜着走，要求编程序找出从左上角到右下角的路线。
入口点为[0,0],既第一格是可以走的路。

数据范围： 2≤n,m≤10  ， 输入的内容只包含 0≤val≤1

输入描述：
输入两个整数，分别表示二维数组的行数，列数。再输入相应的数组，其中的1表示墙壁，0表示可以走的路。
数据保证有唯一解,不考虑有多解的情况，即迷宫只有一条通道。

输出描述：
左上角到右下角的最短路径，格式如样例所示。
 */
public class HJ43 {
    static int row, col;
    static int[][] matrix;
    public static void main(String[] args) {
        //process input
        Scanner sc = new Scanner(System.in);
        row = sc.nextInt();
        col = sc.nextInt();
        matrix = new int[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                matrix[i][j] = sc.nextInt();
            }
        }
        //process
        processLoc(0, 0, new int[row][col], new Stack<String>());
    }

    /**
     * 当前坐标是否走得通
     * @param i
     * @param j
     * @param visited
     * @param stk
     * @return
     */
    public static boolean processLoc(int i, int j, int[][] visited,
                                     Stack<String> stk) {
        if (i < 0 || i >= row || j < 0 || j >= col) {
            return false;
        }
        if (matrix[i][j] == 1 || visited[i][j] == 1) {
            return false;
        }
        if (i == row - 1 && j == col - 1) {
            stk.push(i + "," + j);
            Stack<String> s = new Stack<>();
            //打印结果
            while (!stk.empty()) {
                s.push("(" + stk.pop() + ")");
            }
            while (!s.empty()) {
                System.out.println(s.pop());
            }
            return true;
        }

        visited[i][j] = 1;
        stk.push(i + "," + j);

        boolean b1 = processLoc(i - 1, j, visited, stk);
        if (b1) return true;
        boolean b2 = processLoc(i + 1, j, visited, stk);
        if (b2) return true;
        boolean b3 = processLoc(i, j - 1, visited, stk);
        if (b3) return true;
        boolean b4 = processLoc(i, j + 1, visited, stk);
        if (b4) return true;

        stk.pop();
        return false;
    }
}
