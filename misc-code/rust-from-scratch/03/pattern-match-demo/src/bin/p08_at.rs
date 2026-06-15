pub enum Message {
    Hello { id: i32 },
    General { content: String },
    Bye,
}
fn main() {
    let msg = Message::Hello { id: 105 };

    match msg {
        //接收内容
        Message::General {
            content: raw_content,
        } => println!("raw message is {:?}", raw_content),
        //指定整数范围
        Message::Hello { id: 0..=100 } => println!("hi, from user whoes id ranges [0, 100]"),
        //指定整数范围 + 使用 @ 接收内容
        Message::Hello {
            id: id_var @ 101..=1000,
        } => println!("hi, from user whoes id is {}", id_var),
        _ => println!("other message"),
    }
}
