package im.engure.recursive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author engure
 */
public class CombinationSum39 {

    int[] candidates;
    List<List<Integer>> res = new LinkedList<>();

    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        Arrays.sort(candidates);
        this.candidates = candidates;
        process(target, 0, new LinkedList<>());
        return res;
    }

    public void process(int target, int idx, List<Integer> list) {
        if (target == 0) {
            res.add(new LinkedList<>(list));
            return;
        }

        for (int i = idx; i < candidates.length; i++) {
            if (target >= candidates[i]) {
                list.add(candidates[i]);
                process(target - candidates[i], i, list);
                list.remove(list.size() - 1);
            } else {
                return;
            }
        }
    }
}
