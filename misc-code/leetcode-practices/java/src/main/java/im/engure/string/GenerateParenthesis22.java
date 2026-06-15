package im.engure.string;

import java.util.ArrayList;
import java.util.List;

public class GenerateParenthesis22 {
    public static void main(String[] args) {
        System.out.println(new Solution22().generateParenthesis(3));
    }

    /**
     * 栈的思想，使用递归
     */
    static class Solution22 {

        int N;
        List<String> l0 = new ArrayList<>();

        public List<String> generateParenthesis(int n) {
            N = n;
            StringBuilder sb = new StringBuilder();
            sb.append('(');
            process(sb, 1, 0);
            return l0;
        }

        public void process(StringBuilder subStr, int left, int right) {

            //左满，结束
            if (left == N) {

                //左满，补全右括弧
                while (right != N) {
                    subStr.append(')');
                    right++;
                }
                l0.add(subStr.toString());
                return;
            }

            //根据左括弧和右括弧数量，控制正确性
            if (left == right)
                process(new StringBuilder(subStr).append("("), left + 1, right);
            else if (left > right) { // (()
                process(new StringBuilder(subStr).append("("), left + 1, right);
                process(new StringBuilder(subStr).append(")"), left, right + 1);
            }

        }

    }


/*

(

((   ()


(((  (()
()(  ())


 */

}
