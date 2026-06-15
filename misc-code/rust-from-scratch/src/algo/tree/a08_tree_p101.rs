use std::cell::RefCell;
use std::rc::Rc;
fn main() {}

#[allow(dead_code)]
struct Solution {}
impl Solution {
    //Recursion, 0ms
    #[allow(dead_code)]
    pub fn is_symmetric2(root: Option<Rc<RefCell<TreeNode>>>) -> bool {
        let node = root.unwrap();
        return Solution::is_same(node.borrow().left.as_ref(), node.borrow().right.as_ref());
    }
    pub fn is_same(
        left: Option<&Rc<RefCell<TreeNode>>>,
        right: Option<&Rc<RefCell<TreeNode>>>,
    ) -> bool {
        if left.is_none() && right.is_none() {
            return true;
        }
        if left.is_none() || right.is_none() {
            return false;
        }
        let l = left.unwrap();
        let r = right.unwrap();
        if l.borrow().val != r.borrow().val {
            return false;
        }
        return Solution::is_same(l.borrow().left.as_ref(), r.borrow().right.as_ref())
            && Solution::is_same(l.borrow().right.as_ref(), r.borrow().left.as_ref());
    }

    #[allow(dead_code)]
    pub fn is_symmetric(root: Option<Rc<RefCell<TreeNode>>>) -> bool {
        //clone() 耗时，转为 as_ref or as_mut ?
        let node = root.unwrap();
        let mut stack1 = vec![node.borrow().left.clone()];
        let mut stack2 = vec![node.borrow().right.clone()];
        loop {
            if stack1.len() != stack2.len() {
                return false;
            }
            let len = stack1.len();
            if len == 0 {
                break;
            }
            let mut new_stack1 = vec![];
            let mut new_stack2 = vec![];
            for i in 0..len {
                let x = stack1[i].as_mut();
                let y = stack2[i].as_mut();
                if x.is_none() && y.is_none() {
                    continue;
                }
                if x.is_none() || y.is_none() {
                    return false;
                }
                let node_x = x.unwrap();
                let node_y = y.unwrap();
                if node_x.borrow().val != node_y.borrow().val {
                    return false;
                }
                new_stack1.push(node_x.borrow().left.clone());
                new_stack1.push(node_x.borrow().right.clone());
                new_stack2.push(node_y.borrow().right.clone());
                new_stack2.push(node_y.borrow().left.clone());
            }
            stack1 = new_stack1;
            stack2 = new_stack2;
        }

        return true;
    }

    //-----------------------------------------

    //P#104
    #[allow(dead_code)]
    pub fn max_depth(root: Option<Rc<RefCell<TreeNode>>>) -> i32 {
        // return Solution::count(&root);
        return Solution::depth(&root);
    }

    //others
    fn depth(root: &Option<Rc<RefCell<TreeNode>>>) -> i32 {
        root.as_ref()
            .map(|node| Self::depth(&node.borrow().left).max(Self::depth(&node.borrow().right)) + 1)
            .unwrap_or(0)
    }

    //my 2th version
    #[allow(dead_code)]
    pub fn count(root: &Option<Rc<RefCell<TreeNode>>>) -> i32 {
        if root.is_none() {
            return 0;
        }
        let rc = root.as_ref().unwrap();
        let l = Solution::count(&rc.borrow().left);
        let r = Solution::count(&rc.borrow().right);
        if l > r {
            return l + 1;
        } else {
            return r + 1;
        }
    }

    //my 1th version
    #[allow(dead_code)]
    pub fn max_depth0(root: Option<Rc<RefCell<TreeNode>>>) -> i32 {
        if root.is_none() {
            return 0;
        }
        let rc = root.unwrap();
        let l = Solution::max_depth(rc.borrow().left.clone());
        let r = Solution::max_depth(rc.borrow().right.clone());
        if l > r {
            return l + 1;
        } else {
            return r + 1;
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
