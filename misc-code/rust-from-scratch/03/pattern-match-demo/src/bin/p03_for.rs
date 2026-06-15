fn main() {
    let v = vec!['a', 'b', 'c'];

    for (idx, ch) in v.iter().enumerate() {
        println!("arr[{}]={}", idx, ch);
    }
}
