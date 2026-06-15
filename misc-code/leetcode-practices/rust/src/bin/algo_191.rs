fn main() {
    assert_eq!(Solution::hamming_weight(11), 3);
    assert_eq!(Solution::hamming_weight(128), 1);
    assert_eq!(Solution::hamming_weight(1), 1);
}

struct Solution;

impl Solution {
    pub fn hamming_weight(mut n: i32) -> i32 {
        let mut num = 0;
        while n != 0 {
            num += 1;
            //clear lowest digit of 1 flag
            n = n & (n - 1);
        }
        num
    }
}
