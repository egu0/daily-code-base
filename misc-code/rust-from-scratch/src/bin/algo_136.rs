fn main() {
    assert_eq!(Solution::single_number(vec![1,2,3,2,3]), 1);
    assert_eq!(Solution::single_number(vec![2,1,2]), 1);
    assert_eq!(Solution::single_number(vec![2,2,1]), 1);
    assert_eq!(Solution::single_number(vec![1]), 1);
}

struct Solution;


/*
 * XOR
 *      1^1 = 0
 *      1^1^2 = 2
 *      1^1^2^2 = 0
 *      ...
 */

impl Solution {
    pub fn single_number(nums: Vec<i32>) -> i32 {
        let mut x = 0;
        for i in nums.iter() {
            x = x ^ * i;
        }
        x
    }
}
