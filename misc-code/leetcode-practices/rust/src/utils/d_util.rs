use std::time::SystemTime;

pub fn now() -> String {
    let now = SystemTime::now();
    let s: String = format!("{:?}", now);
    s
}
