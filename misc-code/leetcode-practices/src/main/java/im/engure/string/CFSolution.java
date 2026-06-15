package im.engure.string;

import java.util.Scanner;

/**
 * 第一个 CF 题目
 * <a href="https://codeforces.com/contest/71/problem/A">...</a>
 * Contest 71， Problem A
 */
public class CFSolution {
    public static void main(String[] args) {
        int n;
        Scanner in = new Scanner(System.in);
        n = in.nextInt();
        while (n-- > 0) {
            String s = in.next();
            int len = s.length();
            if (len <= 10) {
                System.out.println(s);
            } else {
                System.out.println(s.charAt(0) + "" + (len - 2) + "" + s.charAt(len - 1));
            }
        }
    }
}
