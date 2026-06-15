use std::cell::RefCell;
use std::rc::Rc;

fn main() {
    assert_eq!(
        Solution::preorder_traversal(Some(Rc::new(RefCell::new(TreeNode {
            val: 1,
            left: None,
            right: None
        })))),
        vec![1]
    );

    assert_eq!(
        Solution::preorder_traversal(Some(Rc::new(RefCell::new(TreeNode {
            val: 1,
            left: None,
            right: Some(Rc::new(RefCell::new(TreeNode {
                val: 2,
                left: Some(Rc::new(RefCell::new(TreeNode::new(3)))),
                right: None
            })))
        })))),
        vec![1, 2, 3]
    );

    assert_eq!(Solution::preorder_traversal(None), vec![]);
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
    pub fn preorder_traversal(root: Option<Rc<RefCell<TreeNode>>>) -> Vec<i32> {
        if root.is_none() {
            return vec![];
        }

        let mut res_vec: Vec<i32> = vec![];
        let mut stack = vec![root.unwrap()];
        Solution::add_all_left_node(&mut stack, &mut res_vec);

        /*
        while !stack.is_empty() {
            let node = stack.pop().unwrap();
        */
        while let Some(node) = stack.pop() {
            let right = node.borrow().right.clone();
            if let Some(right_node) = right {
                stack.push(right_node);
                Solution::add_all_left_node(&mut stack, &mut res_vec);
            }
        }
        res_vec
    }

    fn add_all_left_node(stack: &mut Vec<Rc<RefCell<TreeNode>>>, nums: &mut Vec<i32>) {
        loop {
            let node = stack.pop().unwrap();
            nums.push(node.borrow().val);

            let left = node.borrow().left.clone();
            stack.push(node);
            if let Some(left_node) = left {
                stack.push(left_node);
            } else {
                break;
            }
        }
    }

    /*
        pub fn preorder_traversal(root: Option<Rc<RefCell<TreeNode>>>) -> Vec<i32> {
            let mut res = vec![];
            if root.is_some() {
                let rc_node = root.unwrap();
                let cell = rc_node.borrow();
                res.push(cell.val);
                res.append(&mut Solution::preorder_traversal(cell.left.clone()));
                res.append(&mut Solution::preorder_traversal(cell.right.clone()));
            }
            res
        }
    */
}
