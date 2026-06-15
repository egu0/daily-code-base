fn main() {
    assert_eq!(Solution::max_profit(vec![7, 1, 5, 3, 6, 4]), 5);
    assert_eq!(Solution::max_profit(vec![7, 6, 5, 4, 3, 2]), 0);
}

struct Solution;

impl Solution {
    pub fn max_profit(prices: Vec<i32>) -> i32 {
        let mut min_price = 10001;
        let mut max_profit = 0;

        for p in prices.iter() {
            let p = *p;

            if p < min_price {
                min_price = p;
                continue;
            }

            let profit = p - min_price;
            if profit > max_profit {
                max_profit = profit;
            }
        }

        max_profit
    }
}
