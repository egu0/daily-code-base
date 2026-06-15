fn main() {
    #[derive(Debug)]
    enum Status {
        Value(u32),
    }

    let list: Vec<Status> = (0u32..5).map(Status::Value).collect();
    //[Value(0), Value(1), Value(2), Value(3), Value(4)]
    println!("{:?}", list);
}
