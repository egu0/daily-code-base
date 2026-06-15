package im.engure.math;

import java.util.Scanner;

/*
质数因子
https://www.nowcoder.com/practice/196534628ca6490ebce2e336b47b3607
 */
public class HJ6 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            long num = sc.nextLong();
            for (long i = 2; i <= (long) Math.sqrt(num); i++) {
                while (num % i == 0) {
                    System.out.print(i + " ");
                    num /= i;
                }
            }
            if (num > 2) {
                System.out.print(num);
            }
        }
    }
}
