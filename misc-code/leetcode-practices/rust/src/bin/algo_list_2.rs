fn main() {
    Solution::add_two_numbers(
        Some(Box::new(ListNode {
            val: 2,
            next: Some(Box::new(ListNode {
                val: 4,
                next: Some(Box::new(ListNode::new(3))),
            })),
        })),
        Some(Box::new(ListNode {
            val: 5,
            next: Some(Box::new(ListNode {
                val: 6,
                next: Some(Box::new(ListNode::new(4))),
            })),
        })),
    );
}

#[derive(PartialEq, Eq, Clone, Debug)]
pub struct ListNode {
    pub val: i32,
    pub next: Option<Box<ListNode>>,
}

impl ListNode {
    #[inline]
    fn new(val: i32) -> Self {
        ListNode { next: None, val }
    }
}

struct Solution {}

impl Solution {
    pub fn add_two_numbers(
        mut l1: Option<Box<ListNode>>,
        mut l2: Option<Box<ListNode>>,
    ) -> Option<Box<ListNode>> {
        let mut dump_head = ListNode::new(0);
        let mut cur_ptr = &mut dump_head;
        let mut carry: i32 = 0;
        let mut n1: i32 = 0;
        let mut n2: i32 = 0;
        let mut n1_reach_end: bool = false;
        let mut n2_reach_end: bool = false;

        loop {
            match l1 {
                Some(node) => {
                    n1 = node.val;
                    l1 = node.next;
                }
                None => {
                    n1_reach_end = true;
                }
            }

            match l2 {
                Some(node) => {
                    n2 = node.val;
                    l2 = node.next;
                }
                None => {
                    n2_reach_end = true;
                }
            }

            let res: i32;
            if n1_reach_end && n2_reach_end {
                if carry == 0 {
                    break;
                } else {
                    res = carry;
                    carry = 0;
                }
            } else if n1_reach_end {
                res = (n2 + carry) % 10;
                carry = (n2 + carry) / 10;
            } else if n2_reach_end {
                res = (n1 + carry) % 10;
                carry = (n1 + carry) / 10;
            } else {
                res = (n1 + n2 + carry) % 10;
                carry = (n1 + n2 + carry) / 10;
            }
            println!("res={}, carry={}", res, carry);
            let new_one = ListNode::new(res);
            cur_ptr.next = Some(Box::new(new_one));
            cur_ptr = cur_ptr.next.as_mut()?;
            // cur_ptr                : &mut ListNode
            // cur_ptr.next           : Option<Box<ListNode>>
            // cur_ptr.next.as_mut()  : Option<&mut Box<ListNode>>
            // cur_ptr.next.as_mut()? : &mut Box<ListNode>
        }

        dump_head.next
    }
}
