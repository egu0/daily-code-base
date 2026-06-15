package im.engure.binarytree.old;

import im.engure.binarytree.TreeNode;
import im.engure.binarytree.util.BinaryTreeUtil;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * 序列化：TreeNode ==> Queue(TreeNode)
 * 反序列化：Queue(TreeNode) ==> TreeNode
 */
public class SerializeAndDeSerialize {

    public static void main(String[] args) {
        TreeNode h = new TreeNode(100);
        TreeNode n1 = new TreeNode(0);
        TreeNode n2 = new TreeNode(150);
        TreeNode n3 = new TreeNode(-222222);
        TreeNode n4 = new TreeNode(50);
        h.left = n1;
        n1.left = n3;
        n1.right = n4;
        TreeNode n5 = new TreeNode(120);
        TreeNode n6 = new TreeNode(55555555);
        TreeNode n7 = new TreeNode(180);
        h.right = n2;
        n2.left = n5;
        n2.right = n6;
        n6.left = n7;

        Queue<TreeNode> q = levelSerial(h);
        System.out.println(q);
        TreeNode h2 = levelDeSerial(q);
        BinaryTreeUtil.printBinaryTree(h2);
    }

    // 递归序列化
    private static Queue<TreeNode> preSerial(TreeNode h) {
        Queue<TreeNode> ans = new LinkedList<>();
        pre(h, ans);
        return ans;
    }

    private static void pre(TreeNode h, Queue<TreeNode> ans) {
        ans.add(h);
        if (h != null) {
            pre(h.left, ans);
            pre(h.right, ans);
        }
    }

    // 非递归实现序列化
    private static void preSerial2(TreeNode h) {
        if (h != null) {
            Stack<TreeNode> stack = new Stack<>();
            Queue<TreeNode> queue = new LinkedList<>();
            while (!stack.empty() || h != null) {
                if (h != null) {
                    stack.push(h);
                    queue.add(h);
                    h = h.left;
                } else {
                    queue.add(null);
                    TreeNode n = stack.pop();
                    h = n.right;
                }
            }
            queue.add(null);
            System.out.println(queue);
        }
    }

    // 通过前序遍历序列构造树
    private static TreeNode preDeSerial(Queue<TreeNode> queue) {
        TreeNode n = queue.poll();
        if (n != null) {
            n.left = preDeSerial(queue);
            n.right = preDeSerial(queue);
        }
        return n;
    }

    // 后序 递归 序列化 和 反序列化
    private static Queue<TreeNode> postSerial(TreeNode h) {
        Queue<TreeNode> ans = new LinkedList<>();
        post(h, ans);
        return ans;
    }

    private static void post(TreeNode h, Queue<TreeNode> ans) {
        if (h == null) {
            ans.add(null);
        } else {
            post(h.left, ans);
            post(h.right, ans);
            ans.add(h);
        }
    }

//    private static TreeNode postDeSerial(Queue<TreeNode>TreeNode queue) {
//		后续的反序列化？ 队头为null，不能反序列化。
//        return null;
//    }

    // ========================

    // 层序遍历序列化 非递归方法
    // 层序遍历的改进：考虑空位
    private static Queue<TreeNode> levelSerial(TreeNode h) {
        Queue<TreeNode> ans = null;
        if (h != null) {
            Queue<TreeNode> queue = new LinkedList<>();
            ans = new LinkedList<>();

            queue.add(h);
            ans.add(h);
            while (!queue.isEmpty()) {
                TreeNode n = queue.poll();
                ans.add(n.left);
                ans.add(n.right);
                if (n.left != null) {
                    queue.add(n.left);
                }
                if (n.right != null) {
                    queue.add(n.right);
                }
            }
        }
        return ans;
    }

    // 层序遍历反序列化 非递归方式
    private static TreeNode levelDeSerial(Queue<TreeNode> queue) {
        if (queue == null || queue.isEmpty()) {
            return null;
        }
        return deSerial(queue);
    }

    private static TreeNode deSerial(Queue<TreeNode> queue) {
        if (queue == null || queue.isEmpty()) {
            return null;
        }
        TreeNode h = generateNode(queue.poll());
        Queue<TreeNode> q2 = new LinkedList<>();
        q2.add(h);
        while (!q2.isEmpty()) {
            TreeNode n = q2.poll();
            n.left = generateNode(queue.poll());
            n.right = generateNode(queue.poll());
            if (n.left != null) {
                q2.add(n.left);
            }
            if (n.right != null) {
                q2.add(n.right);
            }
        }
        return h;
    }

    private static TreeNode generateNode(TreeNode n) {
        TreeNode node = null;
        if (n != null) {
            node = new TreeNode(n.val);
        }
        return node;
    }

}







