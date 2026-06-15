use adder;

mod common;

#[test]
fn it_add_two() {
    common::setup();
    assert!(3 == adder::add_two(1));
}
