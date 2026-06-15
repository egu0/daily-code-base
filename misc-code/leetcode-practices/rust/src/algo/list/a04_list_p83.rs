fn main() {}

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

#[allow(dead_code)]
struct Solution {}
impl Solution {
    //⭐⭐⭐
    #[allow(dead_code)]
    pub fn delete_duplicates3(mut head: Option<Box<ListNode>>) -> Option<Box<ListNode>> {
        let mut root = ListNode::new(101);
        let mut cur = &mut root;

        while let Some(mut node) = head {
            head = node.next;

            if cur.val != node.val {
                node.next = None;
                cur.next = Some(node);
                //获取 cur.next 中 ListNode 的可变引用
                cur = cur.next.as_mut().unwrap();
            }
        }
        root.next
    }

    #[allow(dead_code)]
    pub fn delete_duplicates(head: Option<Box<ListNode>>) -> Option<Box<ListNode>> {
        if head.is_none() {
            None
        } else {
            //记录上一个数字
            let mut last_number: i32 = 101;

            //冗余节点
            let mut root = ListNode::new(101);
            let mut cur_ptr = &mut root;

            let mut cur_node = head.unwrap();
            loop {
                //下个节点
                let next = cur_node.next;

                //如果数字不同，那么把 cur_node 加入结果链表
                if cur_node.val != last_number {
                    last_number = cur_node.val;
                    cur_node.next = None;
                    cur_ptr.next = Some(cur_node);
                    // let x: Option<&mut Box<ListNode>> = cur_ptr.next.as_mut();
                    cur_ptr = cur_ptr.next.as_mut().unwrap();
                }

                //下一个节点
                if next.is_some() {
                    cur_node = next.unwrap();
                } else {
                    break;
                }
            }
            root.next
        }
    }
}
