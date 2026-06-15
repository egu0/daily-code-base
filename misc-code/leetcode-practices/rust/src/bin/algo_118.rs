fn main() {
    assert_eq!(vec![vec![1]], Solution::generate(1));
    assert_eq!(vec![vec![1], vec![1, 1]], Solution::generate(2));
    assert_eq!(
        vec![vec![1], vec![1, 1], vec![1, 2, 1]],
        Solution::generate(3)
    );
    assert_eq!(
        vec![vec![1], vec![1, 1], vec![1, 2, 1], vec![1, 3, 3, 1]],
        Solution::generate(4)
    );
}

struct Solution;

impl Solution {
    pub fn generate(n: i32) -> Vec<Vec<i32>> {
        let n = n as usize;
        let mut res = vec![];
        res.push(vec![1]);

        for i in 1..n {
            let mut v = vec![1];

            for j in 0..(i - 1) {
                v.push(res[i - 1][j] + res[i - 1][j + 1]);
            }

            v.push(1);
            res.push(v);
        }

        res
    }
}
