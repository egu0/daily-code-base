package im.engure.binarytree.leetcode;

import im.engure.binarytree.TreeNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LowestCommonAncestorInterview0408 {

    public static void main(String[] args) {
        LowestCommonAncestorInterview0408 o = new LowestCommonAncestorInterview0408();

        TreeNode l4_1 = new TreeNode(7, null, null);
        TreeNode l4_2 = new TreeNode(4, null, null);

        TreeNode l3_1 = new TreeNode(6, null, null);
        TreeNode l3_2 = new TreeNode(2, l4_1, l4_2);
        TreeNode l3_3 = new TreeNode(0, null, null);
        TreeNode l3_4 = new TreeNode(8, null, null);

        TreeNode l2_1 = new TreeNode(5, l3_1, l3_2);
        TreeNode l2_2 = new TreeNode(1, l3_3, l3_4);
        TreeNode root = new TreeNode(3, l2_1, l2_2);

        int p = 5;
        int q = 4;
        System.out.println(o.lowestCommonAncestor(root, new TreeNode(p, null, null), new TreeNode(q, null, null)).val);

    }

    /**
     * TODO 根据【题目提示】继续优化
     */
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {

        return null;
    }

    /**
     * PASSED
     * 回溯法遍历两次
     * 8ms、42Mb
     */
    public TreeNode lowestCommonAncestor2(TreeNode root, TreeNode p, TreeNode q) {
        List<TreeNode> p1 = new ArrayList<>();
        List<TreeNode> p2 = new ArrayList<>();

        backtrace(root, p, p1);
        backtrace(root, q, p2);

        int minLen = Math.min(p1.size(), p2.size());
        int i = 1;
        for (; i < minLen; i++) {
            if (p1.get(i).val != p2.get(i).val) {
                return p1.get(i - 1);
            }
        }
        if (i == minLen) {
            return p1.get(i - 1);
        }

        return null;
    }

    private boolean backtrace(TreeNode root, TreeNode targetNode, List<TreeNode> curPath) {
        if (root == null) {
            return false;
        }

        curPath.add(root);

        if (targetNode.val == root.val) {
            return true;
        }

        //遍历左枝
        if (backtrace(root.left, targetNode, curPath)) {
            return true;
        }

        //遍历右枝，可能没找到
        if (backtrace(root.right, targetNode, curPath)) {
            return true;
        } else {
            curPath.remove(root);
            return false;
        }
    }

    /**
     * 内存超限!
     * pass/total: 29/31
     */
    public TreeNode lowestCommonAncestor1(TreeNode root, TreeNode p, TreeNode q) {
        List<TreeNode> path1 = new ArrayList<>();
        List<TreeNode> path2 = new ArrayList<>();

        getPathByRecursion(root, p, new ArrayList<>(), path1);
        getPathByRecursion(root, q, new ArrayList<>(), path2);

        Set<Integer> s1 = new HashSet<>();
        for (TreeNode node : path1) {
            s1.add(node.val);
        }
        for (int i = path2.size() - 1; i >= 0; i--) {
            if (s1.contains(path2.get(i).val)) {
                return path2.get(i);
            }
        }

        return null;
    }

    private void getPathByRecursion(TreeNode cur, TreeNode target, List<TreeNode> curPath,
                                    List<TreeNode> targetPath) {
        if (!targetPath.isEmpty() || cur == null) {
            return;
        }

        curPath.add(cur);
        if (cur.val == target.val) {
            targetPath.addAll(curPath);
        } else {
            getPathByRecursion(cur.left, target, new ArrayList<>(curPath), targetPath);
            getPathByRecursion(cur.right, target, new ArrayList<>(curPath), targetPath);
        }
    }
}
