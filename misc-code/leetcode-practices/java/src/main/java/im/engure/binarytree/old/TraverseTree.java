package im.engure.binarytree.old;

import im.engure.binarytree.TreeNode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

// 一般递归遍历（前中后）
// 递归序【重要】
// 非递归遍历（前中后）
// 层序遍历、统计树宽

public class TraverseTree {

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

//		getMaxWidth(h);
        getMaxWidth2(h);


        //printRecursionSequence(h);

        /*
         * System.out.print("pre order:"); preOrder(h); System.out.println();
         *
         * System.out.print("in order:"); inOrder(h); System.out.println();
         *
         * System.out.print("post order: "); postOrder(h); System.out.println();
         *
         * System.out.print("level order:"); levelOrder(h); System.out.println();
         */

//		System.out.print("pre non-recursion:");
//		preNonRecursion(h);
//		System.out.println();
//
//		System.out.print("in non-recursion:");
//		inNonRecursion(h);
//		System.out.println();
//
//		System.out.print("post non-recursion:");
//		postNonRecursion(h);
//		System.out.println();
    }

    // 后续非递归 双栈法
    private static void postNonRecursion(TreeNode h) {
        if (h != null) {
            // 头右左 遍历序列的逆序结果

            //1.将整个右边界从上到下节点的值入stack2，节点入stack
            //2.弹栈stack，对弹出节点的左子树重复 1.

            //stack2：100 150 55555 180 120 0 50 -22222，倒序为后序遍历
            Stack<TreeNode> stack = new Stack<>();
            Stack<Integer> stack2 = new Stack<>();
            while (!stack.empty() || h != null) {
                if (h != null) {
                    stack2.push(h.val);
                    stack.push(h);
                    h = h.right;
                } else {
                    TreeNode n = stack.pop();
                    h = n.left;
                }

            }
            while (!stack2.empty()) {
                System.out.print(stack2.pop() + " ");
            }
        }

        // 头 右 左 遍历 （左老师）
        //1.弹打印
        //2.如有左则压入左节点
        //3.如有右则压入右节点
    }

    // 单栈后继遍历（巧妙地利用指针、卡逻辑）
    private static void pos2(TreeNode h) {
        // 双指针
        // 先处理左树、再处理右树、最后处理头节点

        System.out.println("pos-order: ");
        if (h != null) {
            Stack<TreeNode> stack = new Stack<>();
            stack.push(h);
            TreeNode c;
            while (!stack.empty()) {
                c = stack.peek();// 栈顶

                // c.left不为空、h不是c子节点
                if (c.left != null && h != c.left && h != c.right) {
                    stack.push(c.left);
                    // c.right不为空、h不是c右节点
                } else if (c.right != null && h != c.right) {
                    stack.push(c.right);
                    // c为叶子节点
                } else {
                    System.out.println(stack.pop().val + " ");
                    h = c;
                }
            }
        }
        System.out.println();
    }

    private static void pos3(TreeNode h) {
        // 不使用 双指针是否可实现？

        if (h != null) {
            Stack<TreeNode> stack = new Stack<>();
            stack.push(h);
            while (!stack.empty()) {
                if (h.left != null) {
                    stack.push(h.left);
                    h = h.left;
                } else if (h.right != null) {
                    stack.push(h.right);
                    h = h.right;
                } else {
                    if (stack.size() >= 2) {
                        TreeNode n = stack.pop();
                        System.out.println(n.val + " ");
                        TreeNode m = stack.peek();

                        // 此时打印节点并弹栈，如何进行标记 才能不进入死循环？

                        if (n == m.left) {
                            h = m.right;
                        } else if (n == m.right) {
                            h = m;
                        }
                    }
                }
            }

        }

    }

    // 中序非递归
    private static void inNonRecursion(TreeNode h) {
        // 左头右
        // 1.整条左边界入栈
        // 2.左边界到头1.无法继续，则弹栈打印，来到右树继续执行1.
        if (h != null) {
            Stack<TreeNode> stack = new Stack<>();
            while (!stack.empty() || h != null) {
                if (h != null) {
                    stack.push(h);
                    h = h.left;
                } else {
                    TreeNode n = stack.pop();
                    System.out.print(n.val + " ");
                    h = n.right;
                }
            }
        }


    }

    // 前序非递归
    private static void preNonRecursion(TreeNode h) {
        // 头左右
        //1.左边界依次打印入栈
        //2.当1.不能继续时弹栈，对右子树执行1.
        if (h != null) {
            Stack<TreeNode> stack = new Stack<>();
            while (!stack.empty() || h != null) {
                if (h != null) {
                    System.out.print(h.val + " ");
                    stack.push(h);
                    h = h.left;
                } else {
                    TreeNode n = stack.pop();
                    h = n.right;
                }

            }
        }

        // 方法二（左老师）
        //1.弹打印
        //2.如有右则压入右节点
        //3.如有左则压入左节点
    }


    // 层级遍历
    private static void levelOrder(TreeNode h) {
        LinkedList<TreeNode> q = new LinkedList<>();
//		q.pop();  删除首部第一个元素
//		q.add(e); 向尾部添加一个元素
        q.add(h);
        while (!q.isEmpty()) {
            TreeNode n = q.pop();
            System.out.print(n.val + " ");
            if (n.left != null) {
                q.add(n.left);
            }
            if (n.right != null) {
                q.add(n.right);
            }
        }
    }

    // 求最大树宽（层序遍历、切入点：当前层的末尾节点）
    private static int getMaxWidth(TreeNode h) {
        if (h != null) {
            Queue<TreeNode> queue = new LinkedList<>();
            queue.add(h);
            TreeNode curEnd = h;        // 当前行末尾节点
            TreeNode nextEnd = null;    // 下一行末尾节点
            int curLevelNodeNum = 0;// 当前行节点数
            int max = 0;            // 最大节点数

            while (!queue.isEmpty()) {
                TreeNode n = queue.poll();
                if (n.left != null) {
                    queue.add(n.left);
                    nextEnd = n.left;
                }
                if (n.right != null) {
                    queue.add(n.right);
                    nextEnd = n.right;
                }
                curLevelNodeNum++;
                if (curEnd == n) {
                    max = Math.max(curLevelNodeNum, max);
                    curEnd = nextEnd;
                    curLevelNodeNum = 0;
                }
            }
            System.out.println("max = " + max);
        }
        return 0;
    }

    private static void getMaxWidth2(TreeNode h) {
        if (h != null) {
            HashMap<TreeNode, Integer> map = new HashMap<>();
            Queue<TreeNode> queue = new LinkedList<>();
            map.put(h, 1);
            queue.add(h);
            int curLevel = 1;            // 当前第几层
            int curLevelNodeNum = 0;    // 当前层节点数
            int max = 0;                // 最大节点数
            while (!queue.isEmpty()) {
                TreeNode n = queue.poll();
                Integer levelNum = map.get(n);
                if (n.left != null) {
                    queue.add(n.left);
                    map.put(n.left, levelNum + 1);
                }
                if (n.right != null) {
                    queue.add(n.right);
                    map.put(n.right, levelNum + 1);
                }
                if (levelNum == curLevel) {
                    curLevelNodeNum++;
                } else {
                    curLevel++;
                    max = Math.max(max, curLevelNodeNum);
                    curLevelNodeNum = 1;
                }
            }
            // 最后一层更新
            max = Math.max(max, curLevelNodeNum);
            System.out.println("max = " + max);
        }
    }


    // 递归方式
    private static void postOrder(TreeNode h) {
        if (h == null) {
            return;
        }

        postOrder(h.left);
        postOrder(h.right);
        System.out.print(h.val + " ");
    }

    private static void inOrder(TreeNode h) {
        if (h == null) {
            return;
        }
        inOrder(h.left);
        System.out.print(h.val + " ");
        inOrder(h.right);
    }

    private static void preOrder(TreeNode h) {
        if (h == null) {
            return;
        }

        System.out.print(h.val + " ");
        preOrder(h.left);
        preOrder(h.right);
    }


    /**
     * 打印 【递归序】
     *
     * @param h 头节点
     */
    private static void printRecursionSequence(TreeNode h) {
        if (h == null) {
            return;
        }
        //1.
        System.out.println(h.val + " [1]");
        //2.
        printRecursionSequence(h.left);
        System.out.println(h.val + " [2]");
        //3.
        printRecursionSequence(h.right);
        System.out.println(h.val + " [3]");
    }


}
