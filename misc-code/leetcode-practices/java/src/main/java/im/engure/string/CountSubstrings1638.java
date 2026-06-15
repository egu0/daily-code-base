package im.engure.string;

/*
#业务题
 */
public class CountSubstrings1638 {
    public int countSubstrings(String s, String t) {
        int n1 = s.length(), n2 = t.length();
        int n3 = Math.min(n1, n2), res = 0;
        for (int i = 1; i <= n3; i++) {
            for (int j = 0; j < n1; j++) {
                if (j + i <= n1) {
                    //[j, j+i-1]
                    for (int k = 0; k < n2; k++) {
                        if (k + i <= n2) {
                            //[k, k+i-1]
                            if (match(s, t, j, k, i)) {
                                res += 1;
                            }
                        }
                    }
                }
            }
        }
        return res;
    }

    private boolean match(String s, String t, int j, int k, int i) {
        boolean hit = false;
        for (int l = 0; l < i; l++) {
            if (s.charAt(j + l) != t.charAt(k + l)) {
                if (hit) {
                    return false;
                }
                hit = true;
            }
        }
        return hit;
    }
}

/*
s = "aba", t = "baba"
("[a]ba", "[b]aba")
("[a]ba", "ba[b]a")
("a[b]a", "b[a]ba")
("a[b]a", "bab[a]")
("ab[a]", "[b]aba")
("ab[a]", "ba[b]a")
-------------------------
s = "ab", t = "bb"
("[a]b", "[b]b")
("[a]b", "b[b]")
("[ab]", "[bb]")
-------------------------
s = "abe", t = "bbc"
("[a]be", "[b]bc") 1
("[a]be", "b[b]c") 2
("[a]be", "bb[c]") 3
("a[b]e", "bb[c]") 4
("ab[e]", "[b]bc") 5
("ab[e]", "b[b]c") 6
("ab[e]", "bb[c]") 7
("[ab]e", "[bb]c") 8
("[ab]e", "b[bc]") 9
("a[be]", "b[bc]") 10
 */