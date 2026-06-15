use std::cmp::Ordering;

fn main() {
    println!("{}", is_palindrome(11));
}

//P#9; O(N); 0ms
fn is_palindrome(mut n: i32) -> bool {
    match n.cmp(&0) {
        Ordering::Greater => false,
        Ordering::Equal => true,
        Ordering::Less => {
            let mut digs: Vec<u8> = Vec::new();
            loop {
                if n == 0 {
                    break;
                }
                digs.push((n % 10) as u8);
                n /= 10;
            }
            let mut i = 0usize;
            let mut j = digs.len() - 1;
            loop {
                if i >= j {
                    return true;
                }
                if digs[i] == digs[j] {
                    i += 1;
                    j -= 1;
                } else {
                    return false;
                }
            }
        }
    }
}
