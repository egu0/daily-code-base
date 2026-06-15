fn main() {
    assert!(Solution::is_happy(1));
    assert!(Solution::is_happy(7));
    assert!(Solution::is_happy(19));
    assert!(Solution::is_happy(18));
}

struct Solution;

impl Solution {
    //https://leetcode.com/problems/happy-number/description/comments/1575497
    pub fn is_happy(mut n: i32) -> bool {
        if n < 10 {
            /*
                match n {
                    1 | 7 => true,
                    _ => false,
                }
            */
            matches!(n, 1 | 7)
        } else {
            let mut sum = 0;
            while n != 0 {
                let m = n % 10;
                sum += m * m;
                n /= 10;
            }
            Solution::is_happy(sum)
        }
    }
}
