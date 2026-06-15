use crate::ListNode::{Nil, Node};

fn main() {
    let list = Node(1, Box::new(Node(2, Box::new(Node(3, Box::new(Nil))))));
    println!("{:?}", list);
}

#[allow(dead_code)]
#[derive(Debug)]
enum ListNode {
    //Box<T>
    Node(i32, Box<ListNode>),
    Nil,
}
