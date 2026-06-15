#[derive(Debug)]
enum ListNode {
    Node(i32, Rc<ListNode>),
    Nil,
}

use crate::ListNode::{Nil, Node};
use std::rc::Rc;

fn main() {
    let a: Rc<ListNode> = Rc::new(Node(1, Rc::new(Node(2, Rc::new(Node(3, Rc::new(Nil)))))));
    println!("a' ref count is {}", Rc::strong_count(&a)); //1

    let _b: ListNode = Node(5, Rc::clone(&a));
    println!("a' ref count is {}", Rc::strong_count(&a)); //2

    {
        let _c: ListNode = Node(4, Rc::clone(&a));
        println!("a' ref count is {}", Rc::strong_count(&a)); //3
    }

    println!("a' ref count is {}", Rc::strong_count(&a)); //2
}
