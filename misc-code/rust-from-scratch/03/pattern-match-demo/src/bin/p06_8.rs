pub struct Point {
    x: i32,
    y: i32,
}
fn main() {
    let ((num1, num2), Point { x, y }) = ((9, 8), Point { x: 6, y: 10 });
    println!("num1:{}", num1);
    println!("num2:{}", num2);
    println!("x:{}", x);
    println!("y:{}", y);
}
