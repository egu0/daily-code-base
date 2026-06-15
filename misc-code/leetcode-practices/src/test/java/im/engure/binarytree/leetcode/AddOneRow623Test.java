package im.engure.binarytree.leetcode;

import im.engure.binarytree.TreeNode;
import im.engure.binarytree.util.BinaryTreeUtil;
import im.engure.util.ArrayAssertions;
import org.junit.jupiter.api.Test;

class AddOneRow623Test {

    @Test
    void t1() {
        AddOneRow623 o = new AddOneRow623();

        TreeNode tree = BinaryTreeUtil.buildBinaryTree(new Integer[]{4, 2, 6, 3, 1, 5});
        TreeNode resTree = o.addOneRow(tree, 1, 2);

        Integer[] result = BinaryTreeUtil.flatBinaryTreeV1(resTree);
        Integer[] expected = {4, 1, 1, 2, null, null, 6, 3, 1, 5};

        ArrayAssertions.assertEqual(expected, result);
    }

    @Test
    void t2() {
        AddOneRow623 o = new AddOneRow623();

        TreeNode tree = BinaryTreeUtil.buildBinaryTree(new Integer[]{4, 2, null, 3, 1});
        TreeNode resTree = o.addOneRow(tree, 1, 3);

        Integer[] result = BinaryTreeUtil.flatBinaryTreeV1(resTree);
        Integer[] expected = {4, 2, null, 1, 1, 3, null, null, 1};

        ArrayAssertions.assertEqual(expected, result);
    }

    @Test
    void t3() {
        AddOneRow623 o = new AddOneRow623();

        TreeNode tree = BinaryTreeUtil.buildBinaryTree(new Integer[]{4});
        TreeNode resTree = o.addOneRow(tree, 1, 1);

        Integer[] result = BinaryTreeUtil.flatBinaryTreeV1(resTree);
        Integer[] expected = {1, 4};

        ArrayAssertions.assertEqual(expected, result);
    }

    @Test
    void t4() {
        AddOneRow623 o = new AddOneRow623();

        TreeNode tree = BinaryTreeUtil.buildBinaryTree(new Integer[]{4});
        TreeNode resTree = o.addOneRow(tree, 1, 2);

        Integer[] result = BinaryTreeUtil.flatBinaryTreeV1(resTree);
        Integer[] expected = {4, 1, 1};

        ArrayAssertions.assertEqual(expected, result);
    }
}