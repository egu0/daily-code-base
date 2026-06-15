struct Point {
    x: i32,
    y: i32,
}

fn main() {
    let p = Point { x: 0, y: 8 };

    //省略写法
    let Point { x, y } = p;
    assert_eq!(0, x);
    assert_eq!(8, y);

    //为字段起别名
    let Point { x: a, y: b } = p;
    assert_eq!(0, a);
    assert_eq!(8, b);

    match p {
        //y: b 表示为 y 起别名 b
        Point { x: 0, y: b } => println!("(0, {})", b),
        Point { x, y: 0 } => println!("({}, 0)", x),
        Point { x, y } => println!("({}, {})", x, y),
    }
}
