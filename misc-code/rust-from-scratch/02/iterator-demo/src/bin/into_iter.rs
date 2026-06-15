fn main() {}

#[test]
fn into_iter_1() {
    let v = vec![
        String::from("apple"),
        String::from("peach"),
        String::from("greap"),
    ];

    let v2: Vec<String> = v
        .into_iter()
        .map(|mut s| {
            s.push_str("..");
            s
        })
        .collect();

    let v2_iter = v2.iter();
    for i in v2_iter {
        assert!(i.ends_with(".."));
    }
}

#[test]
fn into_iter_2() {
    let v = vec![
        String::from("apple"),
        String::from("peach"),
        String::from("greap"),
    ];
    let mut v2 = v.into_iter();

    assert!(v2.next() == Some(String::from("peach")));
    assert!(v2.next() == Some(String::from("greap")));
    assert!(v2.next() == None);
}
