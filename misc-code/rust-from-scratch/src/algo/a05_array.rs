use std::vec;

fn main() {
    let r = plus_one(vec![1, 2, 3]);
    println!("{:?}", r);
    let r = plus_one(vec![1, 9, 9]);
    println!("{:?}", r);
    let r = plus_one(vec![9, 9]);
    println!("{:?}", r);

    merge2(&mut vec![1, 2, 4, 5, 6, 0], 5, &mut vec![3], 1);
}

#[allow(dead_code)]
fn test_search_insert() {
    println!("{}", search_insert(vec![1, 3, 5, 6], 5)); //2
    println!("{}", search_insert(vec![1, 3, 5, 6], 2)); //1
    println!("{}", search_insert(vec![1, 3, 5, 6], 7)); //4
    println!("{}", search_insert(vec![1, 3, 5, 6], 0)); //0
    println!("---");
    println!("{}", search_insert(vec![1, 3], 0)); //0
    println!("{}", search_insert(vec![1, 3], 2)); //1
    println!("{}", search_insert(vec![1, 3], 1)); //0
    println!("{}", search_insert(vec![1, 3], 3)); //1
    println!("{}", search_insert(vec![1, 3], 4)); //2
    println!("---");
    println!("{}", search_insert(vec![3, 3], 1)); //0
    println!("{}", search_insert(vec![3, 3], 3)); //0
    println!("{}", search_insert(vec![3, 3], 4)); //2
    println!("---");
    println!("{}", search_insert(vec![1], 1)); //0
    println!("{}", search_insert(vec![1], 0)); //0
    println!("{}", search_insert(vec![1], 3)); //1
}

//P#35
#[allow(dead_code)]
pub fn search_insert(nums: Vec<i32>, target: i32) -> i32 {
    let mut left = 0;
    let mut right = nums.len() - 1;
    if left == right {
        let ele = nums[0];
        if ele >= target {
            return 0;
        } else {
            return 1;
        }
    }
    loop {
        if left + 1 == right {
            if nums[right] < target {
                return (right + 1) as i32;
            } else if target <= nums[left] {
                return left as i32;
            } else {
                return right as i32;
            }
        }
        let mid = (right + left) / 2;
        if nums[mid] < target {
            left = mid;
        } else if nums[mid] >= target {
            right = mid;
        }
    }
}

#[allow(dead_code)]
fn test_remove_element() {
    println!("{:?}", remove_element(&mut vec![1, 2, 2, 2,], 2)); //1
    println!("{:?}", remove_element(&mut vec![2, 1], 2)); //1
    println!("{:?}", remove_element(&mut vec![1, 2], 2)); //1
    println!("{:?}", remove_element(&mut vec![2, 2, 3], 2)); //1
    println!("{:?}", remove_element(&mut vec![1], 2)); //1
    println!("----");
    println!("{:?}", remove_element(&mut vec![], 2)); //0
    println!("{:?}", remove_element(&mut vec![2, 2], 2)); //0
    println!("{:?}", remove_element(&mut vec![2], 2)); //0
    println!("{:?}", remove_element(&mut vec![2, 2, 2, 2,], 2)); //0
    println!("----");
    println!("{:?}", remove_element(&mut vec![1, 1], 2)); //2
    println!("{:?}", remove_element(&mut vec![1, 2, 2, 1,], 2)); //2
    println!("{:?}", remove_element(&mut vec![2, 2, 1, 1,], 2)); //2
    println!("{:?}", remove_element(&mut vec![2, 1, 2, 1,], 2)); //2
    println!("----");
    println!("{:?}", remove_element(&mut vec![2, 1, 1, 1,], 2)); //3
    println!("{:?}", remove_element(&mut vec![1, 1, 1, 1,], 2)); //4
    println!("{:?}", remove_element(&mut vec![0, 1, 2, 2, 3, 0, 4, 2], 2)); //5
    println!(
        "{:?}",
        remove_element(&mut vec![0, 1, 2, 2, 3, 4, 0, 4, 2], 2)
    ); //6
}

//P#26
pub fn remove_duplicates(nums: &mut Vec<i32>) -> i32 {
    let mut idx: usize = 1;
    let mut last_one: i32 = nums[0];
    for i in 1..nums.len() {
        if nums[i] != last_one {
            nums[idx] = nums[i];
            last_one = nums[i];
            idx += 1
        }
    }
    return idx as i32;
}

