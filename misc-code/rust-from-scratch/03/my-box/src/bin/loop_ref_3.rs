use std::cell::RefCell;
use std::rc::Rc;

#[allow(dead_code)]
#[derive(Debug)]
struct Node {
    value: i32,
    neighbors: RefCell<Vec<Rc<Node>>>,
}

fn main() {
    let one = Rc::new(Node {
        value: 3,
        neighbors: RefCell::new(vec![]),
    });

    // another.neighbors = [ leaf ]
    let another = Rc::new(Node {
        value: 5,
        neighbors: RefCell::new(vec![Rc::clone(&one)]),
    });

    // one.neighbors = [ another ]
    (*one).neighbors.borrow_mut().push(Rc::clone(&another));

    //2,2
    println!(
        "ref count: one is {}, another is {}",
        Rc::strong_count(&one),
        Rc::strong_count(&another)
    );
}
