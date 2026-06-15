package im.engure.stack;

import java.util.Stack;

public class LongestValidParentheses32 {
    public static void main(String[] args) {
        System.out.println(new LongestValidParentheses32().longestValidParentheses(")))(()))"));
        System.out.println(new LongestValidParentheses32().longestValidParentheses("(()"));
        System.out.println(new LongestValidParentheses32().longestValidParentheses(")()())"));
        System.out.println(new LongestValidParentheses32().longestValidParentheses("()(()"));
        System.out.println(new LongestValidParentheses32().longestValidParentheses("(()()))))"));
    }

    /**
     * "最快的方法"
     * 用 maxL[i] 记录 s[0,1,,,i-1] 上的最大合法长度，遍历数组，根据 s[i] 和 maxL[i-1] 确定左边边界和 maxL[i]
     *
     * @param s
     * @return
     */
    public int longestValidParentheses(String s) {
        int sl = s.length();
        int[] maxL = new int[sl];
        int res = 0;
        for (int i = 1; i < sl; i++) {
            if (s.charAt(i) == ')') {
                // ...()
                if (s.charAt(i - 1) == '(') {
                    maxL[i] = 2 + (i >= 2 ? maxL[i - 2] : 0);
                } else if (i - maxL[i - 1] - 1 >= 0 && s.charAt(i - maxL[i - 1] - 1) == '(') {
                    // (...())
                    maxL[i] = 2 + maxL[i - 1] + ((i - maxL[i - 1] - 2) >= 0 ? maxL[i - maxL[i - 1] - 2] : 0);
                }
            }
            res = Math.max(res, maxL[i]);
        }
        return res;
    }

    public void process(int l, int r, char[] arr) {
        if (r < arr.length && l < r && arr[l] == '(' && arr[r] == ')') {
            arr[l] = '1';
            arr[r] = '1';
            if (l - 1 >= 0 && r + 1 < arr.length && arr[l - 1] == '(' && arr[r + 1] == ')') {
                process(l - 1, r + 1, arr);
            }
        }
    }

    /**
     * 用栈记录所有不合法位置，然后计算最大的合法子数组长度
     *
     * @param s
     * @return
     */
    public int longestValidParentheses1(String s) {
        char[] chs = s.toCharArray();
        boolean[] mark = new boolean[chs.length];
        // ( --> 1,   ) --> 7
        Stack<Integer> stk = new Stack<>();
        for (int i = 0; i < chs.length; i++) {
            char ch = chs[i];
            if (ch == ')') {
                if (stk.isEmpty()) {
                    mark[i] = true;
                } else {
                    stk.pop();
                }
            } else {
                stk.push(i);
            }
        }
        while (!stk.isEmpty()) {
            mark[stk.pop()] = true;
        }
        //计算连续为false的最长子数组
        int max = 0;
        for (int i = 0; i < chs.length; i++) {
            if (!mark[i]) {
                int sub = 0;
                int j = i;
                while (j < chs.length && !mark[j]) {
                    sub++;
                    j++;
                }
                max = Math.max(sub, max);
                i = j - 1;
            }
        }
        return max;
    }

    /**
     * 错解
     * 1。对于 ），它可能是不合法的，不能入栈
     * 2。对于（，应入栈
     *
     * @param s
     * @return
     */
    public int longestValidParentheses0(String s) {
        s = s.replaceAll("\\(", "1").replaceAll("\\)", "7");
        char[] chs = s.toCharArray();
        Stack<Integer> stk = new Stack<>();
        int res = 0;
        for (char ch : chs) {
            int num = ch - '1' + 1;
            if (!stk.isEmpty() && stk.peek() + num == 8 && num == 7) {
                stk.pop();
                res += 2;
            } else {
                stk.push(num);
            }
        }
        return res;
    }
}
