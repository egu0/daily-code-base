/// 通过 pub 控制 struct 中字段的可见性，外部代码无法直接访问非 pub 字段，
/// 需要通过公开 API 暴露它们
pub mod 可见性 {
    #[allow(dead_code)]
    #[derive(Debug)]
    pub struct ArtCollection {
        all: Vec<String>,
        owner: String,
    }

    #[allow(dead_code)]
    impl ArtCollection {
        pub fn new() -> Self {
            ArtCollection {
                all: vec![],
                owner: String::new(),
            }
        }
        pub fn all(&self) -> &Vec<String> {
            &self.all
        }
        pub fn all_mut(&mut self) -> &mut Vec<String> {
            &mut self.all
        }
        pub fn add_one(&mut self, col: String) {
            self.all.push(col);
        }
        pub fn owner(&self) -> &String {
            &self.owner
        }
        pub fn set_owner(&mut self, new_owner: String) {
            self.owner = new_owner;
        }
    }
}

// 面向对象练习：实现一个 gui
pub mod gui {
    pub trait Draw {
        fn draw(&self);
    }

    /// Q：为啥不使用泛型 + trait bounds 的写法？
    /// A：使用泛型时只能在 components 中存放一种 Draw trait 的实现
    ///    pub struct Screen<T: Draw> {
    ///        pub components: Vec<T>,
    ///    }
    pub struct Screen {
        // <dyn Draw> 表示实现了 Draw 的类型
        pub components: Vec<Box<dyn Draw>>,
    }

    impl Screen {
        pub fn run(&self) {
            for comp in self.components.iter() {
                comp.draw();
            }
        }
    }

    pub struct Button {
        pub label: String,
    }
    impl Draw for Button {
        fn draw(&self) {
            println!("drawing button, label is {:?}", self.label);
        }
    }

    pub struct TextFiled {
        pub content: String,
    }
    impl Draw for TextFiled {
        fn draw(&self) {
            println!("drawing textfield, content is {:?}", self.content);
        }
    }
}

/*
mod xxx {
    pub trait Clone {
        fn coloe(&self) -> Self;
    }

    pub struct Collection {
        // Clone 是一个 trait，无法作为一个类型使用，而需要用 dyn 修饰：<dyn Clone>
        // 报错：the trait `xxx::Clone` cannot be made into an object
        pub elements: Vec<Box<dyn Clone>>,
    }
}
*/
