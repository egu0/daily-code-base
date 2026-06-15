/*
pub trait Iterator<T> {
    fn next(&mut self) -> Option<T>;
}

pub struct Person {}
impl Iterator<i32> for Person {
    fn next(&mut self) -> Option<i32> {
        Some(1)
    }
}
impl Iterator<String> for Person {
    fn next(&mut self) -> Option<String> {
        Some(String::new())
    }
}

pub trait Iterator {
    type Item;

    fn next(&mut self) -> Option<Self::Item>;
}

pub struct Stu {}
impl Iterator for Stu {
    type Item = i32;

    fn next(&mut self) -> Option<Self::Item> {
        Some(1)
    }
}
*/
fn main() {}
