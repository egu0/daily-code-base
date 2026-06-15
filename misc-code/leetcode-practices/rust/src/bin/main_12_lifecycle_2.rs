use std::fmt::Display;

fn main() {
    let novel = String::from("Call me Ishmael. Some years ago ...");
    let first_sentence = novel.split('.').next().expect("Could not found a '.'");
    let _i = ImportantExcerpt {
        part: first_sentence,
    };

    // 这段代码中，first_sentence 的存活时间比 i 的存活时间长，所以不会出错
}

#[allow(dead_code)]
fn longest_with_an_announcement<'a, T>(x: &'a str, y: &'a str, ann: T) -> &'a str
where
    T: Display + Clone,
{
    println!("Announcement: {}", ann);
    if x.len() > y.len() {
        x
    } else {
        y
    }
}

#[allow(dead_code)]
//在结构体名后声明生命周期参数
struct ImportantExcerpt<'a> {
    //需要为每个引用类型添加生命周期标注
    part: &'a str,
}

#[allow(dead_code)]
impl<'a> ImportantExcerpt<'a> {
    fn level(&self) -> i32 {
        3
    }
    fn annouce_and_return_part(&self, announcement: &str) -> &str {
        println!("attention:{}", announcement);
        self.part
    }
}
