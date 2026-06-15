use std::thread;

fn main() {
    let nums = vec![1, 2, 3];
    let jh = thread::spawn(move || {
        println!("nums: {:?}", nums);
    });

    // println!("{:?}", nums); //borrow of moved value: `nums`

    jh.join().unwrap();
}
