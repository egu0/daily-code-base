fn main() {}

#[derive(PartialEq)]
pub struct Shoe {
    pub size: u32,
    pub style: String,
}

pub fn shoes_in_my_size(shoes: Vec<Shoe>, shoe_size: u32) -> Vec<Shoe> {
    shoes
        .into_iter()
        .filter(|item| item.size == shoe_size)
        .collect()
}

#[test]
fn filter() {
    let v = vec![
        Shoe {
            size: 32,
            style: "Deli".to_string(),
        },
        Shoe {
            size: 43,
            style: "Peak".to_string(),
        },
        Shoe {
            size: 43,
            style: "Nike".to_string(),
        },
    ];
    let v2 = shoes_in_my_size(v, 43);
    assert!(
        v2 == vec![
            Shoe {
                size: 43,
                style: "Peak".to_string(),
            },
            Shoe {
                size: 43,
                style: "Nike".to_string(),
            },
        ]
    );
}

#[test]
fn map() {
    let v = vec![1, 2, 3];
    // Vec<_>：不指定类型，交给编译器自行推断
    let v2: Vec<_> = v.iter().map(|x| x * 2).collect();
    assert!(v2 == vec![2, 4, 6]);
}

#[test]
fn sum() {
    let v = vec![1, 2, 3];
    let v_iter = v.iter();
    let sum: i32 = v_iter.sum();

    assert!(sum == 6);

    // println!("next: {:?}", v_iter.next()); //错误，因为所有权移动了
}

#[test]
fn for_in_iterator() {
    let v = vec![1, 2, 3];
    let v_iter = v.iter();

    for item in v_iter {
        println!("Got {}", item);
    }
}

#[test]
fn next() {
    let x = vec![1, 2, 4];
    let mut iterator = x.iter();

    assert_eq!(iterator.next(), Some(&1));
    assert_eq!(iterator.next(), Some(&2));
    assert_eq!(iterator.next(), Some(&4));
    assert_eq!(iterator.next(), None);
}
