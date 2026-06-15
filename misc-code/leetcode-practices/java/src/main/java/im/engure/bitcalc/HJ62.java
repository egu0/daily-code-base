package im.engure.bitcalc;

import java.util.Scanner;

/*
HJ62 查找输入整数二进制中1的个数
 */
public class HJ62 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (in.hasNext()) {
            int n = in.nextInt();
            int c = 0;
            while (n != 0) {
                if ((n & 1) != 0) {
                    c++;
                }
                n >>= 1;
            }
            System.out.println(c);
        }
    }
}
