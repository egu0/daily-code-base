
fn main() {
    assert_eq!(true, Solution::is_happy(1));
    assert_eq!(true, Solution::is_happy(7));
    assert_eq!(true, Solution::is_happy(19));
    assert_eq!(false, Solution::is_happy(18));
}

struct Solution;

impl Solution {
    //https://leetcode.com/problems/happy-number/description/comments/1575497
    pub fn is_happy(mut n: i32) -> bool {
        if n < 10 {
            match n {
                1 | 7 => return true,
                _ => return false,
            }
        } else {
            let mut sum = 0;
            while n != 0 {
                let m = n % 10;
                sum += m * m;
                n /= 10;
            }
            return Solution::is_happy(sum);
        }
    }
}
