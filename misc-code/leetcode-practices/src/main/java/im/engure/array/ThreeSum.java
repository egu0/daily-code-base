package im.engure.array;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 三数之和
 */

public class ThreeSum {

    /**
     * 三数之和，时间复杂度是 O(N^2)
     *
     * @param nums
     * @param tttt
     * @return
     */
    public static List<List<Integer>> threeSumImpl(int[] nums, boolean tttt) {

        List<List<Integer>> res = new ArrayList<>();
        int LEN;
        if (null == nums || (LEN = nums.length) < 3) return res;
        Arrays.sort(nums);

        for (int i = 0; i < LEN; i++) {
            //最小值>0，那么不满足三数之和为零
            if (nums[i] > 0) break;
            //重复元素
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            //两数之和
            int l = i + 1, r = LEN - 1;
            while (l < r) {
                int sum = nums[l] + nums[r] + nums[i];
                if (sum == 0) {
                    res.add(Arrays.asList(nums[l], nums[r], nums[i]));
                    while (l < LEN - 1 && nums[l + 1] == nums[l]) l++;
                    while (r > 0 && nums[r - 1] == nums[r]) r--;
                    l++;
                    r--;
                } else if (sum > 0) {
                    while (r > 0 && nums[r - 1] == nums[r]) r--;
                    r--;
                } else {
                    while (l < LEN - 1 && nums[l + 1] == nums[l]) l++;
                    l++;
                }
            }
        }
        return res;
    }

    /**
     * 两数之和，复杂度 O(N)，前提是排好序的
     *
     * @param nums   已经排序的数组
     * @param start  开始的位置！
     * @param target 目标值
     * @return 所有两数之和
     */
    public static List<List<Integer>> twoSum(int[] nums, int start, int target) {
        List<List<Integer>> l0 = new ArrayList<>();
        int LEN = nums.length;
        int l = start, r = LEN - 1;
        while (l < r) {
            int sum = nums[l] + nums[r];
            if (sum == target) {
                l0.add(Arrays.asList(nums[l], nums[r]));
                while (l < LEN - 1 && nums[l + 1] == nums[l]) l++;
                while (r > 0 && nums[r - 1] == nums[r]) r--;
                l++;
                r--;
            } else if (sum > target) {
                while (r > 0 && nums[r - 1] == nums[r]) r--;
                r--;
            } else {
                while (l < LEN - 1 && nums[l + 1] == nums[l]) l++;
                l++;
            }
        }
        return l0;
    }

    /**
     * 先计算两数之和O(N^2)将结果存入 map<value, list<list>> ，再进行一次遍历
     * 太麻烦，不是很好的选择
     *
     * @param nums
     * @param tttt
     * @return
     */
    public static List<List<Integer>> threeSumImpl(int[] nums, int tttt) {

        Map<Integer, List<List<Integer>>> twoSumMap = new HashMap<>();
        List<List<Integer>> res = new ArrayList<>();

        int len = nums.length;

        Arrays.sort(nums);

        //计算所有两数之和
        for (int i = 0; i < len - 1; i++) {
            for (int j = i + 1; j < len; j++) {
                if (i > 0 && nums[i] == nums[i - 1]) continue;
                if (j > i + 1 && nums[j] == nums[j - 1]) continue;

                int v = nums[i] + nums[j];
                List<List<Integer>> list = twoSumMap.get(v);
                if (null == list) {
                    list = new ArrayList<>();
                }
                AtomicBoolean exists = new AtomicBoolean(false);
                int finalI = i;
                int finalJ = j;
                list.forEach(twoNumList -> {
                    int v0 = twoNumList.get(0);
                    int v1 = twoNumList.get(1);
                    if (v0 == nums[finalI] && v1 == nums[finalJ]) {
                        exists.set(true);
                    } else if (v1 == nums[finalI] && v0 == nums[finalJ]) {
                        exists.set(true);
                    }
                });
                if (!exists.get()) {
                    list.add(Arrays.asList(nums[i], nums[j]));
                }
                twoSumMap.put(v, list);
            }
        }

        System.out.println(twoSumMap);

        //排除twoSumMap中重复的元素
        for (Integer v : twoSumMap.keySet()) {
            List<List<Integer>> list = twoSumMap.get(v);
            for (List<Integer> twoNumList : list) {
                int v0 = twoNumList.get(0);
                int v1 = twoNumList.get(1);
                if (v0 != v) {
                    List<List<Integer>> list_v0 = twoSumMap.get(v0);
                    for (List<Integer> list_v : list_v0) {
                        int tmp1 = list_v.get(0);
                        int tmp0 = list_v.get(1);
                        if (tmp1 == v || tmp0 == v) list_v0.remove(list_v);
                    }
                }
                if (v1 != v) {
                    List<List<Integer>> list_v1 = twoSumMap.get(v1);
                    for (List<Integer> list_v : list_v1) {
                        int tmp1 = list_v.get(0);
                        int tmp0 = list_v.get(1);
                        if (tmp1 == v || tmp0 == v) list_v1.remove(list_v);
                    }
                }
            }
        }

        System.out.println(twoSumMap);

        for (int i = 0; i < len; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            List<List<Integer>> list = twoSumMap.get(-nums[i]);
            if (null == list) continue;
            for (List<Integer> twoNumList : list) {
                List<Integer> twoNumList2 = new ArrayList<>(twoNumList);
                twoNumList2.add(nums[i]);
                res.add(twoNumList2);
            }
        }

        System.out.println(res);

        // 去重不到位

        return null;
    }


    /**
     * 三数之和暴力法
     *
     * @param nums
     * @return
     */
    public static List<List<Integer>> threeSumImpl(int[] nums) {

        List<List<Integer>> res = new ArrayList<>();

        int len;

        if (nums == null || (len = nums.length) < 3) return res;
        Arrays.sort(nums);

        for (int i = 0; i < len - 2; i++) {
            for (int j = i + 1; j < len - 1; j++) {
                for (int k = j + 1; k < len; k++) {

                    //去重
                    if (i > 0 && nums[i] == nums[i - 1]) continue;
                    if (j > i + 1 && nums[j] == nums[j - 1]) continue;
                    if (k > j + 1 && nums[k] == nums[k - 1]) continue;

                    if (nums[i] + nums[j] + nums[k] == 0) {
                        res.add(Arrays.asList(nums[i], nums[j], nums[k]));
                    }
                }
            }
        }

        return res;
    }

    public static void main(String[] args) {
        int[] nums = {-1, 0, 1, 0, 2, -1, -4};//-4,-1,-1,0,0,1,2
//        int[] nums = {0, 0, 0, 0, 0};
        List<List<Integer>> res = threeSumImpl(nums, true);
        System.out.println(res);
    }

}
