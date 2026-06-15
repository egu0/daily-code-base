package im.engure.string;

import java.util.Stack;

/**
 * 验证托号匹配正确性
 * []{}()
 * <p>
 * {[]()} true
 * ()[]{} true
 * ([{]}) false
 * ([)]   false
 * <p>
 * 细节
 */

public class IsValid20 {
}

class Solution {
    public boolean isValid(String s) {
        char[] sa = s.toCharArray();
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < sa.length; i++) {
            if (stack.size() > 0) {
                char top = stack.peek();
                char mat = sa[i];
                if ((top == '[' && mat == ']')
                        || (top == '{' && mat == '}')
                        || (top == '(' && mat == ')')) stack.pop();
                else stack.push(sa[i]);
            } else stack.push(sa[i]);
        }
        return stack.empty();
    }
}
