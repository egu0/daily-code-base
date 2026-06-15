package im.engure.recursive;


import java.util.HashSet;
import java.util.Set;

/*
经典递归题目
 */

public class NumTilePossibilities1079 {
    public int numTilePossibilities(String tiles) {
        int n = tiles.length();
        Set<Integer> uniqueTiles = new HashSet<>();
        int[] count = new int[27]; // 'A' -> count[1] ... 'Z' -> count[26]
        for (int i = 0; i < n; i++) {
            count[tiles.charAt(i) & 31]++;
        }
        for (int i = 0; i < count.length; i++) {
            if (count[i] > 0) {
                uniqueTiles.add(i);
            }
        }

        int res = 0;
        for (int i = 1; i <= n; i++) {
            res += numTilePossibilities(i, uniqueTiles, count);
        }
        return res;
    }

    private int numTilePossibilities(int remaining, Set<Integer> allUniqueTiles, int[] count) {

        if (remaining == 0) {
            return 0;
        }

        int res = 0;
        for (Integer tile : allUniqueTiles) {
            if (count[tile] > 0) {
                if (remaining == 1) {
                    res += 1;
                } else {
                    count[tile]--;
                    res += numTilePossibilities(remaining - 1, allUniqueTiles, count);
                    count[tile]++;
                }
            }
        }

        return res;
    }
}
