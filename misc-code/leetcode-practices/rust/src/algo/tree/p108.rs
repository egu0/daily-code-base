use std::cell::RefCell;
use std::rc::Rc;

fn main() {
    Solution::sorted_array_to_bst(vec![-10, -3, 0, 5, 9]);
}

#[allow(dead_code)]
struct Solution {}
impl Solution {
    #[allow(dead_code)]
    pub fn sorted_array_to_bst(nums: Vec<i32>) -> Option<Rc<RefCell<TreeNode>>> {
        Solution::solve(&nums[..])
    }

    //使用向量切片，减少 new 申请空间的次数
    pub fn solve(nums: &[i32]) -> Option<Rc<RefCell<TreeNode>>> {
        let len = nums.len();
        if len == 0 {
            return None;
        }
        let mid = len / 2;
        let mut n = TreeNode::new(nums[mid]);
        n.left = Solution::solve(&nums[..mid]);
        n.right = Solution::solve(&nums[mid + 1..]);
        Some(Rc::new(RefCell::new(n)))
    }

    #[allow(dead_code)]
    pub fn sorted_array_to_bst2(nums: Vec<i32>) -> Option<Rc<RefCell<TreeNode>>> {
        Solution::process(&nums, 0, nums.len() - 1)
    }

    //将升序数组转为平衡的BST，思路：分治思想，将当前数组的最中间元素转化为当前树的根节点
    pub fn process(nums: &Vec<i32>, l: usize, r: usize) -> Option<Rc<RefCell<TreeNode>>> {
        if l > r {
            return None;
        }
        let m = (l + r) / 2;
        let v = nums[m];
        let mut n = TreeNode::new(v);
        if m == 0 {
            n.left = Solution::process(nums, l, m - 1);
        }
        n.right = Solution::process(nums, m + 1, r);
        Some(Rc::new(RefCell::new(n)))
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
