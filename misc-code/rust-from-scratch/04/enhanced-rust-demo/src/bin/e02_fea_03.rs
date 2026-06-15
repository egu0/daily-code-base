extern "C" {
    fn abs(input: i32) -> i32;
}
fn main() {
    unsafe {
        //需要在 safe 中调用外部代码
        let x = abs(-3);
        println!("abs(-3) is {}", x);
    }
}

#[no_mangle]
pub extern "C" fn call_from_c() {
    println!("called a rust function from c!");
}
