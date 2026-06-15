use std::cell::RefCell;
use std::cmp::min;
use std::rc::Rc;

fn main() {}

#[allow(dead_code)]
struct Solution {}
impl Solution {
    #[allow(dead_code)]
    pub fn min_depth(root: Option<Rc<RefCell<TreeNode>>>) -> i32 {
        if root.is_none() {
            0
        } else {
            Solution::solve(root)
        }
    }
    fn solve(root: Option<Rc<RefCell<TreeNode>>>) -> i32 {
        if let Some(node) = root {
            if node.borrow().left.is_none() && node.borrow().right.is_none() {
                1
            } else {
                1 + min(
                    Solution::solve(node.borrow().left.clone()),
                    Solution::solve(node.borrow().right.clone()),
                )
            }
        } else {
            // root is None
            100001
        }
    }

    //----------------------

    //树根到叶子结点的最小距离（树根节点到叶子节点之间的节点个数，包含起始点）
    #[allow(dead_code)]
    pub fn min_depth2(root: Option<Rc<RefCell<TreeNode>>>) -> i32 {
        if root.is_none() {
            return 0;
        }
        return Solution::process(root.as_ref());
    }

    pub fn process(root: Option<&Rc<RefCell<TreeNode>>>) -> i32 {
        let y = root.as_ref().unwrap().borrow();
        let mut l: Option<i32> = None;
        let mut r: Option<i32> = None;
        if y.left.is_some() {
            l = Some(Solution::process(y.left.as_ref()));
        }
        if y.right.is_some() {
            r = Some(Solution::process(y.right.as_ref()));
        }
        match (l, r) {
            (None, None) => 1,
            (Some(a), None) => a + 1,
            (None, Some(b)) => b + 1,
            (Some(a), Some(b)) => min(a, b) + 1,
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
