package im.engure.math;

import java.util.Scanner;

/**
 * <p>杨辉三角变形</p>
 *
 * @author Guoyj
 * @return
 * @since 2022/11/6 00:07
 */
public class HJ53 {
    public static void main(String[] args) {
        for (int n = 1; n <= 20; n++) {
            System.out.print(n + "=====>");
            long[] arr = new long[3];
            arr[0] = 1;
            arr[1] = 1;
            arr[2] = 1;
            for (int i = 3; i <= n; i++) {
                long[] tmp = new long[2 * i - 1];
                tmp[0] = 1;
                tmp[1] = arr[0] + arr[1];
                for (int j = 2; j < i; j++) {
                    tmp[j] = arr[j - 2] + arr[j - 1] + arr[j];
                }
                tmp[i] = tmp[i - 2];
                arr = tmp;
            }
            boolean finished = false;
            for (int i = 0; i <= n; i++) {
                if (arr[i] % 2 == 0) {
                    System.out.println(i + 1);
                    finished = true;
                    break;
                }
            }
            if (!finished) {
                System.out.println(-1);
            }
        }
    }

    /**
    * <p>找规律题目，我吐了呀</p>
    *
    * @return
    * @author Guoyj
    * @since 2022/11/6 01:40
    */
    public static void main2(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            int n = sc.nextInt();
            if (n <= 2) {
                System.out.println(-1);
            } else if (n % 2 == 1) {
                System.out.println(2);
            } else if (n % 4 == 0) {
                System.out.println(3);
            } else {
                System.out.println(4);
            }
        }


    }
}