pub enum Color {
    Rgb(i32, i32, i32),
    Hsv(i32, i32, i32),
}
pub enum Message {
    Quit,
    Move { x: i32, y: i32 },
    Write(String),
    ChangeColor(Color),
}
fn main() {
    let msg = Message::ChangeColor(Color::Hsv(0, 160, 255));

    match msg {
        Message::Quit => println!("quit program"),
        Message::Move { x, y } => println!("move to ({},{})", x, y),
        Message::Write(content) => println!("messsage: {}", content),
        Message::ChangeColor(Color::Rgb(r, g, b)) => {
            println!("change color to rgb({},{},{})", r, g, b)
        }
        Message::ChangeColor(Color::Hsv(h, s, v)) => {
            println!("change color to hsv({},{},{})", h, s, v)
        }
    }
}
