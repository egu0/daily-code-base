package im.engure.string;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Partition131 {

    // region DP+DP

    /*
                    a   b   b   a   b   a
                 a  1   0   0   1   0   0
                 b      1   1   0   0   0
                 b          1   0   1   0
                 a              1   0   1
                 b                  1   0
                 a                      1

               p[i][i+len] = {
                                1                                  ,len=0
                                a[i]==a[j]?1:0                     ,len=1
                                (a[i]==a[j] && p[i+1][j-1]=1)?1:1  ,len>1
                            }
     */
    public List<List<String>> partition(String s) {
        int n = s.length();
        int[][] palindromes = new int[n][n];
        // 计算右上三角形。len 为步长
        for (int len = 0; len < n; len++) {
            for (int i = 0; i < n; i++) {
                int j = i + len;
                // dp[i][i+len], len 从 0 到 n-1，表示每次计算一个子对角线
                if (j >= n) {
                    continue;
                }
                boolean same = s.charAt(i) == s.charAt(j);
                if (len == 0) {
                    palindromes[i][j] = 1;
                } else if (len == 1) {
                    palindromes[i][j] = same ? 1 : 0;
                } else {
                    boolean papa = same && palindromes[i + 1][j - 1] == 1;
                    palindromes[i][j] = papa ? 1 : 0;
                }
            }
        }

        Map<Integer, List<List<String>>> records = new HashMap<>();
        // DP：从后往前计算 substring(i) 上的 partition() 结果
        for (int i = n - 1; i >= 0; i--) {
            List<List<String>> subRes = new ArrayList<>();
            for (int j = i; j < n; j++) {
                if (palindromes[i][j] == 1) {
                    String base = s.substring(i, j + 1);
                    if (j + 1 == n) {
                        subRes.add(List.of(base));
                    } else {
                        List<List<String>> next = records.get(j + 1);
                        for (List<String> l : next) {
                            ArrayList<String> l2 = new ArrayList<>(l);
                            l2.addFirst(base);
                            subRes.add(l2);
                        }
                    }
                }
            }
            records.put(i, subRes);
        }

        return records.get(0);
    }

    // endregion

    // region DP+DFS

    /*
                    a   b   b   a   b   a
                 a  1   0   0   1   0   0
                 b      1   1   0   0   0
                 b          1   0   1   0
                 a              1   0   1
                 b                  1   0
                 a                      1

               p[i][i+len] = {
                                1                                  ,len=0
                                a[i]==a[j]?1:0                     ,len=1
                                (a[i]==a[j] && p[i+1][j-1]=1)?1:1  ,len>1
                            }
               len from 0 to n-1
     */

    public List<List<String>> partitionV2(String s) {
        int n = s.length();
        int[][] palindromes = new int[n][n];
        // 计算右上三角形。len 为步长
        for (int len = 0; len < n; len++) {
            for (int i = 0; i < n; i++) {
                int j = i + len;
                // dp[i][i+len], len 从 0 到 n-1，表示每次计算一个子对角线
                if (j >= n) {
                    continue;
                }
                boolean same = s.charAt(i) == s.charAt(j);
                if (len == 0) {
                    palindromes[i][j] = 1;
                } else if (len == 1) {
                    palindromes[i][j] = same ? 1 : 0;
                } else {
                    boolean papa = same && palindromes[i + 1][j - 1] == 1;
                    palindromes[i][j] = papa ? 1 : 0;
                }
            }
        }

        return partitionFrom2(s, n, 0, new ArrayList<>(), palindromes);
    }

    public List<List<String>> partitionFrom2(String s, int n, int idx,
                                             List<String> keptOnes, int[][] palindromes) {
        if (idx == n) {
            return List.of(new ArrayList<>(keptOnes));
        }

        List<List<String>> res = new ArrayList<>();

        for (int i = idx; i < n; i++) {
            if (palindromes[idx][i] == 1) {
                String str = s.substring(idx, i + 1);
                keptOnes.add(str);
                res.addAll(partitionFrom2(s, n, i + 1, keptOnes, palindromes));
                keptOnes.removeLast();
            }
        }

        return res;
    }

    // endregion

    // region BruteForce

    // 暴力枚举法
    // 时间：O(2^N)
    public List<List<String>> partitionV1(String s) {
        return partitionFrom(s, 0, new ArrayList<>());
    }

    public List<List<String>> partitionFrom(String s, int idx, List<String> keptOnes) {
        List<List<String>> res = new ArrayList<>();

        List<String> all = allPalindromeFrom(s, idx);
        for (String start : all) {
            int newLen = start.length() + idx;
            if (newLen == s.length()) {
                ArrayList<String> one = new ArrayList<>(keptOnes);
                one.add(start);
                res.add(one);
            } else {
                keptOnes.add(start);
                res.addAll(partitionFrom(s, newLen, keptOnes));
                keptOnes.removeLast();
            }
        }
        return res;
    }

    public int isPalindromeRange(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return 0;
            }
            left++;
            right--;
        }
        return 1;
    }

    public List<String> allPalindromeFrom(String s, int index) {
        List<String> res = new ArrayList<>();
        for (int i = index; i < s.length(); i++) {
            if (isPalindromeRange(s, index, i) == 1) {
                res.add(s.substring(index, i + 1));
            }
        }
        return res;
    }

    // endregion
}
