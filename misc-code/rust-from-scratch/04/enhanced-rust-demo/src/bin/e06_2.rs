/*
fn takes_long_type(f: Box<dyn Fn() + Send + 'static>) {}
fn returns_long_type() -> Box<dyn Fn() + Send + 'static> {
    Box::new(|| println!("hi"))
}
fn main() {
    let f: Box<dyn Fn() + Send + 'static> = Box::new(|| println!("hi"));
}
*/

/*
//使用类型别名优化代码
type Thunk = Box<dyn Fn() + Send + 'static>;
fn takes_long_type(f: Thunk) {}
fn returns_long_type() -> Thunk {
    Box::new(|| println!("hi"))
}
fn main() {
    let f: Thunk = Box::new(|| println!("hi"));
}
*/
fn main() {}
