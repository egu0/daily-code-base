package im.engure.stack;

import java.util.Stack;

/*
HJ50 四则运算
https://www.nowcoder.com/practice/9999764a61484d819056f807d2a91f1e
=============
描述
输入一个表达式（用字符串表示），求这个表达式的值。
保证字符串中的有效字符包括[‘0’-‘9’],‘+’,‘-’, ‘*’,‘/’ ,‘(’， ‘)’,‘[’, ‘]’,‘{’ ,‘}’。且表达式一定合法。

数据范围：表达式计算结果和过程中满足 |val| \le 1000 \∣val∣≤1000  ，字符串长度满足 1 \le n \le 1000 \1≤n≤1000

输入描述：
输入一个算术表达式

输出描述：
得到计算结果

xmpl
=======
输入：
3+2*{1+2*[-4/(8-6)+7]}

输出：
25

======================================
========= 思路 =======================
======================================

中序表达式，即我们平时用的表达式，将运算符放在中间。
后续表达式：将运算符放在中间，没有括号，仅表示了计算的顺序，恰好是计算器中的一般计算顺序。这种表达式便于利用栈进行计算

======================================
顺序扫描中序表达式，将其转换为后续表达式。转换规则：
a - 是数字， 直接输出
b - 是运算符
  i   - “(” 直接入栈
  ii  - “)” 将符号栈中的元素依次出栈并输出, 直到 “(“, “(“只出栈, 不输出
  iii - 其他符号, 将符号栈中的元素依次出栈并输出, 直到 遇到比当前符号优先级更低的符号或者”(“。 将当前符号入栈。

注意：
- 需要考虑负数，比如 2*(-4*6)+1、2*(-2)+2、(-2+4*2)，发现规则：负数的前一个字符一定为 (，可以根据此标志来提取负数

======================================
参考链接：https://blog.csdn.net/u012507347/article/details/52245233
 */

public class HJ50 {
    public static void main(String[] args) {
        String s = "2*(-4*6)+1";
        Stack<String> symbol = mid2Post(s);
        System.out.println();
        System.out.println(calPost(symbol));
    }

    /**
     * 中序转后序，得到
     *
     * @param s
     * @return
     */
    private static Stack<String> mid2Post(String s) {
        String str = s.replaceAll("\\[", "(").replaceAll("\\{", "(");
        str = str.replaceAll("]", ")").replaceAll("}", ")");
        Stack<String> symbol = new Stack<>();
        Stack<String> target = new Stack<>();
        int flag = 1;//上一个符号类型，(=1，其他为2。此flag为提取负数做准备
        char[] arr = str.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            // 情况 i
            if (arr[i] == '(') {
                symbol.push(arr[i] + "");
                flag = 1;
            } else if (arr[i] == ')') {
                // 情况 ii
                while (true) {
                    if (symbol.peek().equals("(")) {
                        symbol.pop();
                        break;
                    } else {
                        //output
                        System.out.print(symbol.peek() + " ");
                        target.push(symbol.pop() + "");
                    }
                }
                flag = 2;
            } else {
                //情况 iii
                int result = 0;
                int j = i;
                // 1.是数字
                //   1.1 是负数
                if (arr[i] == '-' && flag == 1) {
                    result = Integer.parseInt(arr[i + 1] + "");
                    j = i + 2;
                    while (j < arr.length && arr[j] <= '9' && arr[j] >= '0') {
                        result *= 10;
                        result += Integer.parseInt(arr[j] + "");
                        j++;
                    }
                    result *= -1;
                }
                //   2.2 是无符号正数
                if (arr[i] <= '9' && arr[i] >= '0') {
                    result = Integer.parseInt(arr[i] + "");
                    j = i + 1;
                    while (j < arr.length && arr[j] <= '9' && arr[j] >= '0') {
                        result *= 10;
                        result += Integer.parseInt(arr[j] + "");
                        j++;
                    }
                }
                // 是数字则输出。注意：读取的数字可能为0，通过 j 和 i 来确定
                if (result != 0 || j != i) {
                    System.out.print(result + " ");
                    target.push(result + "");
                    i = j - 1;
                    flag = 2;
                    continue;
                }

                // 2.是符号
                //   2.1 高优先级符号
                if (arr[i] == '*' || arr[i] == '/') {
                    while (!symbol.empty()) {
                        String peek = symbol.peek();
                        if ("+".equals(peek) || "-".equals(peek) || "(".equals(peek)) {
                            break;
                        } else {
                            System.out.print(symbol.peek() + " ");
                            target.push(symbol.pop() + "");
                        }
                    }
                    symbol.push(arr[i] + "");
                } else {
                    //   2.2 低优先级符号
                    while (!symbol.empty()) {
                        String peek = symbol.peek();
                        if ("(".equals(peek)) {
                            break;
                        } else {
                            System.out.print(symbol.peek() + " ");
                            target.push(symbol.pop() + "");
                        }
                    }
                    symbol.push(arr[i] + "");
                }
                flag = 2;
            }
        }
        //将符号栈剩余输出
        while (!symbol.isEmpty()) {
            System.out.print(symbol.peek() + " ");
            target.push(symbol.pop() + "");
        }

        //得到结果
        while (!target.empty()) {
            symbol.push(target.pop());
        }
        return symbol;
    }

    /**
     * 根据一个算术表达式的后续表达式计算运算结果，比如 3 4 + 5 * 6 -
     * step1:  3+4=7,    7 5 * 6 -
     * step2:  7*5=35,   35 6 -
     * step3:  35-6=29,  29
     *
     * @param stk 后续表达式栈，以上边例子为例，3 为栈顶，- 为栈底
     * @return 计算结果
     */
    public static int calPost(Stack<String> stk) {
        Stack<Integer> nums = new Stack<>();
        while (true) {
            String item = stk.pop();
            if ("*".equals(item) || "/".equals(item) || "+".equals(item) || "-".equals(item)) {
                Integer n2 = nums.pop();
                Integer n1 = nums.pop();
                switch (item) {
                    case "*":
                        nums.push(n1 * n2);
                        break;
                    case "/":
                        nums.push(n1 / n2);
                        break;
                    case "+":
                        nums.push(n1 + n2);
                        break;
                    case "-":
                        nums.push(n1 - n2);
                        break;
                    default:
                        throw new RuntimeException("error");
                }
            } else {
                nums.push(Integer.parseInt(item));
            }

            if (stk.isEmpty()) {
                return nums.pop();
            }
        }
    }
}
