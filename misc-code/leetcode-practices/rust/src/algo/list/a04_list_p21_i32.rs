use std::fmt::Debug;

fn main() {
    problem21();
}

fn problem21() {
    let list1 = form_list(vec![1, 2, 5]);
    let list2 = form_list(vec![2, 3, 4]);
    let s = Solution {};
    let r = s.merge_two_lists3(list1, list2);
    print_list(r);
}

fn form_list(vec: Vec<i32>) -> Option<Box<ListNode>> {
    if vec.is_empty() {
        return None;
    }
    let mut root: ListNode = ListNode::new(vec[0]);
    let mut cur_node: &mut ListNode = &mut root;
    //for i in 1..vec.len() {
    for i in vec.iter().skip(1) {
        cur_node.next = Some(Box::new(ListNode::new(*i)));
        cur_node = cur_node.next.as_mut().unwrap();
    }
    Some(Box::new(root))
}

fn print_list(cur: Option<Box<ListNode>>) {
    print!("list:");
    let mut cur_node = &cur;
    while let Some(ref cur) = cur_node {
        print!("{} ", cur.val);
        cur_node = &cur.next;
    }
    println!();
}

#[allow(dead_code)]
struct Solution {}
impl Solution {
    // 0ms: https://leetcode.com/problems/merge-two-sorted-lists/submissions/1213481288/
    #[allow(dead_code)]
    pub fn merge_two_lists(
        &self,
        list1: Option<Box<ListNode>>,
        list2: Option<Box<ListNode>>,
    ) -> Option<Box<ListNode>> {
        if list1.is_none() {
            return list2;
        }
        if list2.is_none() {
            return list1;
        }

        //冗余节点
        let mut head = ListNode::new(101);
        let mut cur_ptr = &mut head;

        let mut cur_node_1 = list1.unwrap();
        let mut cur_node_2 = list2.unwrap();

        loop {
            // 如果 list1 或 list2 遍历结束，则把另一个链表剩余未遍历部分拼接到 head 链表中
            if cur_node_1.val == 101 || cur_node_2.val == 101 {
                // cur_node_x 指向未遍历完的链表
                let mut cur_node_x;
                if cur_node_1.val == 101 {
                    cur_node_x = cur_node_2;
                } else {
                    cur_node_x = cur_node_1;
                }

                loop {
                    let next = cur_node_x.next;
                    cur_node_x.next = None;
                    cur_ptr.next = Some(cur_node_x);
                    cur_ptr = cur_ptr.next.as_mut().unwrap();
                    if next.is_some() {
                        cur_node_x = next.unwrap();
                    } else {
                        break;
                    }
                }

                break;
            }

            if cur_node_1.val >= cur_node_2.val {
                /*
                let tmp = cur_node_1;
                cur_node_1 = cur_node_2;
                cur_node_2 = tmp;
                */
                std::mem::swap(&mut cur_node_1, &mut cur_node_2);
            }

            // 此时 cur_node_1.val < cur_node_2.val

            let next = cur_node_1.next;

            cur_node_1.next = None;
            cur_ptr.next = Some(cur_node_1);
            // cur_ptr.next  -->  Option<Box<ListNode>>
            let x: Option<&mut Box<ListNode>> = cur_ptr.next.as_mut();
            cur_ptr = x.unwrap();

            if next.is_some() {
                cur_node_1 = next.unwrap();
            } else {
                // node.next 为空，遍历到头，添加一个结束节点
                cur_node_1 = Box::new(ListNode::new(101));
            }
        }

        head.next
    }

    // 2ms
    #[allow(dead_code)]
    pub fn merge_two_lists2(
        &self,
        list1: Option<Box<ListNode>>,
        list2: Option<Box<ListNode>>,
    ) -> Option<Box<ListNode>> {
        // let n1 = list1.unwrap();
        // println!("{}", n1.val);
        // let n2 = n1.next.unwrap();
        // println!("{}", n2.val);
        // let n3 = n2.next.unwrap();
        // println!("{}", n3.val);

        // let mut cur_node = &list2;
        // while let Some(ref cur) = cur_node {
        //     println!("{}", cur.val);
        //     cur_node = &cur.next;
        // }

        let mut ptr_1 = &list1;
        let mut ptr_2 = &list2;
        if ptr_1.is_none() {
            return list2;
        }
        if ptr_2.is_none() {
            return list1;
        }

        let mut root: ListNode = ListNode::new(0);
        let mut cur_node = &mut root;

        loop {
            if ptr_1.is_none() {
                while let Some(ref cur) = ptr_2 {
                    cur_node.next = Some(Box::new(ListNode::new(cur.val)));
                    cur_node = cur_node.next.as_mut().unwrap();
                    ptr_2 = &cur.next;
                }
                break;
            } else if ptr_2.is_none() {
                while let Some(ref cur) = ptr_1 {
                    cur_node.next = Some(Box::new(ListNode::new(cur.val)));
                    cur_node = cur_node.next.as_mut().unwrap();
                    ptr_1 = &cur.next;
                }
                break;
            } else {
                let n1 = ptr_1.as_ref().unwrap();
                let n2 = ptr_2.as_ref().unwrap();
                if n1.val < n2.val {
                    cur_node.next = Some(Box::new(ListNode::new(n1.val)));
                    cur_node = cur_node.next.as_mut().unwrap();
                    ptr_1 = &n1.next;
                } else {
                    cur_node.next = Some(Box::new(ListNode::new(n2.val)));
                    cur_node = cur_node.next.as_mut().unwrap();
                    ptr_2 = &n2.next;
                }
            }
        }

        root.next
    }

    // 可变函数参数
    #[allow(dead_code)]
    pub fn merge_two_lists3(
        &self,
        mut list1: Option<Box<ListNode>>,
        mut list2: Option<Box<ListNode>>,
    ) -> Option<Box<ListNode>> {
        if list1.is_none() {
            return list2;
        }
        if list2.is_none() {
            return list1;
        }

        let mut root = ListNode::new(101);
        let mut cur = &mut root;

        loop {
            let mut n1 = list1.unwrap();
            let mut n2 = list2.unwrap();
            if n1.val == 101 && n2.val == 101 {
                break;
            }

            if n1.val == 101 {
                list1 = Some(n1);

                let next = n2.next;
                n2.next = None;
                cur.next = Some(n2);
                cur = cur.next.as_mut().unwrap();
                if next.is_some() {
                    list2 = next;
                } else {
                    list2 = Some(Box::new(ListNode::new(101)));
                }
            } else if n2.val == 101 {
                list2 = Some(n2);

                let next = n1.next;
                n1.next = None;
                cur.next = Some(n1);
                cur = cur.next.as_mut().unwrap();
                if next.is_some() {
                    list1 = next;
                } else {
                    list1 = Some(Box::new(ListNode::new(101)));
                }
            } else {
                //  n1.val != 101 && n2.val != 101
                if n1.val < n2.val {
                    let next = n1.next;
                    n1.next = None;
                    cur.next = Some(n1);
                    cur = cur.next.as_mut().unwrap();
                    if next.is_some() {
                        list1 = next;
                    } else {
                        list1 = Some(Box::new(ListNode::new(101)));
                    }
                    list2 = Some(n2);
                } else {
                    let next = n2.next;
                    n2.next = None;
                    cur.next = Some(n2);
                    cur = cur.next.as_mut().unwrap();
                    if next.is_some() {
                        list2 = next;
                    } else {
                        list2 = Some(Box::new(ListNode::new(101)));
                    }
                    list1 = Some(n1);
                }
            }
        }
        root.next
    }
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
