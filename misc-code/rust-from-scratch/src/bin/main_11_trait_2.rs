fn main() {
    let mut cat = Category::new();
    cat.add("apple");
    cat.add("peach");
    let m = cat.largest();
    println!("{}", m);

    let mut cat = Category::new();
    cat.add("apple".to_string());
    cat.add("peach".to_string());
    let m = cat.largest_one();
    println!("{}", m);

    //the method `larger_copy` exists for struct `Pair<String>`,
    //but its trait bounds were not satisfied
    //错误，因为 String 类型没有实现 Copy trait，不能满足 trait bounds，故不能调用 impl 块中定义的函数
    // cat.largest(); //Error

    1.to_string();
}

struct Category<T> {
    id_list: Vec<T>,
}

//impl 块的泛型参数没有指定 trait bounds
impl<T> Category<T> {
    fn new() -> Self {
        Category { id_list: vec![] }
    }
    fn add(&mut self, item: T) {
        self.id_list.push(item);
    }
}

//impl 块的泛型参数指定了 trait bounds，实现了这些 trait 的类型可以调用其中定义的方法，比如 i32
impl<T: PartialOrd + Copy> Category<T> {
    fn largest(&self) -> T {
        let mut max = self.id_list[0];
        for i in 1..self.id_list.len() {
            if self.id_list[i] > max {
                max = self.id_list[i]
            }
        }
        max
    }
}

//impl 块的泛型参数指定了 trait bounds，实现了这些 trait 的类型可以调用其中定义的方法，比如 String 类型
impl<T: PartialOrd + Clone> Category<T> {
    fn largest_one(&self) -> &T {
        let mut max = &self.id_list[0];
        for i in 1..self.id_list.len() {
            if &self.id_list[i] > max {
                max = &self.id_list[i]
            }
        }
        max
    }
}
