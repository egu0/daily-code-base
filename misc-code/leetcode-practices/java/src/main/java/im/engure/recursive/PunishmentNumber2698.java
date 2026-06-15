package im.engure.recursive;

import java.util.ArrayList;
import java.util.List;

/**
 * 1*1=1      ... 1
 * 9*9=81     ... 8+1
 * 10*10=100  ... 10+0
 * 36*36=1296 ... 1+29+6
 */

public class PunishmentNumber2698 {
    public static void main(String[] args) {
        PunishmentNumber2698 o = new PunishmentNumber2698();
//        o.punishmentNumber(1000);
        o.isAnswer(81, 9);
        o.isAnswer(1296, 36);
        o.isAnswer(100, 10);
    }

    // region new-method-which-i-can-not-understand

    public int punishmentNumber(int n) {
        int punishmentNum = 0;
        for (int i = 1; i <= n; i++) {
            int temp = i * i;
            if (isAnswer(temp, i)) {
                punishmentNum += temp;
            }
        }
        return punishmentNum;
    }

    // 一种很巧妙的做法
    // 见官方题解：https://leetcode.com/problems/find-the-punishment-number-of-an-integer/solutions/6399433/find-the-punishment-number-of-an-integer
    public boolean isAnswer(int num, int target) {
        if (num < target || target < 0) {
            return false;
        }

//        System.out.println("isAnswer: num=" + num + ", target=" + target);
        if (num == target) {
            return true;
        }

        // 分割数字
        return (isAnswer(num / 10, target - (num % 10)) ||
                isAnswer(num / 100, target - (num % 100)) ||
                isAnswer(num / 1000, target - (num % 1000)));
    }

    // endregion

    // region use-prepared-answers

    // 1 <= n <= 1000
    public int punishmentNumberV2(int n) {
        int[] all = {1, 9, 10, 36, 45, 55, 82, 91, 99, 100, 235, 297, 369, 370, 379, 414, 657, 675, 703, 756, 792, 909, 918, 945, 964, 990, 991, 999, 1000};
        int[] all2 = {1, 81, 100, 1296, 2025, 3025, 6724, 8281, 9801, 10000, 55225, 88209, 136161, 136900, 143641, 171396, 431649, 455625, 494209, 571536, 627264, 826281, 842724, 893025, 929296, 980100, 982081, 998001, 1000000};
        int sum = 0;
        for (int i = 0; i < all.length; i++) {
            if (n < all[i]) {
                break;
            }
            sum += all2[i];
        }
        return sum;
    }

    // endregion

    // region old-complex-one

    // 复杂度：较高
    public int punishmentNumberV1(int n) {
        int res = 0;
        for (int i = 1; i <= n; i++) {
            List<List<String>> papa = partitions(String.valueOf(i * i), 0, i);
//            System.out.printf("%d -> partitions: %s\n", i, papa);
            for (List<String> pa : papa) {
                int sum = 0;
                for (String p : pa) {
                    sum += Integer.parseInt(p);
                }
                if (i == sum) {
//                    System.out.printf("hit: %d * %d = %d ... %s\n", i, i, i * i, pa);
                    System.out.printf("%d,\n", i);
                    res += (i * i);
                    break;
                }
            }
        }
        return res;
    }

    public List<List<String>> partitions(String num, int idx, Integer baseValue) {
        if (idx == num.length() - 1) {
            return List.of(List.of(String.valueOf(num.charAt(idx))));
        }

        List<List<String>> res = new ArrayList<>();
        String s = String.valueOf(num.charAt(idx));

        List<List<String>> papa = partitions(num, idx + 1, baseValue);
        for (List<String> pa : papa) {
            String first = pa.getFirst();
            ArrayList<String> s1 = new ArrayList<>(pa);
            s1.addFirst(s);
            res.add(s1);

            ArrayList<String> s2 = new ArrayList<>(pa);
            String ff = s + first;
            // 剪枝
            if (Integer.parseInt(ff) <= baseValue) {
                s2.set(0, ff);
                res.add(s2);
            }
        }
        return res;
    }

    // endregion
}
