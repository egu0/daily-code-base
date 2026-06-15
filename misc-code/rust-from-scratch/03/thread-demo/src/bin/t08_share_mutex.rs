use std::{
    sync::{Arc, Mutex},
    thread,
};

fn main() {
    let counter = Arc::new(Mutex::new(0));
    *(counter.lock().unwrap()) += 0;

    let mut handles = vec![];

    for _i in 0..10 {
        let m = Arc::clone(&counter);
        let jh = thread::spawn(move || {
            let mut counter: std::sync::MutexGuard<'_, i32> = m.lock().unwrap();
            *counter += 1;
        });
        handles.push(jh);
    }

    for handle in handles {
        handle.join().unwrap();
    }

    assert_eq!(10, *counter.lock().unwrap());
}

/*
use std::{rc::Rc, sync::Mutex, thread};

fn main() {
    let counter = Rc::new(Mutex::new(0));
    *(counter.lock().unwrap()) += 0;

    let mut handles = vec![];

    for i in 0..10 {
        let m = Rc::clone(&counter);
        //Rc 实例只能用于单线程
        //error[E0277]: `Rc<Mutex<i32>>` cannot be sent between threads safely
        let jh = thread::spawn(move || {
            let mut counter: std::sync::MutexGuard<'_, i32> = m.lock().unwrap();
            *counter += 1;
        });
        handles.push(jh);
    }

    for handle in handles {
        handle.join().unwrap();
    }

    assert_eq!(10, *counter.lock().unwrap());
}
*/
