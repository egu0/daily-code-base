use std::cell::RefCell;
use std::rc::Rc;

fn main() {}

#[allow(dead_code)]
struct Solution {}
impl Solution {
    //⭐⭐⭐
    //是否存在一条从 root 到 leaf 的路径，满足路径上的所有节点值的和为 target_sum
    #[allow(dead_code)]
    pub fn has_path_sum(root: Option<Rc<RefCell<TreeNode>>>, target_sum: i32) -> bool {
        if root.is_none() {
            false
        } else {
            fn process(root: Option<Rc<RefCell<TreeNode>>>, target_sum: i32) -> bool {
                if let Some(node) = root {
                    let cur_sum = target_sum - node.borrow().val;
                    let exist_l = node.borrow().left.is_some();
                    let exist_r = node.borrow().right.is_some();
                    if !exist_l && !exist_r {
                        0 == cur_sum
                    } else if exist_l && !exist_r {
                        process(node.borrow().left.clone(), cur_sum)
                    } else if !exist_l && exist_r {
                        process(node.borrow().right.clone(), cur_sum)
                    } else {
                        process(node.borrow().left.clone(), cur_sum)
                            || process(node.borrow().right.clone(), cur_sum)
                    }
                } else {
                    target_sum == 0
                }
            }
            process(root, target_sum)
        }
    }
    // pub fn process() -> bool {
    //     if let Some(node) = root {
    //         Solution::has_path_sum(node.borrow().left.as_ref(), target_sum);
    //     }
    // }
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
