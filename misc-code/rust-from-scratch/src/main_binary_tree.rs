struct TreeNode {
    value: i32,
    left: Option<Box<TreeNode>>,
    right: Option<Box<TreeNode>>,
}

impl TreeNode {
    // Self 表示自身类型
    fn new(value: i32) -> Self {
        TreeNode {
            value,
            left: None,
            right: None,
        }
    }

    // in-order traversal
    fn in_order_traversal(&self) -> Vec<&i32> {
        let mut result = Vec::new();
        // left
        if let Some(ref left) = self.left {
            result.extend(left.in_order_traversal());
        }
        // current
        result.push(&self.value);
        // right
        if let Some(ref right) = self.right {
            result.extend(right.in_order_traversal());
        }
        result
    }
}

fn main() {
    // Create a simple binary tree
    let mut root = TreeNode::new(3);
    root.left = Some(Box::new(TreeNode::new(1)));
    root.right = Some(Box::new(TreeNode::new(4)));

    // Add a child to the left node
    if let Some(ref mut left) = root.left {
        left.right = Some(Box::new(TreeNode::new(2)));
    }

    // Perform in-order traversal and print the values
    let values = root.in_order_traversal();
    println!("In-order traversal: {:?}", values);
}
