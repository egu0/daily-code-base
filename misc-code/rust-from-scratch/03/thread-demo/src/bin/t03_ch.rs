use std::{sync::mpsc, thread::spawn};

fn main() {
    let (sender, receiver) = mpsc::channel();

    spawn(move || {
        //发送数据
        sender.send("hi").unwrap();
    });

    //阻塞接收消息
    match receiver.recv() {
        Ok(data) => {
            println!("recv: {}", data);
        }
        Err(err) => {
            println!("err: {}", err);
        }
    }
}
