fn main() {
    assert_eq!(1, Solution::title_to_number(String::from("A")));
    assert_eq!(26, Solution::title_to_number(String::from("Z")));
    assert_eq!(28, Solution::title_to_number(String::from("AB")));
}

struct Solution;

impl Solution {
    pub fn title_to_number(column_title: String) -> i32 {
        let mut res: i32 = 0;
        for b in column_title.as_bytes() {
            res *= 26;
            res += (b - 64) as i32;
        }

        res
    }
}
