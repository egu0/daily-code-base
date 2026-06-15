#[allow(dead_code)]
struct Point {
    x: i32,
    y: i32,
    z: i32,
}
fn main() {
    let point = Point { x: 0, y: 1, z: 2 };
    match point {
        Point { x, .. } => println!("x is {}", x),
    }

    //-------------
    let nums = (2, 4, 6, 8, 10, 12);
    match nums {
        (first, .., last) => {
            println!("first is {}, last is {}", first, last)
        }
    }
}
