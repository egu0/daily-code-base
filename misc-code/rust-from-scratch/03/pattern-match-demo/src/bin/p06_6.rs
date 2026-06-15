pub enum Message {
    Quit,
    Move { x: i32, y: i32 },
    Write(String),
    ChangeColor(i32, i32, i32),
}
fn main() {
    let msg = Message::ChangeColor(0, 160, 255);
    match msg {
        Message::Quit => println!("quit program"),
        Message::Move { x, y } => println!("move to ({},{})", x, y),
        Message::Write(content) => println!("messsage: {}", content),
        Message::ChangeColor(r, g, b) => println!("change color to ({},{},{})", r, g, b),
    }
}
