// fn foo(_: i32, y: i32) {
//     println!("y is {}", y);
// }

// fn main() {
//     let mut value = Some(5);
//     let new_value = Some(5);
//
//     match (value, new_value) {
//         (Some(_), Some(_)) => {
//             println!("can overwrite an existing value")
//         }
//         _ => value = new_value,
//     }
//
//     println!("setting is {:?}", value)
// }

fn main() {
    // let x = 12; //编译警告
    let _y = 38;

    let s = Some(String::from("hi"));
    // if let Some(_s) = s { //会移动所有权
    if let Some(_) = s {
        //不会移动所有权
        println!("hit if let stmt");
    }
    println!("s:{:?}", s); //可以正常使用
}
