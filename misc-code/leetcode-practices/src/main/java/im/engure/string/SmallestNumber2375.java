package im.engure.string;

import java.util.ArrayList;
import java.util.List;

/*
#业务题
#枚举法
 */
public class SmallestNumber2375 {
    static final char[] CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * 该方案是对【V1 版本】的改进：
     * 1. 使用【new String(char[], offset, count) 的 API】 代替【操作 StringBuilder 的 API】
     * 2. 剪枝：找到的第一个串就是我们需要的答案，直接返回即可
     * <p>
     * 还可以改进的地方：使用【非递归】的方式，缩短代码量
     */
    public String smallestNumber(String pattern) {
        for (int i = 1; i <= 9; i++) {
            boolean[] visited = new boolean[10];
            char[] charArr = new char[10];
            charArr[0] = CHARS[i];
            visited[i] = true;
            List<String> res = generateAllV2(pattern, 1, visited, charArr, i);
            if (!res.isEmpty()) {
                return res.getFirst();
            }
        }
        return "";
    }

    private List<String> generateAllV2(String pattern, int index, boolean[] visited, char[] s, int last) {
        if (index > pattern.length()) {
            return List.of(new String(s, 0, index));
        }
        boolean increase = pattern.charAt(index - 1) == 'I';
        for (int i = 1; i <= 9; i++) {
            if (visited[i]) {
                continue;
            }
            if ((increase && i > last) || !increase && i < last) {
                visited[i] = true;
                s[index] = CHARS[i];
                List<String> res = generateAllV2(pattern, index + 1, visited, s, i);
                if (!res.isEmpty()) {
                    return res;
                }
                visited[i] = false;
            }
        }
        return List.of();
    }

    // region StringBuilder

    public String smallestNumberV1(String pattern) {
        for (int i = 1; i <= 9; i++) {
            boolean[] visited = new boolean[10];
            StringBuilder s = new StringBuilder();
            s.append(i);
            visited[i] = true;
            List<String> res = generateAllV1(pattern, 1, visited, s, i);
            if (!res.isEmpty()) {
                return res.getFirst();
            }
        }
        return "";
    }

    private List<String> generateAllV1(String pattern, int index, boolean[] visited, StringBuilder s, int last) {
        if (index > pattern.length()) {
            return List.of(s.toString());
        }
        List<String> result = new ArrayList<>();
        boolean increase = pattern.charAt(index - 1) == 'I';
        for (int i = 1; i <= 9; i++) {
            if (visited[i]) {
                continue;
            }
            if ((increase && i > last) || !increase && i < last) {
                visited[i] = true;
                s.append(i);
                result.addAll(generateAllV1(pattern, index + 1, visited, s, i));
                visited[i] = false;
                s.delete(s.length() - 1, s.length());
            }
        }
        return result;
    }

    // endregion

}
