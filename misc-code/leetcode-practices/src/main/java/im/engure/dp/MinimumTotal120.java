package im.engure.dp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public class MinimumTotal120 {
    public int minimumTotal(List<List<Integer>> triangle) {
        int len = triangle.size();
        for (int i = len - 2; i >= 0; i--) {
            for (int j = 0; j < triangle.get(i).size(); j++) {
                int n = triangle.get(i).get(j) + Math.min(triangle.get(i + 1).get(j), triangle.get(i + 1).get(j + 1));
                triangle.get(i).set(j, n);
            }
        }
        return triangle.get(0).get(0);
    }

    // region memorization

    Map<String, Integer> note = new HashMap<>();

    public int minimumTotal2(List<List<Integer>> triangle) {
        return minBelow(0, triangle, 0);
    }

    public int minBelow(int floor, List<List<Integer>> triangle, int idx) {
        int height = triangle.size();
        if (floor >= height) {
            return 0;
        }

        String key = floor + ":" + idx;
        if (note.containsKey(key)) {
            return note.get(key);
        }
        int n0 = triangle.get(floor).get(idx);
        int n1 = n0 + minBelow(floor + 1, triangle, idx);
        int n2 = n0 + minBelow(floor + 1, triangle, idx + 1);
        int minN = Math.min(n1, n2);
        note.put(key, minN);
        return minN;
    }

    // endregion
}
