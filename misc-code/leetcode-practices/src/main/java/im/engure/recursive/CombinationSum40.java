package im.engure.recursive;

import java.util.*;

/**
 * 在 39 的基础上进一步约束
 * 1。输入可能重复
 * 2。每个数字只能使用一次
 * 3。解集不能包含重复值
 *
 * @author engure
 */
public class CombinationSum40 {

    public static void main(String[] args) {
        System.out.println(new CombinationSum40().combinationSum2(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, 30));
    }

    int[] candidates;
    List<List<Integer>> res = new LinkedList<>();

    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
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
            if (i - 1 >= idx && candidates[i] == candidates[i - 1]) {
                continue;
            }
            if (target >= candidates[i]) {
                list.add(candidates[i]);
                process(target - candidates[i], i + 1, list);
                list.remove(list.size() - 1);
            } else {
                return;
            }
        }
    }
}
