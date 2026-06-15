use rand::Rng; // trait, 其中包含 gen_range 函数
use std::cmp::Ordering;
use std::io;

fn main() {
    //使用 rand 库生成随机数
    let secret_number = rand::thread_rng().gen_range(1, 21);

    println!("欢迎来到猜数游戏");
    loop {
        println!("Guess a number(1-20):");
        let mut guess = String::new();
        // read_line(buf: &mut String)
        let _ = io::stdin().read_line(&mut guess).expect("读取出错");
        // 类型转换：trim() 可用于去除 guess 首尾的空白、\n 等符号
        let guess: i32 = match guess.trim().parse() {
            // Ok 表示正确解析，返回 data
            Ok(data) => data,
            // Err 表示出错，继续
            Err(err) => {
                println!("类型转换出错:{}", err);
                continue;
            }
        };
        // 数值匹配
        match guess.cmp(&secret_number) {
            Ordering::Less => println!("Too small."),
            Ordering::Greater => println!("Too large."),
            Ordering::Equal => {
                println!("You win!");
                break;
            }
        }
    }
}
