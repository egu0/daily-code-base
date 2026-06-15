use std::collections::HashMap;
use std::collections::LinkedList;

fn main() {
    println!("{}", roman_to_int(String::from("III"))); //3
    println!("{}", roman_to_int(String::from("LVIII"))); //58
    println!("{}", roman_to_int(String::from("MCMXCIV"))); //1994

    println!(
        "{}",
        longest_common_prefix(vec![
            "flower".to_string(),
            "flow".to_string(),
            "flight".to_string()
        ])
    );

    println!("{}", is_valid("{[]()".to_string()));

    println!("{}", str_str("haystack".to_string(), "a".to_string()));
    println!("{}", str_str("haystack".to_string(), "holy".to_string()));

    println!("{}", length_of_last_word("s".to_string()));
    println!("{}", length_of_last_word("abc sorry".to_string()));
    println!("{}", length_of_last_word(" a xor no debet ".to_string()));

    println!("{}", add_binary("100".to_string(), "11".to_string())); //111
    println!("{}", add_binary("0".to_string(), "0".to_string())); //0
    println!("{}", add_binary("111".to_string(), "11".to_string())); //1010
    println!("{}", add_binary("100".to_string(), "110010".to_string())); //110110
}

//P#13
pub fn roman_to_int(s: String) -> i32 {
    let mut romans_weight: HashMap<u8, i32> = HashMap::new();
    romans_weight.insert(b'I', 1);
    romans_weight.insert(b'V', 5);
    romans_weight.insert(b'X', 10);
    romans_weight.insert(b'L', 50);
    romans_weight.insert(b'C', 100);
    romans_weight.insert(b'D', 500);
    romans_weight.insert(b'M', 1000);

    let mut res: i32 = 0;
    let bs = s.as_bytes();
    let mut i = 0;
    while i < s.len() {
        let b = romans_weight.get(&bs[i]).unwrap();
        res += b;
        if i > 0 {
            let a = romans_weight.get(&bs[i - 1]).unwrap();
            if a < b {
                res -= 2 * a;
            }
        }
        i += 1;
    }
    res
}

//P#14
pub fn longest_common_prefix(strs: Vec<String>) -> String {
    if strs.is_empty() {
        return "".to_string();
    }
    let mut idx = 0;
    let pivot = &strs[0];
    if pivot.is_empty() {
        return "".to_string();
    }
    for i in 0..pivot.len() {
        //基准字符
        let ch = pivot.as_bytes()[i];
        //校验其他字符串
        //for j in 1..strs.len() {
        for j in strs.iter().skip(1) {
            if i >= j.len() || j.as_bytes()[i] != ch {
                return String::from(&pivot[0..idx]);
            }
        }
        idx += 1;
    }
    String::from(&pivot[0..idx])
}

//P#20
pub fn is_valid(s: String) -> bool {
    let mut ll: LinkedList<u8> = LinkedList::new();
    for &b in s.as_bytes() {
        if b == b']' || b == b')' || b == b'}' {
            if ll.is_empty() {
                return false;
            } else {
                let top = ll.pop_back().unwrap();
                if (b == b']' && top == b'[')
                    || (b == b')' && top == b'(')
                    || (b == b'}' && top == b'{')
                {
                    continue;
                } else {
                    return false;
                }
            }
        }
        ll.push_back(b);
    }

    ll.is_empty()
}

//P#28
//return the index of the first occurrence of needle in haystack, or -1 if needle is not part of haystack.
//KMP: https://zh.wikipedia.org/wiki/KMP%E7%AE%97%E6%B3%95
pub fn str_str(haystack: String, needle: String) -> i32 {
    match haystack.find(needle.as_str()) {
        Some(data) => data as i32,
        None => -1,
    }
}

//P#58
pub fn length_of_last_word(s: String) -> i32 {
    let trimmed = s.trim();
    let ps = trimmed.split(' ');
    let mut len = 0;
    for item in ps.into_iter() {
        len = item.len();
    }
    len as i32
    /*
    //链式操作
    s.trim()
            .split_whitespace()
            .last()
            .unwrap()
            .to_string()
            .len() as i32
    */
}

//P#67
//todo: 0ms
pub fn add_binary(a: String, b: String) -> String {
    let mut res = String::new();
    let bs1 = a.as_bytes();
    let bs2 = b.as_bytes();
    let mut i1 = bs1.len(); // index range: [1, s.len()]
    let mut i2 = bs2.len(); // index = 0: indicates over of specific iteration
    let mut car = 0;

    // stop cond: i1==0 && i2==0
    while i1 != 0 || i2 != 0 {
        let mut x = car;
        if i1 > 0 {
            x += bs1[i1 - 1];
            x -= 48;
            i1 -= 1;
        }
        if i2 > 0 {
            x += bs2[i2 - 1];
            x -= 48;
            i2 -= 1;
        }
        res.insert(0, (x % 2 + 48) as char);
        car = x / 2;
    }

    if car == 1 {
        res.insert(0, '1');
    } else if res.starts_with('0') && res.len() > 1 {
        res.remove(0);
    }

    res
}

pub fn add_binary2(a: String, b: String) -> String {
    let mut res = String::new();
    let mut i1 = a.as_bytes().len();
    let mut i2 = b.as_bytes().len();
    let mut carry = 0;

    // 使用 while 语句替换 loop 语句后，时间减少明显！！！
    while i1 != 0 || i2 != 0 {
        let mut x: u8 = carry;
        if i1 > 0 {
            x += a.chars().nth(i1 - 1).unwrap_or('0').to_digit(10).unwrap() as u8;
            i1 -= 1;
        }
        if i2 > 0 {
            x += b.chars().nth(i2 - 1).unwrap_or('0').to_digit(10).unwrap() as u8;
            i2 -= 1;
        }
        res.insert(0, (x % 2 + 48) as char);
        carry = x / 2;
    }

    if carry == 1 {
        res.insert(0, '1');
    } else if res.starts_with('0') && res.len() > 1 {
        res.remove(0);
    }

    res
}
