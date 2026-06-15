#[derive(Debug)]
enum List {
    Node(Rc<RefCell<i32>>, Rc<List>),
    Nil,
}

use crate::List::{Nil, Node};
use std::{cell::RefCell, rc::Rc};

fn main() {
    // l2 10 --
    //         \
    //      l1  5 -> nil
    //         |
    // l3 20 --
    let value = Rc::new(RefCell::new(5));
    let l1 = Rc::new(Node(Rc::clone(&value), Rc::new(Nil)));

    //*value         将 Rc<RefCell<T>> 解引用到 RefCell<T>
    //borrow_mut()   获取可变引用
    *value.borrow_mut() += 3;

    let l2 = Node(Rc::new(RefCell::new(10)), Rc::clone(&l1));
    let l3 = Node(Rc::new(RefCell::new(20)), Rc::clone(&l1));

    println!("{:?}", l1);
    println!("{:?}", l2);
    println!("{:?}", l3);
}
