package im.engure.binarytree.leetcode;

import im.engure.binarytree.TreeNode;

import java.util.*;

/**
 * @author engure
 */
public class MinimumOperations2471 {
    public int minimumOperations(TreeNode root) {
        int ans = 0;
        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        while (!q.isEmpty()) {
            int n = q.size();
            int[] nums = new int[n];
            int i = 0;
            while (n-- > 0) {
                TreeNode node = q.poll();
                if (node != null) {
                    nums[i++] = node.val;
                    if (node.left != null) q.add(node.left);
                    if (node.right != null) q.add(node.right);
                }
            }
            ans += process(nums);
        }
        return ans;
    }

    /**
     * hashmap + 数组。可 ac
     */
    public int process2(int[] arr) {
        int len = 0;
        for (int i = 0; i < arr.length && arr[i] != 0; i++) len++;
        if (len <= 1) return 0;

        int ans = 0;
        int[] srt = new int[len];
        Map<Integer, Integer> idx = new HashMap<>(len);
        for (int i = 0; i < len; i++) {
            idx.put(arr[i], i);
            srt[i] = arr[i];
        }

        Arrays.sort(srt);

        for (int i = 0; i < len; i++) {
            if (srt[i] == arr[i]) continue;

            int targetIdx = idx.get(srt[i]);
            swap(idx, arr[i], srt[i]);
            swap(arr, i, targetIdx);
            ans++;
        }

        for (int j : arr) System.out.print(j + ", ");
        System.out.println("]");

        return ans;
    }

    /**
     * 优先队列（堆排序思想）。超时
     */
    public int process(int[] arr) {
        if (arr.length <= 1) return 0;
        int ans = 0;
        PriorityQueue<int[]> queue = new PriorityQueue<>(Comparator.comparingInt(o -> o[0]));
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 0) break;

            queue.add(new int[]{arr[i], i});
        }

        for (int i = 0; i < arr.length && !queue.isEmpty(); i++) {
            if (arr[i] == 0) break;

            int[] peek = queue.poll();
            if (peek[0] != arr[i]) {
                int finalI = i;
                //时间问题
                queue.removeIf(tpl -> tpl[0] == arr[finalI]);
                queue.add(new int[]{arr[i], peek[1]});
                swap(arr, i, peek[1]);
                ans++;
            }
        }

        return ans;
    }

    public void swap(Map<Integer, Integer> idx, int i, int j) {
        int t = idx.get(j);
        idx.put(j, idx.get(i));
        idx.put(i, t);
    }

    public void swap(int[] arr, int i, int j) {
        int t = arr[j];
        arr[j] = arr[i];
        arr[i] = t;
    }
}

// 8,5,7,6
// 3，2，4，1
// 3，1，5，4，2
// 1，3
// 1，2，5，4，3
// 1，2，3，4，5

