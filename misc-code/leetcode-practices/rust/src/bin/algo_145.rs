use std::cell::RefCell;
use std::rc::Rc;

fn main() {
    assert_eq!(
        Solution::postorder_traversal(Some(Rc::new(RefCell::new(TreeNode {
            val: 1,
            left: None,
            right: None
        })))),
        vec![1]
    );

    assert_eq!(
        Solution::postorder_traversal(Some(Rc::new(RefCell::new(TreeNode {
            val: 1,
            left: Some(Rc::new(RefCell::new(TreeNode {
                val: 2,
                left: Some(Rc::new(RefCell::new(TreeNode::new(4)))),
                right: Some(Rc::new(RefCell::new(TreeNode::new(5)))),
            }))),
            right: Some(Rc::new(RefCell::new(TreeNode::new(3)))),
        })))),
        vec![4, 5, 2, 3, 1]
    );

    assert_eq!(
        Solution::postorder_traversal(Some(Rc::new(RefCell::new(TreeNode {
            val: 1,
            left: None,
            right: Some(Rc::new(RefCell::new(TreeNode {
                val: 2,
                left: Some(Rc::new(RefCell::new(TreeNode::new(3)))),
                right: None
            })))
        })))),
        vec![3, 2, 1]
    );

    assert_eq!(Solution::postorder_traversal(None), vec![]);
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

struct Solution;

impl Solution {
    //TODO 迭代方式下的后序遍历

    //递归方式下的后序遍历
    pub fn postorder_traversal(root: Option<Rc<RefCell<TreeNode>>>) -> Vec<i32> {
        let mut res = vec![];
        if root.is_some() {
            let rc_node = root.unwrap();
            let cell = rc_node.borrow();
            res.append(&mut Solution::postorder_traversal(cell.left.clone()));
            res.append(&mut Solution::postorder_traversal(cell.right.clone()));
            res.push(cell.val);
        }
        res
    }
}
