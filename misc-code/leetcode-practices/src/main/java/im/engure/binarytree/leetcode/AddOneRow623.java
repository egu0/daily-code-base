package im.engure.binarytree.leetcode;

import im.engure.binarytree.TreeNode;

public class AddOneRow623 {

    public TreeNode addOneRow(TreeNode root, int val, int depth) {
        if (depth == 1) {
            return new TreeNode(val, root, null);
        }
        return process(root, val, depth, 1);
    }

    private TreeNode process(TreeNode root, int val, int depth, int curDepth) {
        int nextDepth = curDepth + 1;
        if (nextDepth == depth) {
            root.left = new TreeNode(val, root.left, null);
            root.right = new TreeNode(val, null, root.right);
        } else if (nextDepth < depth) {
            if (root.left != null) {
                process(root.left, val, depth, nextDepth);
            }
            if (root.right != null) {
                process(root.right, val, depth, nextDepth);
            }
        }
        return root;
    }
}
