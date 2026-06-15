package im.engure.string;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * () -> 1
 * AB -> A+B
 * (A) -> 2A
 */
public class ScoreOfParentheses856 {
    public int scoreOfParentheses(String s) {
        int n = s.length();
        Stack<Integer> posStack = new Stack<>();
        Map<Integer, Integer> posCoreMap = new HashMap<>();
        Map<Integer, Integer> rangeMap = new HashMap<>();
        for (int i = 0; i < n; i++) {
            char ch = s.charAt(i);
            if (ch == '(') {
                posStack.push(i);
            } else if (!posStack.empty()) {
                Integer p = posStack.pop();
                if (p + 1 == i) {
                    posCoreMap.put(i, 1);
                    posCoreMap.put(p, 1);
                } else {
                    Integer c2 = posCoreMap.get(i - 1);
                    c2 *= 2;
                    posCoreMap.put(i, c2);
                    posCoreMap.put(p, c2);
                }
                rangeMap.put(i, p);
                rangeMap.put(p, i);
                // 合并区间
                //  [      ]
                // ((())(()))
                //      p  i
                Integer preRangeStart = rangeMap.get(p - 1);
                if (preRangeStart != null) {
                    Integer prevCore = posCoreMap.get(preRangeStart);
                    Integer currCore = posCoreMap.get(p);
                    rangeMap.put(preRangeStart, i);
                    rangeMap.put(i, preRangeStart);
                    posCoreMap.put(preRangeStart, prevCore + currCore);
                    posCoreMap.put(i, prevCore + currCore);
                }
            } else {
                return -1; // 不会出现的情况
            }
        }
        return posCoreMap.get(0);
    }
}
