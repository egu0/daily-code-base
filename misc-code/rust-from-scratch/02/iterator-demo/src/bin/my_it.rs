fn main() {}

pub struct Counter {
    pub value: u32,
}

impl Counter {
    pub fn new() -> Self {
        Counter { value: 0 }
    }
}

impl Iterator for Counter {
    type Item = u32;

    fn next(&mut self) -> Option<Self::Item> {
        if self.value < 5 {
            self.value += 1;
            Some(self.value)
        } else {
            None
        }
    }
}

#[test]
fn test_counter() {
    let mut counter = Counter::new();
    assert_eq!(counter.next(), Some(1));
    assert_eq!(counter.next(), Some(2));
    assert_eq!(counter.next(), Some(3));
    assert_eq!(counter.next(), Some(4));
    assert_eq!(counter.next(), Some(5));
    assert_eq!(counter.next(), None);
}

#[test]
fn test_fun() {
    let v: Vec<_> = Counter::new()
        .zip(Counter::new().skip(1))
        .map(|(m, n)| m * n)
        .filter(|n| n % 3 == 0)
        .collect();
    assert!(v == vec![6, 12]);
    // 示例：两个向量相乘和过滤
    //  1,2,3,4,5
    //  2,3,4,5,N
    //              *
    //  2,6,12,20,N
}
