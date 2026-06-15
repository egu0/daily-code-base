package im.engure.backtrace;

import java.util.LinkedList;
import java.util.List;

/**
 * 对不重复数组进行全排列
 *
 * @author engure
 */
public class Permute46 {
    List<List<Integer>> res = new LinkedList<>();
    int[] nums;

    public List<List<Integer>> permute(int[] nums) {
        this.nums = nums;
        process(new boolean[nums.length], new LinkedList<>());
        return res;
    }

    public void process(boolean[] used, List<Integer> path) {
        if (path.size() == nums.length) {
            res.add(new LinkedList<>(path));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) {
                continue;
            }
            path.add(nums[i]);
            used[i] = true;
            process(used, path);
            path.remove(path.size() - 1);
            used[i] = false;
        }
    }

}
