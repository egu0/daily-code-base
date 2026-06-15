use std::fmt::{Debug, Display};

fn main() {
    let n = NewsArticle {
        headline: "a big announcement".to_string(),
        location: "alaska".to_string(),
        author: "bob".to_string(),
        content: "blablabla...".to_string(),
    };
    let s = n.summarize();
    println!("summary:{}", s);
}

#[allow(dead_code)]
fn do_task1(_sum: impl Summary + Display) {}

#[allow(dead_code)]
fn do_task2<T: Summary + Display, U: Clone + Debug>(_sum: T, _check: U) {}

#[allow(dead_code)]
fn do_task3<T, U>(_sum: T, _check: U)
where
    T: Summary + Display,
    U: Clone + Debug,
{
}

#[allow(dead_code)]
fn do_task4() -> impl Summary {
    Tweet {
        username: "elon".to_string(),
        content: "yeah".to_string(),
        reply: true,
        retweet: false,
    }
    // NewsArticle {
    //     headline: "a big announcement".to_string(),
    //     location: "alaska".to_string(),
    //     author: "bob".to_string(),
    //     content: "blablabla...".to_string(),
    // }
}

pub struct NewsArticle {
    pub headline: String,
    pub location: String,
    pub author: String,
    pub content: String,
}

pub struct Tweet {
    pub username: String,
    pub content: String,
    pub reply: bool,
    pub retweet: bool,
}

trait Summary {
    fn summarize(&self) -> String {
        "read more...".to_string()
    }
}

//为两个类型实现 Summary trait

impl Summary for NewsArticle {
    fn summarize(&self) -> String {
        format!("{}, by {} ({})", self.headline, self.author, self.location)
    }
}
impl Summary for Tweet {
    fn summarize(&self) -> String {
        format!("{}: {}", self.username, self.content)
    }
}
