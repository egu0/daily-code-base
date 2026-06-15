use std::cell::RefCell;
use std::rc::Rc;
use std::vec;
fn main() {
    Solution::inorder_traversal2(None);
}
#[allow(dead_code)]
struct Solution {}
/*
        1
         \
          \
           \
            \
             2
            /
           3
    ------------------
        1
      /  \
     3    7
    / \    \
   4   9    5
*/
impl Solution {
    //Iteration
    #[allow(dead_code)]
    pub fn inorder_traversal2(root: Option<Rc<RefCell<TreeNode>>>) -> Vec<i32> {
        if root.is_none() {
            return vec![];
        }

        //初始化
        let mut res_vec: Vec<i32> = vec![];
        let mut stack = vec![root.unwrap()];
        Solution::add_all_left_node(&mut stack);

        /*
        while !stack.is_empty() {
            let node = stack.pop().unwrap();
        */
        while let Some(node) = stack.pop() {
            res_vec.push(node.borrow().val);

            let right = node.borrow().right.clone();
            if let Some(right_node) = right {
                stack.push(right_node);
                Solution::add_all_left_node(&mut stack);
            }
        }
        res_vec
    }

    //添加 stack.last() 的所有左子节点到 stack
    fn add_all_left_node(stack: &mut Vec<Rc<RefCell<TreeNode>>>) {
        loop {
            let node = stack.pop().unwrap();
            let left = node.borrow().left.clone();
            stack.push(node);
            if let Some(left_node) = left {
                stack.push(left_node);
            } else {
                break;
            }
        }
    }

    //Recursion
    #[allow(dead_code)]
    pub fn inorder_traversal(root: Option<Rc<RefCell<TreeNode>>>) -> Vec<i32> {
        if let Some(node) = root {
            let mut a = Solution::inorder_traversal(node.borrow().left.clone());
            let v = node.borrow().val;
            let b = Solution::inorder_traversal(node.borrow().right.clone());
            a.push(v);
            a.extend(b);
            a
        } else {
            vec![]
        }
    }
}

#[derive(Debug, PartialEq, Eq)]
pub struct TreeNode {
    pub val: i32,
    pub left: Option<Rc<RefCell<TreeNode>>>,
    pub right: Option<Rc<RefCell<TreeNode>>>,
}

impl TreeNode {
    #[inline]
    pub fn new(val: i32) -> Self {
        TreeNode {
            val,
            left: None,
            right: None,
        }
    }
}
