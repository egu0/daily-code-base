package im.engure.string;

import java.util.*;

/**
 * @author engure
 */
public class GroupAnagrams49 {
    public static void main(String[] args) {
        System.out.println(new GroupAnagrams49().groupAnagrams(new String[]{"eat", "tea", "tan", "ate", "nat", "bat"}));
        System.out.println(new GroupAnagrams49().groupAnagrams(new String[]{"", "ab", "ba", "a"}));
        System.out.println(new GroupAnagrams49().groupAnagrams(new String[]{"aab", "abb"}));
    }

    public List<List<String>> groupAnagrams(String[] strs) {
        int[] cnt = new int[26];
        StringBuilder sb = new StringBuilder();
        Map<String, List<String>> res0 = new HashMap<>();
        for (String s : strs) {
            sb.delete(0, sb.length());
            for (int i = 0; i < 26; i++) {
                cnt[i] = 0;
            }
            for (int i = 0; i < s.length(); i++) {
                cnt[s.charAt(i) - 'a']++;
            }
            for (int i = 0; i < 26; i++) {
                if (cnt[i] > 0) {
                    while (cnt[i]-- > 0) {
                        sb.append((char) (i + 'a'));
                    }
                }
            }
            List<String> list = res0.getOrDefault(sb.toString(), new LinkedList<>());
            list.add(s);
            res0.put(sb.toString(), list);
        }
        return new LinkedList<>(res0.values());
    }
}
