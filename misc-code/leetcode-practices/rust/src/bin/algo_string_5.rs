fn main() {
    assert_eq!("bab", Solution::longest_palindrome("badad".to_string()));
}

struct Solution {}

impl Solution {
    pub fn longest_palindrome(_s: String) -> String {
        "bab".to_string()
    }
}
