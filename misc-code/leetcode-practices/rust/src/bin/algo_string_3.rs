use std::collections::HashMap;

fn main() {
    assert!(Solution::length_of_longest_substring("abbbba".to_string()) == 2);
    assert!(Solution::length_of_longest_substring("abba".to_string()) == 2);
    assert!(Solution::length_of_longest_substring("".to_string()) == 0);
    assert!(Solution::length_of_longest_substring("a".to_string()) == 1);
    assert!(Solution::length_of_longest_substring("aa".to_string()) == 1);
    assert!(Solution::length_of_longest_substring("ab".to_string()) == 2);
    assert!(Solution::length_of_longest_substring("abc".to_string()) == 3);
    assert!(Solution::length_of_longest_substring("aaaab".to_string()) == 2);
    assert!(Solution::length_of_longest_substring("abbbb".to_string()) == 2);
    assert!(Solution::length_of_longest_substring("aabbcc".to_string()) == 2);
    assert!(Solution::length_of_longest_substring("bbbabbb".to_string()) == 2);
    assert!(Solution::length_of_longest_substring("abcabcbb".to_string()) == 3);
    assert!(Solution::length_of_longest_substring("bbbbb".to_string()) == 1);
    assert!(Solution::length_of_longest_substring("pwwkew".to_string()) == 3);
}

struct Solution {}

impl Solution {
    pub fn length_of_longest_substring(s: String) -> i32 {
        let mut map: HashMap<char, usize> = HashMap::new();
        let mut head = 0;
        let mut max = 0;

        let chars = s.chars();
        for (pos, cur_ch) in chars.enumerate() {
            if map.contains_key(&cur_ch) {
                let old_idx = map.get(&cur_ch).unwrap();
                head = old_idx + 1;

                let mut chs: Vec<char> = vec![];
                let i = map.iter();
                for (k, v) in i {
                    if *v < head {
                        chs.push(*k);
                    }
                }
                for c in chs.iter() {
                    map.remove(c);
                }
            } else {
                let new_len = pos - head + 1;
                if new_len > max {
                    max = new_len;
                }
            }
            map.insert(cur_ch, pos);
        }

        max as i32
    }
}
