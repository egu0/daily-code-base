use core::cmp::Ordering;

fn main() {}

#[allow(dead_code)]
fn test_my_sqrt() {
    // println!("{}", my_sqrt(0));
    // println!("{}", my_sqrt(1));
    // println!("{}", my_sqrt(2));
    // println!("{}", my_sqrt(3));
    // println!("{}", my_sqrt(4));
    // println!("{}", my_sqrt(5));
    println!("{}", my_sqrt(9));
    println!("{}", my_sqrt(10));
    println!("{}", my_sqrt(11));
    println!("{}", my_sqrt(16));
    println!("{}", my_sqrt(17));
    println!("{}", my_sqrt(24));
    println!("{}", my_sqrt(25));
    println!("{}", my_sqrt(2147395600));
    println!("{}", my_sqrt(2147395600));
    println!("{}", my_sqrt(i32::MAX));
    println!("{}", my_sqrt(2147483647));
    // let x = 5 * 4 / 3;
    // println!("{}", x);
}

//P#69; 0 <= x <= 2^31 - 1
//Linear Search, O(N)
pub fn my_sqrt2(x: i32) -> i32 {
    let max: i64 = 46341;
    let x2 = x as i64;
    for e in 0..=max {
        if e * e > x2 {
            return e as i32 - 1;
        }
    }
    0
}

//Binary Search, O(logN), 0ms
pub fn my_sqrt(x: i32) -> i32 {
    println!("------\nx={}", x);
    let mut left: i64 = 0;
    let mut right: i64 = 46341;
    let x2 = x as i64;
    while left <= right {
        // println!("{}\t{}", left, right);
        let mid: i64 = (left + right) / 2;
        let double = mid * mid;

        /*
        if double > x2 {
            right = mid;
        } else if double < x2 {
            left = mid;
        } else {
            return mid as i32;
        }
        */
        match double.cmp(&x2) {
            Ordering::Greater => right = mid,
            Ordering::Less => left = mid,
            Ordering::Equal => return mid as i32,
        }
        if left + 1 == right {
            return left as i32;
        }
    }
    0
}

// 0,1,,4,,9,,16,,25,,36,,49,,64,,81,,100,,,,,X,,,,,,,MAX
// 0 1  2  3   4   5   6   7   8   9   10   46340
