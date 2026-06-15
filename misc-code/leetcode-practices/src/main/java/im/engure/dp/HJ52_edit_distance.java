package im.engure.dp;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/*
HJ52 计算字符串的编辑距离
https://www.nowcoder.com/practice/3959837097c7413a961a135d7104c314
---------
官解 https://www.bilibili.com/video/BV1ea4y147FK
 */
public class HJ52_edit_distance {
    public static void main(String[] args) {
        HJ52_recursive_nd_memorization.main(null);
    }
}

class HJ52_recursive {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String s1 = in.next();
        String s2 = in.next();
        System.out.println(process(s1, s2));
    }

    public static int process(String s1, String s2) {
        if (s1.length() == 0 || s2.length() == 0) {
            return Math.max(s1.length(), s2.length());
        }
        if (s1.charAt(s1.length() - 1) == s2.charAt(s2.length() - 1)) {
            return process(s1.substring(0, s1.length() - 1), s2.substring(0, s2.length() - 1));
        }
        return 1 + Math.min(
                Math.min(
                        process(s1.substring(0, s1.length() - 1), s2.substring(0, s2.length())),
                        process(s1.substring(0, s1.length()), s2.substring(0, s2.length() - 1))),
                process(s1.substring(0, s1.length() - 1), s2.substring(0, s2.length() - 1)));
    }
}


class HJ52_recursive_nd_memorization {
    static Map<String, Integer> map = new HashMap<>();

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String s1 = in.next();
        String s2 = in.next();
        System.out.println(process(s1, s2));
    }

    public static int process(String s1, String s2) {
        String key = s1 + ":" + s2;
        if (map.containsKey(key)) {
            return map.get(key);
        }
        if (s1.length() == 0 || s2.length() == 0) {
            int r = Math.max(s1.length(), s2.length());
            map.put(key, r);
            return r;
        }
        if (s1.charAt(s1.length() - 1) == s2.charAt(s2.length() - 1)) {
            int r = process(s1.substring(0, s1.length() - 1), s2.substring(0, s2.length() - 1));
            map.put(key, r);
            return r;
        }
        int r = 1 + Math.min(
                Math.min(
                        process(s1.substring(0, s1.length() - 1), s2.substring(0, s2.length())),
                        process(s1.substring(0, s1.length()), s2.substring(0, s2.length() - 1))),
                process(s1.substring(0, s1.length() - 1), s2.substring(0, s2.length() - 1)));
        map.put(key, r);
        return r;
    }
}


class HJ52_dp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s1 = sc.next();
        String s2 = sc.next();
        int[][] arr = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i < s1.length() + 1; i++) {
            arr[i][0] = i;
        }
        for (int j = 0; j < s2.length() + 1; j++) {
            arr[0][j] = j;
        }
        for (int i = 1; i < s1.length() + 1; i++) {
            for (int j = 1; j < s2.length() + 1; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    arr[i][j] = arr[i - 1][j - 1];
                } else {
                    arr[i][j] = 1 + Math.min(arr[i - 1][j - 1],
                            Math.min(arr[i][j - 1], arr[i - 1][j]));
                }
            }
        }
        System.out.println(arr[s1.length()][s2.length()]);
    }
}

// word1, word2
// op[i][j] 表示将 word1[0...i-1] 转为 word2[0...j-1] 所需要的次数
/*
abc
ace

    a b c
  0 1 2 3
a 1 0 1 2
c 2 1 1 1
e 3 2 2 2
*/