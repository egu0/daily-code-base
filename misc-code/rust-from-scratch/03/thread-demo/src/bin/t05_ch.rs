use std::{
    sync::mpsc,
    thread::{self, spawn},
    time::Duration,
};

fn main() {
    let (sender, receiver) = mpsc::channel();

    spawn(move || {
        let msgs = vec![
            String::from("hi"),
            String::from("how are you"),
            String::from("fine"),
            String::from("you have a fast internet today"),
        ];
        for msg in msgs {
            sender.send(msg).unwrap();
            thread::sleep(Duration::from_millis(1000));
        }
    });

    for msg in receiver {
        println!("Got: {:?}", msg);
    }

    println!("over!");
}
