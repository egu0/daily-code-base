pub mod math_util {
    pub fn add(a: i32, b: i32) -> i32 {
        a + b
    }
}

pub mod abc {
    #[allow(dead_code)]
    pub fn multiply(a: i32, b: i32) -> i32 {
        a * b
    }
}

#[allow(dead_code)]
pub fn divide(a: i32, b: i32) -> i32 {
    a / b
}
