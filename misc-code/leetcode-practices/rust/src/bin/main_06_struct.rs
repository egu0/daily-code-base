fn main() {
    let mut u1 = User {
        email: String::from("value@a.com"),
        username: "value".to_string(),
        age: 18,
        male: true,
    };
    u1 = User {
        email: "vv@a.com".to_string(),
        username: "vv".to_string(),
        ..u1
    };
    println!("{:?}", u1);
    println!("{:#?}", u1);

    // ----
    let mut u = User::new();
    println!("{:?}", u);
    u.set_age(12);
    println!("{:?}", u);
}

//派生 Debug 这个 trait 后，可以通过 :? 或 :#? 打印结构体实例
#[derive(Debug)]
#[allow(dead_code)]
struct User {
    email: String,
    username: String,
    age: u32,
    male: bool,
}

impl User {
    // 方法
    fn set_age(&mut self, new_age: u32) {
        self.age = new_age;
    }

    // 关联函数，通过 User:: 调用
    fn new() -> Self {
        User {
            email: String::new(),
            username: String::new(),
            age: 0,
            male: true,
        }
    }
}
