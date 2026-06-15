fn hi(name: &String) {
    println!("Hi, {}!", name);
}

fn hello(name: &str) {
    println!("Hello, {}!", name);
}

fn ni_hao(name: &str) {
    println!("你好, {}！", name);
}

fn main() {
    let b = MyBox::new(String::from("eugene"));
    // 转换一：&MyBox -> &String
    hi(&b);
    // 转换二：&String -> &str
    hello(&String::from("guo"));
    // 转换一 + 转换二
    ni_hao(&b);
}

struct MyBox<T>(T);

impl<T> MyBox<T> {
    fn new(x: T) -> Self {
        MyBox(x)
    }
}

impl<T> std::ops::Deref for MyBox<T> {
    //指定关联类型
    type Target = T;

    //解引用方法，参数为 &self，返回值为 &T
    fn deref(&self) -> &Self::Target {
        &self.0
    }
}
