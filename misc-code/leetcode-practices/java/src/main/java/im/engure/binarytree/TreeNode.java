package im.engure.binarytree;

public class TreeNode {
    public TreeNode left = null, right = null;
    public int val;

    public TreeNode(int v, TreeNode l, TreeNode r) {
        val = v;
        this.left = l;
        this.right = r;
    }

    public TreeNode(int v) {
        val = v;
    }
}
