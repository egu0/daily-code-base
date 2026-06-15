package im.engure.string;

import java.util.*;

/**
 * 找出字符串中第一个只出现一次的字符
 */
public class HS59 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String str = in.next();
        char[] chs = str.toCharArray();
        List<Character> lst = new ArrayList<>();
        Map<Character,Integer> map = new HashMap<>();
        for (char ch : chs) {
            if (!map.containsKey(ch)) {
                lst.add(ch);
            }
            map.put(ch, map.getOrDefault(ch, 0) + 1);
        }
        for (Character character : lst) {
            if (map.get(character) == 1) {
                System.out.println(character);
                return;
            }
        }
        System.out.println(-1);
    }
}
