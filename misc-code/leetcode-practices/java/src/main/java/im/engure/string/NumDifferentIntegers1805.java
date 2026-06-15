package im.engure.string;

import im.engure.util.MyAssertions;

import java.util.HashSet;
import java.util.Set;

public class NumDifferentIntegers1805 {

    public static void main(String[] args) {
        NumDifferentIntegers1805 o = new NumDifferentIntegers1805();
        MyAssertions.assertEqual(o.numDifferentIntegers("a1b01c001"), 1);
        MyAssertions.assertEqual(o.numDifferentIntegers("1"), 1);
        MyAssertions.assertEqual(o.numDifferentIntegers("a"), 0);
        MyAssertions.assertEqual(o.numDifferentIntegers("1a2"), 2);
        MyAssertions.assertEqual(o.numDifferentIntegers("a0b00"), 1);
        MyAssertions.assertEqual(o.numDifferentIntegers("0a00"), 1);
        MyAssertions.assertEqual(o.numDifferentIntegers("0a001b000"), 2);
    }

    /**
     * 1ms
     */
    public int numDifferentIntegers(String word) {
        boolean isNum = false;
        int start = 0, end;
        Set<String> set = new HashSet<>();
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if ('0' <= ch && ch <= '9') {
                if (!isNum) {
                    start = i;
                    isNum = true;
                }
            } else if (isNum) {
                end = i;
                add2Set(set, word, start, end);
                isNum = false;
            }
        }
        if (isNum) {
            add2Set(set, word, start, word.length());
        }
        return set.size();
    }

    private void add2Set(Set<String> set, String word, int start, int end) {
        for (int i = start; i < end; i++) {
            if (word.charAt(i) != '0') {
                set.add(word.substring(i, end));
                return;
            }
        }
        set.add("0");
    }

    /**
     * 2ms，set 去重
     */
    public int numDifferentIntegers1(String word) {
        boolean isNum = false;
        int start = 0, end;
        Set<String> set = new HashSet<>();
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if ('0' <= ch && ch <= '9') {
                if (!isNum) {
                    start = i;
                    isNum = true;
                }
            } else if (isNum) {
                end = i;
                set.add(word.substring(start, end));
                isNum = false;
            }
        }
        if (isNum) {
            set.add(word.substring(start));
        }
        return process(set);
    }

    private int process(Set<String> set) {
        Set<String> newSet = new HashSet<>();
        for (String str : set) {
            if (!str.isEmpty()) {
                if (str.charAt(0) != '0') {
                    newSet.add(str);
                } else {
                    int i = 0;
                    for (; i < str.length(); i++) {
                        if (str.charAt(i) != '0') {
                            break;
                        }
                    }
                    if (i == str.length()) {
                        newSet.add("0");
                    } else {
                        newSet.add(str.substring(i));
                    }
                }
            }
        }
        return newSet.size();
    }
}
