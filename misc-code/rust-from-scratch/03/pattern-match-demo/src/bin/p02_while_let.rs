fn main() {
    let mut nums = vec![1, 2, 3];

    while let Some(num) = nums.pop() {
        println!("Got: {}", num);
    }
}
