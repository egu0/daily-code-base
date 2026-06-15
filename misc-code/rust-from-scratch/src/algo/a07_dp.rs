use std::collections::HashMap;

fn main() {
    println!("{}", Solution::climb_stairs(45));
}

#[allow(dead_code)]
struct Solution {}
impl Solution {
    //P#70
    //通俗易读的递归
    #[allow(dead_code)]
    pub fn climb_stairs2(n: i32) -> i32 {
        if n <= 2 {
            return n;
        } else {
            return Solution::climb_stairs(n - 1) + Solution::climb_stairs(n - 2);
        }
    }

    //备忘录
    #[allow(dead_code)]
    pub fn climb_stairs3(n: i32) -> i32 {
        return Solution::process(n, &mut HashMap::new());
    }
    fn process(n: i32, map: &mut HashMap<i32, i32>) -> i32 {
        if map.contains_key(&n) {
            return *map.get(&n).unwrap();
        }
        #[warn(unused_variables)]
        let t: i32;
        if n <= 2 {
            t = n;
        } else {
            t = Solution::process(n - 1, map) + Solution::process(n - 2, map);
        }
        map.insert(n, t);
        return t;
    }

    //DP
    pub fn climb_stairs(n: i32) -> i32 {
        let mut x: [i32; 46] = [1; 46];
        x[2] = 2;
        for i in 3..=(n as usize) {
            x[i] = x[i - 1] + x[i - 2];
        }
        return x[n as usize];
    }
}
