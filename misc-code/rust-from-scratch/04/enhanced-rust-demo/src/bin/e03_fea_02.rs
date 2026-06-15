static mut COUNTER: i32 = 0;

fn add_to_count(inc: i32) {
    unsafe {
        COUNTER += inc;
    }
}

fn main() {
    add_to_count(3);

    unsafe {
        //访问可变静态类型是 unsafe 的
        assert_eq!(3, COUNTER)
    };
}
