fn main() {
    assert_eq!(vec![1], Solution::get_row(0));
    assert_eq!(vec![1,1], Solution::get_row(1));
    assert_eq!(vec![1,2,1], Solution::get_row(2));
    assert_eq!(vec![1,3,3,1], Solution::get_row(3));
}

struct Solution;

impl Solution {
    pub fn get_row(row_num: i32) -> Vec<i32> {
        let row_num = row_num as usize;
        let mut res = vec![1];
        for i in 1..(row_num + 1) {
            let mut v = vec![1];
            for j in 0..(i - 1) {
                v.push(res[j] + res[j + 1]);
            }
            v.push(1);

            res = v;
        }
        res
    }
}
