fn main() {
    let x: u8 = 33;

    match x {
        0..=10 => println!("failed"),
        _ => println!("passed"),
    }

    let ch = 'c';
    match ch {
        'a'..='j' => println!("early letter"),
        'k'..='z' => println!("late letter"),
        _ => println!("other char"),
    }
}
