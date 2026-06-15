use std::{sync::mpsc, thread::spawn};

fn main() {
    let (sender, receiver) = mpsc::channel();

    spawn(move || {
        let data = String::from("hi");

        //所有权已经转移了
        sender.send(data).unwrap();

        //错误
        // println!("data: {}", data);
    });

    let res = receiver.recv().unwrap();
    println!("recv: {}", res);
}
