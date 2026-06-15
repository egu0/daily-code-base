package im.engure.api;

import java.util.Stack;

public class UseStackDemo {
    public static void main(String[] args) {

        Stack<Integer> s = new Stack<>();// Stack继承了Vector，线程安全
        s.push(1); // O(1)，最坏情况下使用 Arrays.copy()
        s.peek();       // O(1)
        s.pop();        // O(1)
        s.size();       // O(1)
        s.empty();

        /*
        常用：
         - push(e)
         - pop()
         - peek()
         - size()
         */

    }
}
