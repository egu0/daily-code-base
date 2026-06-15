package im.engure.string;

public class MakeStringsEqual2546 {
    // https://leetcode.com/problems/apply-bitwise-operations-to-make-strings-equal/solutions/6418010/one-line-solutioin
    public boolean makeStringsEqual(String s, String target) {
        return s.equals(target) || !(s.lastIndexOf("1") == -1 || target.lastIndexOf("1") == -1);
    }
}
