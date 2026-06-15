//use std::collections::HashMap;

fn main() {
    assert_eq!(3, Solution::majority_element(vec![3, 2, 3]));
    assert_eq!(2, Solution::majority_element(vec![2, 2, 1, 1, 1, 2, 2]));
    assert_eq!(1, Solution::majority_element(vec![1, 1, 2]));
}

struct Solution;

impl Solution {
    //Could you solve the problem in linear time and in O(1) space?
    pub fn majority_element(nums: Vec<i32>) -> i32 {
        println!("{:?}", nums);
        0
    }

    /*
    pub fn majority_element(nums: Vec<i32>) -> i32 {
        let mut map: HashMap<i32, i32> = HashMap::new();

        for e in nums.iter() {
            if !map.contains_key(e) {
                map.insert(*e, 1);
            } else {
                let count = map.get(e).unwrap();
                map.insert(*e, count + 1);
            }
        }

        let size = (nums.len() / 2) as i32;
        for (key, value) in map.iter() {
            if *value > size {
                return *key;
            }
        }

        0
    }
    */
}
