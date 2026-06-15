use adder;

mod common;

#[test]
fn it_test() {
    common::setup();
    assert!(3 == adder::add_two(1));
}
