use std::cell::RefCell;
use std::cmp::{max, min};
use std::rc::Rc;
fn main() {}

#[allow(dead_code)]
struct Solution {}
impl Solution {
    //二叉树是否平衡
    #[allow(dead_code)]
    pub fn is_balanced(root: Option<Rc<RefCell<TreeNode>>>) -> bool {
        Solution::process(root.as_ref()) != -1
    }

    // 判断 root 的左右子树的高度相差是否不超过 1，返回 root 作为树根的高度
    pub fn process(root: Option<&Rc<RefCell<TreeNode>>>) -> i32 {
        if root.is_none() {
            return 0;
        }
        let rc = root.unwrap();
        let lp = Solution::process(rc.borrow().left.as_ref());
        let rp = Solution::process(rc.borrow().right.as_ref());
        if lp == -1 || rp == -1 {
            return -1;
        }

        let max = max(lp, rp);
        let min = min(lp, rp);
        if max - min < 2 {
            max + 1
        } else {
            -1
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
