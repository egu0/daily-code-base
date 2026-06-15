fn main() {
    let o = Some(4);
    let p = Some(4);
    let num = 6;

    if let Some(i) = o {
        println!("{}", i);
    } else if num > 5 {
        println!("num: {}", num);
    } else if let Some(j) = p {
        println!("{}", j);
    } else {
        println!("else");
    }
}
