fn main() {
    assert_eq!(Solution::convert_to_title(1), String::from("A"));
    assert_eq!(Solution::convert_to_title(26), String::from("Z"));
    assert_eq!(Solution::convert_to_title(27), String::from("AA"));
    assert_eq!(Solution::convert_to_title(28), String::from("AB"));
}

struct Solution;

impl Solution {
    pub fn convert_to_title(mut n: i32) -> String {
        let mut s = String::new();

        while n != 0 {
            n -= 1;
            let m = (n % 26) as u8;
            let c = (m + 65) as char;
            s.push(c);
            n /= 26;
        }

        let mut r = String::new();
        for ch in s.chars() {
            r.insert(0, ch);
        }
        r
    }
}
