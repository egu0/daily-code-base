package im.engure.string;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author engure
 */
public class CustomSortString791 {

    public static void main(String[] args) {
        System.out.println(new CustomSortString791().customSortString("cba", "abcd"));
        System.out.println(new CustomSortString791().customSortString("hucw",
                "utzoampdgkalexslxoqfkdjoczajxtuhqyxvlfatmptqdsochtdzgypsfkgqwbgqbcamdqnqztaqhqanirikahtmalzqjjxtqfnh"));
    }

    public String customSortString(String order, String s) {

        //将order中的每个字符映射都到一个负数上，且索引越小的字符对应的负数越小
        int[] orderOff = new int[26];
        for (int i = 0; i < order.length(); i++) {
            orderOff[order.charAt(i) - 'a'] = i - 26;
        }

        //将s中每个字符映射到一个数字。如果该字符在order中出现过，则为算得的负数；反之则为ascii
        int[] s1 = new int[s.length()];
        for (int i = 0; i < s.length(); i++) {
            int ch = orderOff[s.charAt(i) - 'a'];
            s1[i] = (ch < 0) ? ch : s.charAt(i);
        }

        //排序。负数代表的字符出现在前边，其余的字符都是正数出现在后边
        Arrays.sort(s1);

        //根据int值转化即可
        StringBuilder sb = new StringBuilder();
        for (int val : s1) {
            sb.append(val < 0 ? order.charAt(val + 26) : (char) val);
        }

        return sb.toString();
    }

    /**
     * 3ms
     *
     * @param order
     * @param s
     * @return
     */
    public String customSortString1(String order, String s) {

        boolean[] exists = new boolean[26];
        for (int i = 0; i < order.length(); i++) {
            exists[order.charAt(i) - 'a'] = true;
        }

        Map<Character, Integer> cnt = new HashMap<>((int) (26 * 1.4));
        for (int i = 0; i < s.length(); i++) {
            cnt.put(s.charAt(i), cnt.getOrDefault(s.charAt(i), 0) + 1);
        }

        Map<Character, String> ss = new HashMap<>((int) (26 * 1.4));
        for (Character ch : cnt.keySet()) {
            StringBuilder s0 = new StringBuilder();
            for (int j = 0; j < cnt.get(ch); j++) {
                s0.append(ch);
            }
            ss.put(ch, s0.toString());
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < order.length(); i++) {
            if (cnt.containsKey(order.charAt(i))) {
                sb.append(ss.get(order.charAt(i)));
            }
        }

        for (Character ch : cnt.keySet()) {
            if (!exists[ch - 'a']) {
                sb.append(ss.get(ch));
            }
        }

        return sb.toString();
    }
}
