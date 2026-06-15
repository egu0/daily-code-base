package im.engure.string;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IsIsomorphic205 {
	public static boolean isIsomorphic(String s, String t) {
		Map<Character, Character> map = new HashMap<>();
		Set<Character> set = new HashSet<>();
		for (int i = 0; i < s.length(); i++) {
			if (map.containsKey(s.charAt(i)) && !map.get(s.charAt(i)).equals(t.charAt(i))) {
				// 映射错误
				return false;
			} else if (!map.containsKey(s.charAt(i))) {
				// 没有映射则添加映射
				map.put(s.charAt(i), t.charAt(i));
				// 不同字符不能映射到同一个字符上
				if (set.contains(t.charAt(i))) {
					return false;
				}
				set.add(t.charAt(i));
			}
		}
		return true;
	}

	// 整个二维数组试试
	public static boolean m2(String s, String t) {
		// 按 8bit Ascii 来算，0-255
		// map[i][0] = j 表示字符 i 映射字符 k（十进制下）
		// map[i][1] = k 表示字符 i 被字符 k 映射（十进制下）
		int[][] map = new int[256][2];
		for (int i = 0; i < s.length(); i++) {
			int a = (int) s.charAt(i);
			int b = (int) t.charAt(i);
			if (map[a][0] != 0 && map[a][0] != b) {
				// 存在映射且映射错误
				return false;
			} else if (map[a][0] == 0) {
				// 没有映射则添加映射
				map[a][0] = b;
				if (map[b][1] != 0) {
					// 字符 b 已经被映射
					return false;
				}
				map[b][1] = a;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		System.out.println(isIsomorphic("abb", "egg") + "," + m2("abb", "egg"));
		System.out.println(isIsomorphic("badc", "baba") + "," + m2("badc", "baba"));
	}
}
