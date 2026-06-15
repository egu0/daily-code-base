package im.engure.dp;

import java.util.Scanner;

/*
放苹果
https://www.nowcoder.com/practice/bfd8234bb5e84be0b493656e390bdebf

==============

把m个同样的苹果放在n个同样的盘子里，允许有的盘子空着不放，问共有多少种不同的分法？
注意：如果有7个苹果和3个盘子，（5，1，1）和（1，5，1）被视为是同一种分法。

数据范围：0≤m≤10 ，1≤n≤10 。
 */
public class HJ61 {
    static int[][] arr;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int m = in.nextInt();
        int n = in.nextInt();
        arr = new int[m + 1][n + 1];
        for (int i = 0; i < m + 1; i++) {
            arr[i][0] = 1;
            arr[i][1] = 1;
        }
        for (int j = 0; j < n + 1; j++) {
            arr[0][j] = 1;
            arr[1][j] = 1;
        }
        for (int i = 2; i <= m; i++) {
            for (int j = 2; j <= n; j++) {
                if (i >= j) {
                    arr[i][j] = arr[i - j][j] + arr[i][j - 1];
                } else {
                    arr[i][j] = arr[i][i];
                }
            }
        }
        System.out.println(arr[m][n]);
    }
}
/*
苹果数 m, 盘子数 n
if m >= n, 可分为两种情况:
  1. 有盘子不放，m个苹果放在n-1个盘子
  2. 所有盘子上都放，m-n个苹果放在n个盘子
else if m < n，则肯定有n-m个盘子为空，问题转化为m个苹果放在m个盘子

=============
递归转dp，状态转移方程：
op[m][n] = op[m-n][n] + op[m][n-1] , m>=n
         = op[m][n-m], m<n

=============
  0 1 2 3
0 1 1 1 1
1 1 1 1 1
2 1 1 2 2
3 1 1
4 1 1
5 1 1
*/




