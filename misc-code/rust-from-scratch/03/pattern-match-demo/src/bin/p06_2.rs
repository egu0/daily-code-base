fn main() {
    let x = Some(5);
    let y = 10;

    match x {
        Some(50) => println!("Got 50!"),
        // Got 5!，y 是命名变量，可以匹配任何值，与作用域中的 y 没有关系
        Some(y) => println!("Got {:?}!", y),
        _ => println!("Met default case, x is {:?}", x),
    }

    println!("x = {:?}, y = {:?}", x, y); // x=Some(5), y=10
}
