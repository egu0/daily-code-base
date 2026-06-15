use std::fmt::{Debug, Display};

fn main() {
    problem21();
}

fn problem21() {
    let list1 = form_list(vec![1, 2, 5]);
    let list2 = form_list(vec![1, 3, 4]);
    let s = Solution {};
    let r = s.merge_two_lists(list1, list2);
    print_list(r);
}

fn form_list<T: Copy>(vec: Vec<T>) -> Option<Box<ListNode<T>>> {
    if vec.is_empty() {
        return None;
    }
    let mut root: ListNode<T> = ListNode::new(vec[0]);
    let mut cur_node: &mut ListNode<T> = &mut root;
    for i in 1..vec.len() {
        cur_node.next = Some(Box::new(ListNode::new(vec[i])));
        cur_node = cur_node.next.as_mut().unwrap();
    }
    return Some(Box::new(root));
}

fn print_list<T: Display>(cur: Option<Box<ListNode<T>>>) {
    print!("list:");
    let mut cur_node = &cur;
    while let Some(ref cur) = cur_node {
        print!("{} ", cur.val);
        cur_node = &cur.next;
    }
    println!("");
}

#[allow(dead_code)]
struct Solution {}
impl Solution {
    pub fn merge_two_lists<T: Copy + Display + PartialOrd + Debug>(
        &self,
        list1: Option<Box<ListNode<T>>>,
        list2: Option<Box<ListNode<T>>>,
    ) -> Option<Box<ListNode<T>>> {
        let mut ptr_1 = &list1;
        let mut ptr_2 = &list2;
        if ptr_1.is_none() {
            return list2;
        }
        if ptr_2.is_none() {
            return list1;
        }

        let mut list: Vec<T> = vec![];

        loop {
            if ptr_1.is_none() {
                while let Some(ref cur) = ptr_2 {
                    list.push(cur.val);
                    ptr_2 = &cur.next;
                }
                break;
            } else if ptr_2.is_none() {
                while let Some(ref cur) = ptr_1 {
                    list.push(cur.val);
                    ptr_1 = &cur.next;
                }
                break;
            } else {
                let n1 = ptr_1.as_ref().unwrap();
                let n2 = ptr_2.as_ref().unwrap();
                if n1.val < n2.val {
                    list.push(n1.val);
                    ptr_1 = &n1.next;
                } else {
                    list.push(n2.val);
                    ptr_2 = &n2.next;
                }
            }
        }

        // Vec<T>  ->  ListNode<T>
        let mut root: ListNode<T> = ListNode::new(list[0]);
        let mut cur_node: &mut ListNode<T> = &mut root;
        for i in 1..list.len() {
            cur_node.next = Some(Box::new(ListNode::new(list[i])));
            cur_node = cur_node.next.as_mut().unwrap();
        }
        return Some(Box::new(root));
    }
}

#[derive(PartialEq, Eq, Clone, Debug)]
pub struct ListNode<T> {
    pub val: T,
    pub next: Option<Box<ListNode<T>>>,
}

impl<T> ListNode<T> {
    #[inline]
    fn new(val: T) -> Self {
        ListNode { next: None, val }
    }
}
