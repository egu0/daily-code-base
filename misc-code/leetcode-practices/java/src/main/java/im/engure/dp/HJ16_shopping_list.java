package im.engure.dp;

import java.util.Scanner;

/*
购物单
https://www.nowcoder.com/practice/f9c6f980eeec43ef85be20755ddbeaf4
==================
* 类似01背包问题

 */
public class HJ16_shopping_list {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int totalMoney = sc.nextInt();
        totalMoney /= 10;
        int n = sc.nextInt();
        // i*6 - 价格，i*6+1 - 重要度
        // i*6+2 - 附件1价格，i*6+3 - 附件1重要度
        // i*6+4 - 附件2价格，i*6+5 - 附件2重要度
        int[] arr = new int[n * 6];
        for (int i = 0; i < n; i++) {
            int v = sc.nextInt() / 10;
            int p = sc.nextInt();
            int q = sc.nextInt();
            // 附件
            if (q != 0) {
                if (arr[(q - 1) * 6 + 2] != 0) {
                    arr[(q - 1) * 6 + 4] = v;
                    arr[(q - 1) * 6 + 5] = p * v;
                } else {
                    arr[(q - 1) * 6 + 2] = v;
                    arr[(q - 1) * 6 + 3] = p * v;
                }
            } else {
                // 主体
                arr[i * 6] = v;
                arr[i * 6 + 1] = p * v;
            }
        }

        // 0,1,2...M
        int[] dp = new int[totalMoney + 1];
        for (int i = 0; i < n; i++) {
            if (arr[i * 6] != 0) {
                int v = arr[i * 6];
                int p = arr[i * 6 + 1];
                int v1 = arr[i * 6 + 2];
                int p1 = arr[i * 6 + 3];
                int v2 = arr[i * 6 + 4];
                int p2 = arr[i * 6 + 5];
                for (int j = totalMoney; j >= v; j--) {
                    //附件
                    dp[j] = Math.max(dp[j], p + dp[j - v]);
                    //或主体
                    if (j >= v + v1) {
                        dp[j] = Math.max(dp[j], p + p1 + dp[j - v - v1]);
                    }
                    if (j >= v + v2) {
                        dp[j] = Math.max(dp[j], p + p2 + dp[j - v - v2]);
                    }
                    if (j >= v + v1 + v2) {
                        dp[j] = Math.max(dp[j], p + p1 + p2 + dp[j - v - v1 - v2]);
                    }
                }
            }
        }
        System.out.println(dp[totalMoney] * 10);
    }
}