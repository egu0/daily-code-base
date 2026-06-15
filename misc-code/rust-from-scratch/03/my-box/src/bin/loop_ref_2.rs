use std::cell::RefCell;
use std::rc::{Rc, Weak};

#[allow(dead_code)]
#[derive(Debug)]
struct Node {
    value: i32,
    parent: RefCell<Weak<Node>>,      //子节点指向父节点的智能指针用 Weak
    children: RefCell<Vec<Rc<Node>>>, //父节点指向子节点的智能指针用 Rc
}

fn main() {
    let leaf = Rc::new(Node {
        value: 3,
        parent: RefCell::new(Weak::new()),
        children: RefCell::new(vec![]),
    });

    //leaf: 1,0
    print_ref_count(1, &leaf, None);

    {
        let branch = Rc::new(Node {
            value: 5,
            parent: RefCell::new(Weak::new()),
            children: RefCell::new(vec![]),
        });

        //leaf: 1,0  branch: 1,0
        print_ref_count(2, &leaf, Some(&branch));

        //⭐ 创建 leaf 的强引用并将其加入到 branch.children 集合；伪代码：branch.children.push( Rc(leaf) )
        (*branch).children.borrow_mut().push(Rc::clone(&leaf));

        //leaf: 2,0  branch: 1,0
        print_ref_count(3, &leaf, Some(&branch));

        //⭐ 创建 branch 的弱引用，并赋值给 leaf.parent；伪代码：leaf.parent = Weak(branch)
        *leaf.parent.borrow_mut() = Rc::downgrade(&branch);

        //leaf: 2,0  branch: 1,1
        print_ref_count(4, &leaf, Some(&branch));

        //循环引用
        // leaf.parent      ---weak--->   branch
        // branch.children  ----rc---->     leaf
    }

    // leaf's parent: None
    println!("\nleaf's parent: {:#?}", leaf.parent.borrow().upgrade());
    //leaf: 1, 0
    print_ref_count(5, &leaf, None);
}

/// 打印两个 Rc 实例的强弱引用计数值
fn print_ref_count(stage: i32, leaf: &Rc<Node>, branch: Option<&Rc<Node>>) {
    println!(
        "\n{}. leaf's ref count: strong {}, weak {}",
        stage,
        Rc::strong_count(leaf),
        Rc::weak_count(leaf),
    );
    if let Some(parent) = branch {
        println!(
            " branch's ref count: strong {}, weak {}",
            Rc::strong_count(parent),
            Rc::weak_count(parent)
        );
    }
}

#[allow(dead_code)]
fn main2() {
    let leaf = Rc::new(Node {
        value: 3,
        parent: RefCell::new(Weak::new()),
        children: RefCell::new(vec![]),
    });

    // upgrade(&a) 作用 Weak<T> -> Rc<T>
    println!("1. leaf's parent:\n{:#?}", leaf.parent.borrow().upgrade());

    let parent = Rc::new(Node {
        value: 5,
        parent: RefCell::new(Weak::new()),
        children: RefCell::new(vec![Rc::clone(&leaf)]),
    });

    // Rc::downgrade(&a) 作用 Rc<T> -> Weak<T>
    *leaf.parent.borrow_mut() = Rc::downgrade(&parent);
    println!("2. leaf's parent:\n{:#?}", leaf.parent.borrow().upgrade());
}
