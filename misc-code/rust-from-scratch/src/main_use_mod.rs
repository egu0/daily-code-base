mod one;
mod utils;

// src 目录下的 rs 文件可看成一个模块
//  1. 使用 [mod 文件名] 引入模块
//  2. 使用 [文件名::xxx] 调用模块中的模块、函数等
#[allow(dead_code)]
fn use_mod_one() {
    one::say_hi();
    one::m::hi();
}

// 引入以目录作为模块，比如这里引入 utils 目录
//  1. 目录中可以定义多个 rs 文件，每个文件表示一个模块
//  2. 在目录中创建 mod.rs 文件，在其中定义要暴露的内容
#[allow(dead_code)]
fn use_mod_utils() {
    let s = utils::now();
    println!("{}", s);

    let c = utils::math_util::add(1, 2);
    println!("{}", c);
}

fn main() {
    use_mod_one();
    use_mod_utils();
}
