use std::cell::RefCell;
use std::rc::Rc;
fn main() {}

#[allow(dead_code)]
struct Solution {}
impl Solution {
    #[allow(dead_code)]
    pub fn is_same_tree(
        p: Option<Rc<RefCell<TreeNode>>>,
        q: Option<Rc<RefCell<TreeNode>>>,
    ) -> bool {
        if p.is_none() && q.is_none() {
            return true;
        }
        if p.is_none() || q.is_none() {
            return false;
        }
        let rc_p = p.unwrap();
        let rc_q = q.unwrap();
        if rc_p.borrow().val != rc_q.borrow().val {
            return false;
        }
        return Solution::is_same_tree(rc_p.borrow().left.clone(), rc_q.borrow().left.clone())
            && Solution::is_same_tree(rc_p.borrow().right.clone(), rc_q.borrow().right.clone());
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
