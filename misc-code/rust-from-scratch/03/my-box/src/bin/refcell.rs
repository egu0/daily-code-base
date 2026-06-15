use std::cell::{RefCell, RefMut};

pub trait Messager {
    fn send(&self, msg: &str);
}

struct MockMessager {
    sent_messages: RefCell<Vec<String>>,
}

impl MockMessager {
    fn new() -> Self {
        MockMessager {
            sent_messages: RefCell::new(vec![]),
        }
    }
}

impl Messager for MockMessager {
    fn send(&self, msg: &str) {
        //self.sent_messages.borrow_mut(): 获取内部值的可变引用
        self.sent_messages.borrow_mut().push(String::from(msg));
    }
}

fn main() {
    let mm = MockMessager::new();
    mm.send("ok");
    mm.send("network error: gQiKeO");
    //mm.sent_messages.borrow(): 获取内部值的不可变引用
    assert_eq!(2, mm.sent_messages.borrow().len());
    //mm.sent_messages.borrow_mut(): 获取内部值的可变引用
    mm.sent_messages.borrow_mut().push(String::from("what?"));
    assert_eq!(3, mm.sent_messages.borrow().len());

    //panic: already borrowed: BorrowMutError
    // do_it(mm.sent_messages.borrow_mut(), mm.sent_messages.borrow_mut());
}

#[allow(dead_code)]
fn do_it(_x1: RefMut<Vec<String>>, _x2: RefMut<Vec<String>>) {}
