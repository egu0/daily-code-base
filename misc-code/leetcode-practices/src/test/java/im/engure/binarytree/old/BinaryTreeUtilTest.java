package im.engure.binarytree.old;

import im.engure.binarytree.TreeNode;
import im.engure.binarytree.util.BinaryTreeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class BinaryTreeUtilTest {

    @Test
    void t1() {
        /*
        100
       /    \
      0     150
    /  \    /  \
  -22  50 120   55555555
                 /
               180
         */
        Integer[] arr = {100, 0, 150, -22, 50, 120, 55555555, null, null, null, null, null, null, 180};
        TreeNode tree = BinaryTreeUtil.buildBinaryTree(arr);
        BinaryTreeUtil.printBinaryTree(tree);
        Assertions.assertEquals(100, tree.val);
        Assertions.assertEquals(0, tree.left.val);
        Assertions.assertEquals(150, tree.right.val);
        Assertions.assertEquals(-22, tree.left.left.val);
        Assertions.assertNull(tree.left.left.left);
        Assertions.assertNull(tree.left.left.right);
        Assertions.assertEquals(50, tree.left.right.val);
        Assertions.assertEquals(120, tree.right.left.val);
        Assertions.assertEquals(55555555, tree.right.right.val);
        Assertions.assertEquals(180, tree.right.right.left.val);
        Assertions.assertNull(tree.right.right.right);
    }

    @Test
    void t3() {
        /*
         1
       /   \
      2      3
     / \    /
    4   5  6
         */
        Integer[] arr = {1, 2, 3, 4, 5, 6};
        TreeNode tree = BinaryTreeUtil.buildBinaryTree(arr);
        Assertions.assertEquals(1, tree.val);
        Assertions.assertEquals(2, tree.left.val);
        Assertions.assertEquals(3, tree.right.val);
        Assertions.assertEquals(4, tree.left.left.val);
        Assertions.assertEquals(5, tree.left.right.val);
        Assertions.assertEquals(6, tree.right.left.val);
        Assertions.assertNull(tree.right.right);
    }

    @Test
    void t2() {
        Integer[] arr = {100, 0, 150, -22, 50, 120, 55555555, null, null, null, null, null, null, 180};
        TreeNode tree = BinaryTreeUtil.buildBinaryTree(arr);
        Integer[] res = BinaryTreeUtil.flatBinaryTree(tree);
        for (int i = 0; i < arr.length; i++) {
            Assertions.assertEquals(arr[i], res[i]);
        }
    }

    @Test
    void t4() {
        Integer[] arr = {1, 2, 3, 4, 5, 6};
        TreeNode tree = BinaryTreeUtil.buildBinaryTree(arr);
        Integer[] res = BinaryTreeUtil.flatBinaryTree(tree);
        for (int i = 0; i < arr.length; i++) {
            Assertions.assertEquals(arr[i], res[i]);
        }
    }

    @Test
    void t5() {
        /*
         1
       /
      2
     / \
    4   5
         \
          6
         */
        Integer[] arr = {1, 2, null, 4, 5, null, null, null, null, null, 6};
        TreeNode tree = BinaryTreeUtil.buildBinaryTree(arr);
        BinaryTreeUtil.printBinaryTree(tree);
        Assertions.assertEquals(1, tree.val);
        Assertions.assertEquals(2, tree.left.val);
        Assertions.assertEquals(4, tree.left.left.val);
        Assertions.assertEquals(5, tree.left.right.val);
        Assertions.assertEquals(6, tree.left.right.right.val);

        Integer[] res = BinaryTreeUtil.flatBinaryTree(tree);
        System.out.println(Arrays.toString(arr));
        System.out.println(Arrays.toString(res));
        for (int i = 0; i < arr.length; i++) {
            Assertions.assertEquals(arr[i], res[i]);
        }
    }

}