package im.engure.binarytree.util;

import im.engure.binarytree.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BinaryTreeUtil {

    // ANSI escape codes
    private static final String[] COLORS = {
            "\033[31m",
            "\033[32m",
            "\033[33m",
            "\033[34m",
            "\033[35m",
            "\033[36m",
            "\033[37m"};
    // 用于记录最大下标值
    private static int maxIndex = 0;

    /**
     * 构造二叉树
     * // 用于记录最大下标值
     * private static int maxIndex = 0;
     * <p>
     * /**
     * 构造二叉树
     *
     * @param array 按照层序遍历的数据存储的数据
     */
    public static TreeNode buildBinaryTree(Integer[] array) {
        if (array == null || array.length == 0 || array[0] == null) {
            return null;
        }

        TreeNode[] nodes = new TreeNode[array.length];
        nodes[0] = new TreeNode(array[0]);

        // 利用完全二叉树的性质：对于下标为 i 的节点，其左子节点为索引 2*i+1，右子节点为索引 2*i+2
        for (int i = 0; i < array.length; i++) {
            if (nodes[i] == null) {
                continue;
            }
            int leftIndex = 2 * i + 1;
            int rightIndex = 2 * i + 2;
            if (leftIndex < array.length && array[leftIndex] != null) {
                nodes[leftIndex] = new TreeNode(array[leftIndex]);
                nodes[i].left = nodes[leftIndex];
            }
            if (rightIndex < array.length && array[rightIndex] != null) {
                nodes[rightIndex] = new TreeNode(array[rightIndex]);
                nodes[i].right = nodes[rightIndex];
            }
        }

        return nodes[0];
    }

    /**
     * @create-by o3-mini
     */
    private static void updateMaxIndex(TreeNode node, int index) {
        if (node == null) {
            return;
        }
        // 更新最大下标
        maxIndex = Math.max(maxIndex, index);
        updateMaxIndex(node.left, 2 * index + 1);
        updateMaxIndex(node.right, 2 * index + 2);
    }

    /**
     * @create-by o3-mini
     */
    private static void fillArray(TreeNode node, int index, Integer[] arr) {
        if (node == null) {
            return;
        }
        arr[index] = node.val;
        fillArray(node.left, 2 * index + 1, arr);
        fillArray(node.right, 2 * index + 2, arr);
    }

    /**
     * 将二叉树展平
     *
     * @return 不保留完整的结构信息。比如 <a href="https://leetcode.com/problems/add-one-row-to-tree/description/">...</a> 第一个示例中的 Output
     * @create-by o3-mini
     */
    public static Integer[] flatBinaryTreeV1(TreeNode root) {
        if (root == null) {
            return new Integer[0];
        }

        // 使用队列做层序遍历；放入 null 节点以保留结构信息
        Queue<TreeNode> queue = new LinkedList<>();
        List<Integer> list = new ArrayList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            TreeNode current = queue.poll();
            if (current != null) {
                list.add(current.val);
                // 不论子节点是否为 null，都放入队列
                queue.offer(current.left);
                queue.offer(current.right);
            } else {
                list.add(null);
            }
        }

        // 移除数组末尾的多余 null 节点
        int i = list.size() - 1;
        while (i >= 0 && list.get(i) == null) {
            i--;
        }
        List<Integer> trimmed = list.subList(0, i + 1);

        return trimmed.toArray(new Integer[0]);
    }

    /**
     * 将二叉树展平
     *
     * @return 保留完整的结构信息，即使某些位置没有节点也会显示为 null
     * @create-by o3-mini
     */
    public static Integer[] flatBinaryTree(TreeNode root) {
        if (root == null) {
            return new Integer[0];
        }

        // 第一次递归：确定数组所需的长度
        maxIndex = 0;  // 重置全局变量
        updateMaxIndex(root, 0);

        // 创建数组，长度为 (maxIndex + 1)，全部初始化为 null
        Integer[] result = new Integer[maxIndex + 1];
        // 默认数组中的元素即为 null，无需另外赋值

        // 第二次递归：填入节点值
        fillArray(root, 0, result);

        return result;
    }

    /**
     * 打印二叉树
     */
    public static void printBinaryTree(TreeNode root) {
        int ratio = maxLengthOfTreeNodePrintableWidth(root) + 3;
        System.out.println("------------------------");
        printBinaryTree(root, 0, "", ratio);
        System.out.println("------------------------");
    }

    private static int maxLengthOfTreeNodePrintableWidth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int max = String.valueOf(root.val).length();
        int left = maxLengthOfTreeNodePrintableWidth(root.left);
        int right = maxLengthOfTreeNodePrintableWidth(root.right);
        if (max < left) {
            max = left;
        }
        if (max < right) {
            max = right;
        }
        return max;
    }

    private static void printBinaryTree(TreeNode root, int level, String marker, int ratio) {
        if (root == null) {
            return;
        }

        //打印右子树
        printBinaryTree(root.right, level + 1, "/", ratio);

        //打印当前节点
        int len = ratio * level;
        String color = COLORS[level % COLORS.length];
        // RESET
        String RESET = "\033[0m";
        if (len == 0) {
            System.out.println(color + root.val + RESET);
        } else {
            String formatStr = color + "%s%s %-" + len + "s\n" + RESET;
            System.out.printf(formatStr, " ".repeat(len), marker, root.val);
        }

        //打印左子树
        printBinaryTree(root.left, level + 1, "\\", ratio);
    }
}

