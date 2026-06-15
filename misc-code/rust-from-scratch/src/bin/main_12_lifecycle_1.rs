fn main() {
    let s1 = String::from("abc");
    let s2 = String::from("wxyz");
    let res = longest(s1.as_str(), s2.as_str());
    println!("res:{}", res);
}

#[allow(dead_code)]
//在函数名后通过 <'a> 声明生命周期，并在方法参数和返回值中使用它
fn longest<'a>(x: &'a str, y: &'a str) -> &'a str {
    if x.len() > y.len() {
        x
    } else {
        y
    }
}

#[allow(dead_code)]
fn first_char(s: &str) -> &str {
    let bs = s.as_bytes();
    for (i, &item) in bs.iter().enumerate() {
        if item == b' ' {
            return &s[0..i];
        }
    }
    return &s[..];
}
