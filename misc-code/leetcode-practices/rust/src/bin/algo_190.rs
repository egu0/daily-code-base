fn main() {
    assert_eq!(Solution::reverse_bits(43261596), 964176192);
    assert_eq!(Solution::reverse_bits(4294967293), 3221225471);
}

struct Solution;

impl Solution {
    pub fn reverse_bits(mut x: u32) -> u32 {
        let mut bit;
        let mut res: u32 = 0;
        let mut base: u32 = 0b1000_0000_0000_0000_0000_0000_0000_0000;
        for _ in 0..32 {
            bit = x % 2;
            res += bit * base;

            base >>= 1;
            x /= 2;
        }

        res
    }
}
