fn add_one(x: i32) -> i32 {
    x + 1
}

// fn(i32) -> i32   表示函数指针类型
fn do_twice(f: fn(i32) -> i32, arg: i32) -> i32 {
    f(arg) + f(arg)
}

fn main() {
    let fn_ptr: fn(i32) -> i32 = add_one; //函数指针类型
    println!("do twice: {}", do_twice(fn_ptr, 1));
}
