use std::{
    sync::mpsc,
    thread::{self, spawn},
    time::Duration,
};

fn main() {
    let (sender, receiver) = mpsc::channel();
    //克隆发送端
    let sender2 = mpsc::Sender::clone(&sender);

    spawn(move || {
        let msgs = vec![
            String::from("hi"),
            String::from("how are you"),
            String::from("fine"),
        ];
        for msg in msgs {
            sender.send(msg + ", from sender.").unwrap();
            thread::sleep(Duration::from_millis(1));
        }
    });

    spawn(move || {
        let msgs = vec![String::from("hi")];
        for msg in msgs {
            sender2.send(msg + ", from sender2.").unwrap();
            thread::sleep(Duration::from_millis(1));
        }
    });

    for msg in receiver {
        println!("Got: {:?}", msg);
    }

    println!("over!");
}
