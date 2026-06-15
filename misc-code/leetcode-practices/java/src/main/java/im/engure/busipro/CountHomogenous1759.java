package im.engure.busipro;

/**
 * my solution <a href="https://leetcode.cn/problems/count-number-of-homogenous-substrings/solution/by-engure-prln/">...</a>
 *
 * @author Administrator
 */
public class CountHomogenous1759 {

    public static void main(String[] args) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 23456; i++) {
            s.append("w");
        }//100000
        test(s.toString());

        // int 最大放 20 亿，即 2 后边 9 个 0
        //System.out.println(Integer.MAX_VALUE);
    }

    public static void test(String s) {
        System.out.println("correct:" + new CountHomogenous1759().countHomogenousOk(s));
        System.out.println("----------");
        System.out.println("original:" + new CountHomogenous1759().countHomogenous(s));
    }

    /**
     * 问题排查：
     * - 多算
     * - int存不下。int最大存20亿，但如果 s 中字母都相同且为最大即 10w 个，那么 10w * 5w 是50亿，int存不下，要用long
     *
     * @param s
     * @return
     */
    public int countHomogenous(String s) {
        int mod = 1000000007, j;
        long cnt = 0;
        long count;
        for (int i = 0; i < s.length(); i++) {
            count = 1;
            // s[i]!=s[i-1], count max continuous length of s[i]
            for (j = i + 1; j < s.length() && s.charAt(j - 1) == s.charAt(j); j++) {
                count++;
            }
            long tmp = 0;
            if (count % 2 == 0) {
                tmp = (count / 2) * (count + 1);
            } else {
                tmp = ((count + 1) / 2) * count;
            }
            i = j - 1;
            cnt += tmp;
            cnt %= mod;
        }
        return (int) cnt;
    }

    /**
     * 9ms
     *
     * @param s
     * @return
     */
    public int countHomogenousOk(String s) {
        int mod = 1000000007, cnt = 0, count = 0;
        s = "$" + s;
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i - 1) != s.charAt(i)) {
                count = 1;
            } else {
                count++;
            }
            cnt += count;
            cnt %= mod;
        }
        return cnt;
    }

}
