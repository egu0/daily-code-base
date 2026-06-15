package im.engure.probs.p918;

/*

给出一个数字 0 <= n <= 10^17，请确定至少需要多少个整数才能计算求得它。这些整数必须是 2 的幂指数，比如 1，2，4，8，，，
另外，必须通过  x1 + x2 + x3 + .... 或  y1 - y2 - y3 .... 的计算公式求得他。
每个整数只能用一次。

比如：
1 ：1 = 2^0
3 ：1+2 = 2^0 + 2^1
7 ：8-1 = 2^3 - 2^0，而非 1+2+3
30：32-2 = 2^5 - 2^1

---------------------

位运算。
- 加法情况。需要确定二进制表示中 1 的个数。
- 减法情况。计算大于等于 n 的最大2的幂指数 m，计算 m-n 表示的数二进制中 1 的个数。

90% 通过率。

 */


public class CalcNumDemo01 {
    public static void main(String[] args) {
        //Scanner in = new Scanner(System.in);
        //long n = in.nextLong();

        int c1, c2;
        for (long i = Long.MAX_VALUE - 100000; i < Long.MAX_VALUE; i++) {
            c1 = count1(i);
            c2 = count2(i);

            System.out.println(i + " >>> " + Math.min(c1, c2));
        }

    }

    static int count2(long n) {
        long m = max(n);
        return count1(m - n) + 1;
    }

    static long max(long n) {
        long res = 1;
        while (n != 0) {
            n = n >> 1;
            res = res << 1;
        }
        return res;
    }

    static int count1(long n) {
        long res;
        int c = 0;
        while (n != 0) {
            res = n & 1;
            if (res == 1) c++;
            n = n >> 1;
        }
        return c;
    }

}
