package im.engure.backtrace;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 对有重复元素的数组进行全排列，结果不能包含重复解
 *
 * @author engure
 */
public class Permute47 {

    public static void main(String[] args) {
        System.out.println(new Permute47().permuteUnique(new int[]{1, 1, 2}));
    }

    List<List<Integer>> res = new LinkedList<>();
    int[] nums;

    LinkedList<Integer> track = new LinkedList<>();

    public List<List<Integer>> permuteUnique(int[] nums) {
        this.nums = nums;
        Arrays.sort(nums);
        process(new boolean[nums.length]);
        return res;
    }

    public void process(boolean[] used) {
        if (track.size() == nums.length) {
            res.add(new LinkedList<>(track));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) {
                continue;
            }
            //剪枝：剪去已经遍历过的路径
            if (i - 1 >= 0 && nums[i - 1] == nums[i] && !used[i - 1]) {
                continue;
            }
            track.add(nums[i]);
            used[i] = true;
            process(used);
            track.remove(track.size() - 1);
            used[i] = false;
        }
    }

}

// 1,2,2
// 1, 12, 122
// 2, 21, 212
//    22, 221
// 2
