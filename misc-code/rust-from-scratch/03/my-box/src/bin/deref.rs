use std::ops::Deref;

fn main() {
    let x = 5;
    let y: MyBox<i32> = MyBox::new(x);

    assert_eq!(5, x);
    assert_eq!(5, *y);
    assert_eq!(5, *(y.deref())); //编译器会将 *y 展开为这种方式
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
