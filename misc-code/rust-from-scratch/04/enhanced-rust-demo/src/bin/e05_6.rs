use std::fmt::Display;

struct WrapperType(Vec<String>);

impl Display for WrapperType {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "[{}]", self.0.join(" | "))
    }
}

fn main() {
    let n = WrapperType(vec![
        String::from("tomy"),
        String::from("jetty"),
        String::from("katy"),
    ]);
    println!("{}", n);
}
