#[derive(Debug)]
enum List {
    Node(i32, RefCell<Rc<List>>),
    Nil,
}

use crate::List::{Nil, Node};
use std::{cell::RefCell, rc::Rc};

impl List {
    fn next(&self) -> Option<&RefCell<Rc<List>>> {
        match self {
            Node(_, item) => Some(item),
            Nil => None,
        }
    }
}

fn main() {
    let a = Rc::new(Node(5, RefCell::new(Rc::new(Nil))));
    println!(
        "1. ref count: a is {}, \n\ta.next is {:?}",
        Rc::strong_count(&a),
        a.next()
    );

    // a: 5 -> nil
    // b: 10 -> a
    let b = Rc::new(Node(10, RefCell::new(Rc::clone(&a))));
    println!(
        "2. ref count: a is {}, b is {}, \n\ta.next is {:?}, \n\tb.next is {:?}",
        Rc::strong_count(&a),
        Rc::strong_count(&b),
        a.next(),
        b.next()
    );

    // a: 5 -> b
    if let Some(link) = a.next() {
        *link.borrow_mut() = Rc::clone(&b);
    }
    println!(
        "3. ref count: a is {}, b is {}",
        Rc::strong_count(&a),
        Rc::strong_count(&b),
    );

    //infinite loop
    // println!("{:?}", a.next());
    // println!("{:?}", b.next());
}
