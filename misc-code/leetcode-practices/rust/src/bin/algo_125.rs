fn main() {
    assert!(Solution::is_palindrome(String::from(
        "A man, a plan, a canal: Panama"
    )));
    assert!(!Solution::is_palindrome(String::from("race a car")));
    assert!(Solution::is_palindrome(String::from(" ")));
    assert!(!Solution::is_palindrome(String::from("0P")));
}

struct Solution;

impl Solution {
    pub fn is_palindrome(mut s: String) -> bool {
        s = s
            .to_lowercase()
            .chars()
            .filter(char::is_ascii_alphanumeric)
            .collect();
        s.chars().rev().collect::<String>() == s.as_str()
    }

    /*
    pub fn is_palindrome(mut s: String) -> bool {
        s = s.to_lowercase().chars().filter(|c| ('a' <= *c && *c <= 'z') || ('0' <= *c && *c <= '9')).collect();
        s.chars().rev().collect::<String>()  == s.as_str()
    }
    */
}