//P#27
//使用两个指针划分“三个区间”
#[allow(dead_code)]
pub fn remove_element(nums: &mut Vec<i32>, val: i32) -> i32 {
    let mut i = 0;
    let mut cur = 0;
    let mut k = 0;
    while i < nums.len() {
        if nums[i] != val {
            nums[cur] = nums[i];
            cur += 1;
            k += 1;
        }
        i += 1;
    }
    return k as i32;
}

#[allow(dead_code)]
pub fn remove_element1(nums: &mut Vec<i32>, val: i32) -> i32 {
    let len = nums.len();
    if len == 0 {
        return 0;
    }
    if len == 1 {
        if nums[0] == val {
            return 0;
        } else {
            return 1;
        }
    }
    let mut left = 0;
    let mut right = len - 1;

    loop {
        if right < left {
            return left as i32;
        }
        while nums[right] == val {
            if right == left {
                return left as i32;
            }
            right -= 1;
        }
        while nums[left] != val {
            if left == right {
                left += 1;
                return left as i32;
            }
            left += 1;
        }
        // 0 <= left <= right
        // [left] == val, [right] != val
        nums[left] = nums[right];
        left += 1;
        if right > 0 {
            right -= 1;
        }
    }
}

#[allow(dead_code)]
pub fn remove_element2(nums: &mut Vec<i32>, val: i32) -> i32 {
    if nums.len() == 0 {
        return 0;
    }
    if nums.len() == 1 {
        if nums[0] == val {
            return 0;
        } else {
            return 1;
        }
    }
    let mut right = nums.len() - 1;
    let mut left: usize = 0;
    loop {
        if right < left {
            return left as i32;
        }
        if nums[left] == val {
            while nums[right] == val {
                if right <= left {
                    return left as i32;
                }
                right -= 1;
            }
            nums[left] = nums[right];
            // bcs 0 <= left < right, so
            left += 1;
            right -= 1;
        } else {
            left += 1;
        }
    }
}

//P#66
//constraints: 1 <= digits.length <= 100;  0 <= digits[i] <= 9;  digits does not contain any leading 0's.
pub fn plus_one(mut digits: Vec<i32>) -> Vec<i32> {
    let mut car = 1;
    let len = digits.len();
    let mut i = len - 1;
    loop {
        let nd = digits[i] + car;
        digits[i] = nd % 10;
        car = nd / 10;
        if car == 0 {
            return digits;
        }
        if i == 0 {
            break;
        }
        i -= 1;
    }
    if car == 1 {
        digits.insert(0, 1);
    }
    return digits;
}

//P#88
pub fn merge(nums1: &mut Vec<i32>, m: i32, nums2: &mut Vec<i32>, n: i32) {
    let m = m as usize;
    let n = n as usize;
    let nums1_cp = nums1.clone();
    let mut i: usize = 0; //0..m
    let mut j: usize = 0; //0..n
    let mut idx = 0;

    while i < m || j < n {
        if i >= m {
            while j < n {
                nums1[idx] = nums2[j];
                j += 1;
                idx += 1;
            }
            return;
        }
        if j >= n {
            while i < m {
                nums1[idx] = nums1_cp[i];
                i += 1;
                idx += 1;
            }
            return;
        }
        if nums1_cp[i] < nums2[j] {
            nums1[idx] = nums1_cp[i];
            i += 1;
        } else {
            nums1[idx] = nums2[j];
            j += 1;
        }
        idx += 1;
    }
}

pub fn merge2(nums1: &mut Vec<i32>, m: i32, nums2: &mut Vec<i32>, n: i32) {
    let m = m as usize;
    let n = n as usize;
    for i in (0..m).rev() {
        nums1[n + i] = nums1[i];
    }
    // nums1: [n blank elements, m elements]

    let mut i: usize = 0; //0..m
    let mut j: usize = 0; //0..n
    let mut idx = 0;
    while i < m && j < n {
        if nums1[i + n] < nums2[j] {
            nums1[idx] = nums1[i + n];
            i += 1;
        } else {
            nums1[idx] = nums2[j];
            j += 1;
        }
        idx += 1;
    }
    if i >= m {
        while j < n {
            nums1[idx] = nums2[j];
            j += 1;
            idx += 1;
        }
        return;
    }
    if j >= n {
        while i < m {
            nums1[idx] = nums1[i + n];
            i += 1;
            idx += 1;
        }
        return;
    }
}
