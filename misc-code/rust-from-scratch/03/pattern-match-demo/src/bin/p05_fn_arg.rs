//函数参数也可以是模式匹配：一
fn foo(_x: i32) {}
//函数参数也可以是模式匹配：二
fn print_coordinates(&(x, y): &(i32, i32)) {
    println!("({}, {})", x, y);
}

fn main() {
    foo(5);

    let point = (3, 5);
    print_coordinates(&point);
}
