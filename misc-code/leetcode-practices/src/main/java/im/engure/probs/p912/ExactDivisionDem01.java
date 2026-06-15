package im.engure.probs.p912;

/*
problem：https://blog.csdn.net/qq_43341057/article/details/120249215
已知两个数组，m[]，n[]，问 m 数组的所有元素相乘的结果能否[整除] n 数组所有元素相乘的结果
 */

public class ExactDivisionDem01 {

    public static void main(String[] args) {

        /*
        取模运算中的正负值。
        已知 m>0 且为整数。
            * 正数 % m   0 ~ m-1
            * 正数 % -m  0 ~ m-1
            * 负数 % m   -(m-1) ~ 0
            * 负数 % -m  -(m-1) ~ 0
         */
        int j;
        for (int i = -10; i < 0; i++) {
            j = -i;
            System.out.println(j + " / 5 = " + j / 5 + " ... " + (j % 5));
            System.out.println(j + " / -5 = " + j / -5 + " ... " + (j % -5));
            System.out.println(i + " / 5 = " + i / 5 + " ... " + (i % 5));
            System.out.println(i + " / -5 = " + i / -5 + " ... " + (i % -5));
            System.out.println("--------------------------------");
        }

    }

}
