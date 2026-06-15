package im.engure.string;

public class LongestPalindrome5 {
}


//b a b a d
// i...j 是否为回文序列？
// 1->2345
// 2->345
// 3->45
// 4->5

//a
//aba
//aaa
//abcba

// ababaca

//aa
//abba
//aaaa

//涉及两个点的区间，并且上下步骤关系很少，递归难以解决
//以 相同元素区间[i..j] 为中间想两边扩散，O(N) 复杂度
//整理的题解：https://leetcode-cn.com/problems/longest-palindromic-substring/solution/zui-chang-hui-wen-zi-chuan-on-by-engure-q76t/

class Solution5 {

    public String longestPalindrome(String s) {
        if (s == null || s.length() == 0) return "";

        int begin = 0, len = 1;
        int L = s.length();
        char[] sa = s.toCharArray();

        int j, tmp, newLen;
        for (int i = 0; i < L; i++) {
            //找到相同元素，直到区间右侧
            j = i;
            while (j < L - 1 && sa[j + 1] == sa[i]) j++;
            //跳过重复的，下次从 i+1 开始找
            tmp = j;
            //从[i..j]向外找回文序列，此时 sa[i...j] = sa[i]
            while (j < L - 1 && i > 0 && sa[i - 1] == sa[j + 1]) {
                i--;
                j++;
            }
            if ((newLen = j - i + 1) > len) {
                len = newLen;
                begin = i;
            }
            //下一轮
            i = tmp;
        }
        return s.substring(begin, len + begin);//begin,end
    }

}

/*
作者：engure
链接：https://leetcode-cn.com/problems/longest-palindromic-substring/solution/zui-chang-hui-wen-zi-chuan-on-by-engure-q76t/
来源：力扣（LeetCode）
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
*/

