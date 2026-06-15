use std::{thread, time::Duration};

fn main() {
    let join_handle = thread::spawn(|| {
        for i in 0..10 {
            println!("in spawned thread, i is {}", i);
            thread::sleep(Duration::from_millis(1));
        }
    });

    for i in 0..5 {
        println!("in    main thread, i is {}", i);
        thread::sleep(Duration::from_millis(1));
    }

    //调用 join 方法等待子线程执行完成
    join_handle.join().unwrap();
}
