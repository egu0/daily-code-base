# 第一部分

[Rust编程语言入门教程（Rust语言/Rust权威指南配套）【已完结】](https://www.bilibili.com/video/BV1hp4y1k7SV)

## 猜数字

```rust
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
```

## 类型

- **标量类型**：整数、浮点、布尔、字符
- **复合类型**：可以将多个值放在一个类型里，Rust 提供了两个基础复合类型即元组、数组

```rust
fn main() {
    //标量类型
    let _i: u32 = 76u32;
    let _j: i32 = 100_000_000;
    let _k: i32 = 0o12;
    let _b: u8 = b'f';
    let _c: char = '😄';

    //复合类型
    let tup: (&str, i32, i32) = ("Apple", 20023, 1988);
    let arr: [i32; 4] = [1, 2, 3, 4];

    //元组和数组变量的所有权没有转移，说明数组数据在 stack 上
    fn1(arr);
    fn2(tup);
    _ = tup.0;
    _ = arr[0];
}

fn fn1(_a: [i32; 4]) {}
fn fn2(_t: (&str, i32, i32)) {}
```

## 字符串

**字符串切片**，也叫做字符串字面值

在编译时就知道了它的内容了，文本内容直接被硬编码到最终的可执行文件里；优点是速度快高效，缺点是不可变

**String 类型**

操作系统在运行时在 heap 上分配内存保存文本内容；使用完后会调用 drop 函数释放内存

示例

```rust
fn main() {
    //字符串切片
    let t: &str = "jelly";
    fn1(t);
    println!("t:{}", t);

    //String 类型
    let s: String = String::from("jewelry");
    fn1(s.as_str());
    fn1(&s[..]);
}

fn fn1(s: &str) {
    _ = s.len();
}
```

## 所有权

### stack vs heap

1. 二者都是可用的内存，但他俩的结构不同
2. 访问数据方面，stack 快于 heap

**stack 特点**

1. 按值的接收顺序来存储，先进后出
2. 所有存储在 stack 上的数据必须拥有已知的固定大小
3. 指针的大小是固定的，所以把指针存放在 stack 上
4. 把数据存在 stack 上要比存在 heap 上快得多，因为对于 stack 来说永远放在栈顶

### 所有权解决的问题

1. 跟踪代码的哪些部分正在使用 heap 的哪些数据
2. 最小化 heap 上的重复数据
3. 清理 heap 上未使用的数据以避免空间不足

### 所有权规则

1. 每个值都有一个变量，这个变量就是该值的所有者
2. 每个值同时只能有一个所有者
3. 当所有者超出作用域（scope）时，该值将被删除

所有权示意图：（堆中数据在同一时刻只被一个变量拥有）

```text
         STACK                                   HEAP
--------------------------------------------------------------------
                                       offset    char     addr
                                     |--------|--------|--------|
|----------|----------|              |   0    |    j   | 0x1988 |
|   name   |     s    |              |   1    |    e   |        |
|   ptr    |  0x1988  |  ------->    |   2    |    l   |        |
|   len    |     5    |              |   3    |    l   |        |
| capacity |     5    |              |   4    |    y   |        |
|----------|----------|              |--------|--------|--------|
```

### clone vs copy

同一时刻 **heap 中的数据**只能对应 stack 中一个变量，存在所有权问题，解决方法可以是 **clone**，即将 heap 中的数据再复制一份

**stack 上数据**复制时使用 **copy**：

1. 如果一个类型实现了 Copy，那么旧的变量在赋值后任然可用
2. 如果一个类型或该类型的一部分实现了 Drop，那么 Rust 不允许他再实现 Copy
3. 任何需要分配内存或某种资源的都不是 Copy 的
4. 一些拥有 Copy trait 的类型：简单的标量类型，比如整形、布尔、字符、浮点、Tuple(要求所有字段都是 Copy 类型)

## 引用

引用是一个指针，指向拥有数据所有权的变量，**引用没有数据的所有权**

```text
         STACK                                       HEAP
--------------------------------------------------------------------
                                           offset    char     addr
                                         |--------|--------|--------|
      0x2024                             |   0    |    j   | 0x1988 |
    |----------|----------|              |   1    |    e   |        |
    |   name   |     s    |              |   2    |    l   |        |
    |   ptr    |  0x1988  |   ------->   |   3    |    l   |        |
    |   len    |     5    |              |   4    |    y   |        |
    | capacity |     5    |              |--------|--------|--------|
    |----------|----------|

                    / \
                     |------
                           |
|----------|----------|    |
|   name   |  ref_s   |    |
|   ptr    |  0x2024  |-----   注：0x2024 是变量 s 的栈内存地址
|----------|----------|
```

**借用**：把引用作为函数参数传递给函数，不会转移所有权。默认为不可变借用

```rust
fn test2() {
    let mut s = String::from("value");
    fn2(&s); //不可变借用
    fn3(&mut s); //可变借用
}

fn fn2(_s: &String) {}
fn fn3(_s: &mut String) {}
```

**悬空引用**

```rust
fn dangle(s: &String) -> &String {
    let s = String::from("value");
    // 悬空引用：一个指针指向内存中某个地址，而这个数据在离开作用域时被销毁了
    // cannot return reference to local variable `s`
    &s
}
```

## 结构体

### 基础

一旦 struct 的实例是可变的，那么实例中所有的字段都是可变的。rust 不允许 struct 实例中一部分字段是可变的。

当字段名和字段值对应变量名相同时，可以省略 `字段名:`，比如

```rust
User {
    email,
    username,
    male: true
}
```

struct 更新语法（类似于 js 解构）

```rust
let mut u1 = User {
    email: String::from("value@a.com"),
    username: "value".to_string(),
    age: 18,
    male: true,
};

u1 = User {
    email: "vv@a.com".to_string(),
    username: "vv".to_string(),
    ..u1
};
```

### Tuple struct

将 tuple 结构体化：给一个 tuple 结构起名字，同时定义其中的元素结构

```rust
struct Color(i32, i32, i32);
let black = Color(0, 0, 0);
```

**Unit-like struct**

没有任何字段的 struct，类似于单元类型`()`

### 打印结构体

```rust
fn main() {
    let mut u1 = User {
        email: String::from("value@a.com"),
        username: "value".to_string(),
        age: 18,
        male: true,
    };
    println!("{:?}", u1);
    println!("{:#?}", u1);
}

//派生 Debug 这个 trait 后，可以通过 :? 或 :#? 格式打印这个结构体的实例
#[derive(Debug)]
struct User {
    email: String,
    username: String,
    age: u32,
    male: bool,
}
```

### 方法和关联函数

方法：定义在 impl 块中；第一个参数可以是 `&self/self/&mut slef/mut self`；有助于更好的维护代码

关联函数：在 impl 块中定义的不把 self 作为第一个参数的函数，比如 String 的 new/from 等方法。可通俗理解为“静态函数”

```rust
fn main() {
    //通过 User:: 调用关联函数
    let mut u = User::new();
    println!("{:?}", u);
    //通过 . 调用实例的方法
    u.set_age(12);
    println!("{:?}", u);
}

//派生 Debug 这个 trait 后，可以通过 :? 或 :#? 打印结构体实例
#[derive(Debug)]
#[allow(dead_code)]
struct User {
    email: String,
    username: String,
    age: u32,
    male: bool,
}

impl User {
    // 方法
    fn set_age(&mut self, new_age: u32) {
        self.age = new_age;
    }

    // 关联函数，通过 User:: 调用
    fn new() -> Self {
        User {
            email: String::new(),
            username: String::new(),
            age: 0,
            male: true,
        }
    }
}
```

一个结构体可以拥有多个 impl 块

```rust
struct X {}

impl X {
    //block1
}
impl X {
    //block2
}
```

## 枚举

### 简单介绍

简单示例

```rust
fn main() {
    let mut ipk = IpAddrKind::Ipv4;
    println!("{:?}", ipk);
    ipk = IpAddrKind::Ipv6;
    println!("{:?}", ipk);
}

#[derive(Debug)]
enum IpAddrKind {
    Ipv4,
    Ipv6,
}
```

枚举可以作为函数参数、函数返回值、结构体字段、、

Rust 支持**数据附加到枚举的变体中**，变体中数据的类型无需保持一致

```rust
fn main() {
    let ip1 = IpAddr::v4(127, 0, 0, 1);
    let ip2 = IpAddr::v6(String::from("::1"));
}
enum IpAddr {
    v4(u8, u8, u8, u8),
    v6(String),
}
```

标准库中的 IP 枚举（`std::net::IpAddr`）

```rust
pub struct Ipv4Addr {
    octets: [u8; 4],
}

pub struct Ipv6Addr {
    octets: [u8; 16],
}

pub enum IpAddr {
    V4(Ipv4Addr),
    V6(Ipv6Addr),
}
```

同结构体，也可以通过 impl 块为枚举添加方法

### Option

Rust 中没有类似 null 的概念，但它提供了 Option 枚举，其中包含的 None 变体表示为空；

Option 在标准库中的定义：

```rust
enum Option<T> {
    Some(T),
    None,
}
```

`Option<T>, Some(T), None` 三者被包含在 **Prelude**（预导入模块）中，因此可以直接使用。

示例

```rust
fn test1() {
    //自动类型推断
    let o1 = Some("hello");
    let o2 = Some([1, 2, 3]);

    //指定类型
    let o3: Option<i32> = None;
}
```

### match

强大的控制流运算符；

允许一个值与一系列模式进行匹配，并执行匹配的模式对应的代码；

模式可以是字面量、变量名、通配符等；

示例

```rust
fn main() {
    println!("{}", value_in_coin(Coin::Nickel));
}

#[allow(dead_code)]
enum Coin {
    Penny,
    Nickel,
    Dime,
    Quarter,
}
fn value_in_coin(coin: Coin) -> u8 {
    match coin {
        Coin::Penny => 1,
        Coin::Nickel => 5,
        Coin::Dime => 10,
        Coin::Quarter => 25,
    }
}
```

上边的例子是枚举类型匹配，如果枚举的类型绑定了数据，也可以**在 match 中获取到枚举绑定的值**

```rust
fn main() {
    println!("{}", value_in_coin(Coin::Nickel));
    println!("{}", value_in_coin(Coin::Quarter(String::from("alaska"))));
}

#[allow(dead_code)]
enum Coin {
    Penny,
    Nickel,
    Dime,
    Quarter(String),
}

fn value_in_coin(coin: Coin) -> u8 {
    match coin {
        Coin::Penny => 1,
        Coin::Nickel => 5,
        Coin::Dime => 10,
        Coin::Quarter(city) => {
            println!("city is {}", city);
            25
        }
    }
}
```

匹配 `Option<T>`

```rust
fn main() {
    println!("{:?}", plus_one(Some(1)));
    println!("{:?}", plus_one(None));
}

fn plus_one(num: Option<i32>) -> Option<i32> {
    match num {
        Some(n) => Some(n + 1),
        None => None,
    }
}
```

默认情况下需要在 match 块中穷举所有可能；如果情况太多，可以使用 `_` 通配符表示其他情况。

```rust
let age: u8 = 1;
let identify = match age {
    0 => "baby",
    1 => "little child",
    2 => "little child",
    3 => "child",
    _ => "other",
};
println!("{}", identify);

match age {
    0 => println!("0"),
    1 => println!("1"),
    _ => (),
}
```

### if let

特殊的模式匹配：只对某个匹配感兴趣，比如如下代码

```rust
let v = Some(0u8);
match v {
    Some(3) => println!("three"),
    _ => (),
}
```

此时可以用 `if let` 简化

```rust
if let Some(3) = v {
    println!("three");
}
```

if let 用来处理某一种匹配而忽略其它匹配的情况；相比于 match 块，if let 使用更少的代码、更少的缩进和更少的模板代码

可以把 if let 看成 match 的语法糖

if let 也可以搭配 else 使用

```rust
let v = Some(4u8);

if let Some(3) = v {
    println!("three");
} else if let Some(4) = v {
    println!("four");
} else {
    println!("other");
}
```

## 模块系统

模块系统是 Rust 组织代码的方式，它可以：

- 定义哪些细节是公开的或私有的
- 作用域内有哪些名称有效
- 等

模块系统核心概念：

- 包（**package**）：通过 `cargo new` 创建；用来构建、测试、共享 crate；
- **crate**：可以产出库或可执行文件；
- 模块（**module**）：用来组织代码、作用域和私有路径；
- 路径（**path**）：为 struct、function 或 module 等条目命名的方式；

### package 和 crate

crate：

- 有 binary 和 library 两种类型；

package：

- 包含一个 Cargo.toml 文件，用来描述如何构建其中的 crate(s)
- 可以包含任意数量的 binary crate，但最多只能包含一个 library crate

- 至少包含一个 crate（binary 或 library）；通过 `cargo new xx` 创建时默认包含一个 binary crate；通过 `cargo new xxx --lib` 创建 library crate

`src/main.rs` 和 `src/lib.rs` 两个文件：

- package 中的`src/main.rs` 文件是 binary crate 的 crate root，且**crate 名**和 **package 名**相同；

- package 中的`src/lib.rs` 文件是 library crate 的 crate root，且**crate 名**和 **package 名**相同

一个 package 可以同时包含 `src/main.rs` 和 `src/lib.rs` 这两个文件，表示包含一个 binary crate 和一个 library crate，并且这两个 crate 名与 package 名相同。

一个 package 可以包含多个独立的 binary crate，可以把 rs 文件放在 `src/bin/` 下；也可以在 Cargo.toml 中指定 `[[bin]]` 项手动配置。

可以在 `src/main.rs` 中通过 `use crate名;` 导入 `src/lib.rs` ；反之则不行。

### module

module 用于将 crate 中的代码进行分组以提高可读性和复用性。另外也可以控制项目（item）的可见性。

module 由 mod 关键字创建，可以嵌套多层 mod，也可以包含其他项（struct、enum、常量、trait、函数等项目）的定义

**示例**

`src/lib.rs`

```rust
mod moda {
    mod biden {
        fn hi() {}
        fn bye() {}
    }

    mod washington {
        fn hit() {}
        fn speak() {}
    }
}
```

该 package 目录结构

```text
.
├── Cargo.lock
├── Cargo.toml
└── src
    ├── lib.rs
    └── main.rs
```

分析：

`src/lib.rs` 是一个 crate root，它的内容形成了**名为 crate 的模块**；即模块 moda 的上层是一个名为 crate 的隐式模块，这个隐式模块包含了 crate root 中定义的所有根级条目。在本例中，根级条目只有 moda 模块。

```
crate
└── moda                    moda 模块
    └── biden               biden 模块
    |   ├── hi
    |   └── bye
    └── washington          washington 模块
        ├── hit
        └── speak
```

### path

路径用于在 rust 的模块中找到某个条目，有两种形式：

- 绝对路径：路径从 crate root 开始，使用 crate 名或字面值 crate
- 相对路径：相对于当前模块，使用 self、super 或当前模块的标识符

注意：路径至少由一个标识符组成，如果超过一个，那么需要用 `::` 将他们连接起来

示例（`src/lib.rs`）

```rust
// 结构：隐式 crate >> mod_a >> mode_ax >> fnx
mod mod_a {
    pub mod mod_ax {
        pub fn fnx() {}
    }
}

pub fn fn1() {
    //绝对路径
    crate::mod_a::mod_ax::fnx();

    //相对路径
    mod_a::mod_ax::fnx();
}
```

Q：代码中的 mod_a 模块为啥不是 pub 的却没有报错？

A：源代码文件中根级条目之间可以互相调用且无需考虑可见性

Q：fn1 函数怎么在 `src/main.rs` 中调用？

A：通过 `crate名::fn1()` 调用，本例中 crate 名与 package 名相同

**私有边界**：

- 如果想把函数或 struct 等设为私有，那么可以把它放到某个模块中
- rust 中所有条目默认都是私有的（函数、方法、struct、enum、模块、变量等）
- 父级模块无法访问子级模块的私有条目
- 子级模块可以使用所有祖先模块的条目

**super 关键字**：用于访问父级模块中的内容，类似于文件系统中的 `..`

示例：访问父级模块中的条目

```rust
fn a() {}
mod b {
    fn c() {
        //访问同级模块中的条目
        d();

        //访问父级模块中的条目
        //1.绝对路径
        crate::a();
        //2.相对路径（通过 super 关键字）
        super::a();
    }

    fn d() {}
}
```

**pub struct**

struct 默认是私有的，要将他暴露到外部，需要使用 pub 修饰，即 `pub struct ...`

注意：struct 中的字段需要单独设置为 pub 来将其变成共有的。

示例

```rust
mod back_of_house {
    pub struct Breakfast {
        pub toast: String,
        seasonal_fruit: String,
    }

    impl Breakfast {
        pub fn summer(toast: &str) -> Breakfast {
            Breakfast {
                toast: String::from(toast),
                seasonal_fruit: String::from("peaches"),
            }
        }
    }
}

pub fn eat_at_restaurant() {
    let mut meal = back_of_house::Breakfast::summer("Rye");

    meal.toast = String::from("Wheat");
    println!("I'd like {} toast please.", meal.toast);

    //Error
    // meal.seaonsal_fruit = String::from("blueberries");
}
```

**pub enum**

同上边的 pub struct；

注意：pub enum 中的变体默认也是 pub 的

### use

use 关键字可以将路径所指向的条目（模块、函数、枚举等）引入到作用域内。

使用 use 引入条目时需遵守私有性原则，即只能引入公共的条目

示例：

```rust
mod front_of_house {
    pub mod hosting {
        pub fn add_to_waitlist() {}
    }
}

//---------------------
//将其他模块中的条目引入到当前作用域内
use front_of_house::hosting;

pub fn eat_at_restaurant() {
    hosting::add_to_waitlist();
    hosting::add_to_waitlist();
    hosting::add_to_waitlist();
}
```

use 的习惯用法：

- 对于函数，通常将**函数的父级模块**引入到作用域（指定到父级）
- 对于 struct、enum 等项目，通常进行**完整引用**（指定到本身）
- 特殊情况：出现同名条目时通常将条目的父级模块引入作用域
- 用法扩展：使用 **as 关键字**为条目**起别名**

**使用 pub use 重新导出条目**

使用 use 关键字导入条目到当前作用域后，该条目在此作用域内默认是**私有**的。

如何将其设置为共有呢？答：使用 `pub use xxx` 进行**重新导出**：

- 将条目引入当前作用域
- 该条目可以被外部代码引入到它们的作用域

示例

```rust
// src/lib.rs

//定义嵌套模块
mod front_of_house {
    pub mod hosting {
        pub fn add_to_waitlist() {}
    }
}

//引入 hosting 模块，并将它视为 public 的
pub use front_of_house::hosting;//相对路径
//pub use crate::front_of_house::hosting;//绝对路径

pub fn eat_at_restaurant() {
    hosting::add_to_waitlist();
    hosting::add_to_waitlist();
    hosting::add_to_waitlist();
}

//--------------------------------------
// src/main.rs

//引入 hosting 模块
use rust_pratices::hosting;

fn main() {
    // rust_pratices::fn1();
    hosting::add_to_waitlist();
}

```

**使用外部 package**

1. 在 Cargo.toml 中添加要依赖的包。参考 https://crates.io
2. 使用 use 关键字将目标条目引入作用域中
3. 使用条目

注意：标准库`std`也被当作外部包使用，但无需在 Cargo.toml 中声明它而是直接使用

**使用嵌套路径清理大量的 use 语句**

```rust
// use std::cmp::Ordering;
// use std::io;

use std::{cmp::Ordering, io};

//----------------------------------------

// use std::io;
// use std::io::Write;

use std::io::{self, Write};

//----------------------------------------

use std::collections::*;//正常使用时，不推荐一次性全部导入
```

### 模块拆分

使用 `mod xxx` 定义模块

- 如果后边紧接着一个代码块，即 `mod xxx { ... }`，那么表明这是一个模块定义
- 如果后边紧接着一个 `;`，即 `mod xxx;`，那么这条语句表示会从`src/xxx.rs`文件中加载内容

示例：将如下代码（来自 `src/lib.rs` ）拆分到不同文件中，即使用 `mod xxx;` 代替 `mod xxx { ... }`

```rust
mod front_of_house {
    pub mod hosting {
        pub fn add_to_waitlist() {}
    }
}

pub use crate::front_of_house::hosting;

pub fn eat_at_restaurant() {
    hosting::add_to_waitlist();
    hosting::add_to_waitlist();
    hosting::add_to_waitlist();
}
```

结果为：

```rust
/*
src/
  - lib.rs
  - main.rs
  - front_of_house.rs
  - front_of_house/
    - hosting.rs
*/

//lib.rs
//-----------------------------------------------
mod front_of_house;//引入 src/front_of_house.rs
pub use crate::front_of_house::hosting;//重新导出
pub fn eat_at_restaurant() {
    hosting::add_to_waitlist();
    hosting::add_to_waitlist();
    hosting::add_to_waitlist();
}


//front_of_house.rs
//-----------------------------------------------
pub mod hosting;


//hosting.rs
//-----------------------------------------------
pub fn add_to_waitlist() {}
```

## 集合

### Vec

`Vec<T>`，向量

- 由标准库提供，在 prelude 中，无需手动引入
- 可存储多个值，这些值的类型相同
- 值在内存中连续存放

创建和添加元素

```rust
let v0: Vec<bool> = Vec::new();

let mut v1 = Vec::new();//自动类型推断
v1.push(1);
v1.push(1);

let v1 = vec![1, 2, 3, 4, 5];//自动类型推断
```

读取元素

```rust
    let v1 = vec![1, 2, 3, 4, 5]; //自动类型推断

    let idx = 0;

    //通过 [i] 访问，越界时会 panic 终止运行
    let v = v1[idx];
    println!("v:{}", v);

    //通过 get(i) 访问，返回 Option，更安全
    match v1.get(idx) {
        Some(v) => println!("v:{}", v),
        None => println!("v does not exist!"),
    }
```

所有权与借用

```rust
fn main() {
    let mut v = vec![1, 2, 3, 4, 5];

    let v0 = &v[0]; //不可变引用
    v.push(6); //可变引用，报错：不能再同一作用域内同时拥有可变和不可变引用

    println!("0th:{:?}", v0);
}
```

why？答：通过可变引用添加数据时可能要 reallocate 新的内存空间，拷贝后就不能用其它引用访问元素了

遍历向量

```rust
fn main() {
    let v = vec![1, 2, 3, 4, 5];

    for i in &v {
        println!("{}", i);
    }
}
```

```rust
fn main() {
    let mut v = vec![1, 2, 3, 4, 5];
    println!("{:?}", v);

    for i in &mut v {
        *i += 1;
    }
    println!("{:?}", v);
}
```

配合 enum 在 vector 中存储多种数据类型

```rust
enum SpreadExcelCell {
    Int(isize),
    Float(f64),
    String(String),
}

fn main() {
    let row = vec![
        SpreadExcelCell::Int(17),
        SpreadExcelCell::Float(57.1),
        SpreadExcelCell::String(String::from("grade")),
    ];
}
```

### String

> 在 Rust 核心语言层面只有一个字符串类型：**字符串切片**
>
> 字符串切片是对存储在其它地方、UTF-8 编码的字符串的引用
>
> 字符串字面值：存储在二进制文件中，也是字符串切片

String 是基于 UTF-8 编码的 byte 数据的集合；String 提供了一些方法能够将 byte 数据解析为文本；

String 来自标准库但不属于 Rust 核心内容。String 示例可增长、可修改、可拥有。

初始化

```rust
let s1 = "x".to_string();

let s2 = String::new();

let s3 = String::from("hi");
```

追加

```rust
let s = "foo".to_string();
s.push_str("bar");

s.push('!');
```

拼接

```rust
let s1 = "foo".to_string();
let s2 = "foo".to_string();

// 获取 s1 的所有权，将 s2 中的内容复制到 s1 ，最后返回 s1
// s1 丢失所有权；s2 不会丢失所有权
let s3 = s1 + &s2;
// 多个拼接。会从前往后两两进行拼接。要求第一个为 String 类型，其后的都是 &str 类型
let s4 = s2 + "-"  + &s3;


//另一种方式
let x: String = format!(
    "{}-{}-{}",
    "1".to_string(),
    "2".to_string(),
    "3".to_string()
);
```

Rust 不允许对 String 进行索引

```rust
fn main() {
    //Vec<u8>
    let s = "中国人会武功".to_string();

    //字节数组
    for b in s.as_bytes() {
        print!("{} ", b);
    }
    println!();

    // char 表示 Unicode 标量值，占用 4B
    for c in s.chars() {
        print!("{} ", c);
    }
    println!();
}
```

字符串切片

```rust
fn main() {
    let s = String::from("我爱加班");

    let s1 = &s[0..3]; //0,1,2
    println!("{}", s1); //我

    let s2 = &s[3..6]; //3,4,5
    println!("{}", s2); //爱

    let s3 = &s[0..4]; //0,1,2,3，程序 panic
    println!("{}", s3);
}
```

使用 `&[x..y]` 进行切片时需要谨慎，如果切割时跨越了字符边界，那么程序会 panic

### HashMap

hashmap 使用键值对的形式存储数据，一个键对应一个值

创建和添加键值对

```rust
use std::collections::HashMap;

fn main() {
    let mut m: HashMap<String, i32> = HashMap::new();

    let mut m = HashMap::new();
    //自动类型推断
    m.insert(String::from("blue"), 10);

    let team_names = vec![String::from("blue"), String::from("black")];
    let team_scores = vec![20, 30];
    // 指定类型为 HashMap<_, _>，无需指定内部类型
    let mut scores: HashMap<_, _> = team_names.iter().zip(team_scores.iter()).collect();
    scores.insert(&String::from("orange"), &20);
}
```

所有权

- insert 拥有所有权的数据，数据的所有权会转移给 hashmap，之后数据不能再次被使用
- insert 拥有所有权的数据的引用，数据的所有权不会转移给 hashmap，但被引用的值必须在 hashmap 有效期间保持有效

示例

```rust
use std::collections::HashMap;

fn main() {
    let k = "daisy".to_string();
    let v = "good".to_string();
    let mut m = HashMap::new();

    //insert 拥有所有权的数据
    // m.insert(k, v);
    // println!("{},{}", k, v);// panic

    //insert 拥有所有权的数据的引用
    m.insert(&k, &v);
    println!("{},{}", k, v);
}
```

根据 key 获取 value

```rust
//get(K) -> Option<&V>
use std::collections::HashMap;

fn main() {
    let mut m = HashMap::new();
    m.insert("daisy", 10);

    let x = m.get("daisy");
    if let Some(s) = x {
        println!("{}", s);
    } else {
        println!("not exist");
    }
}
```

遍历

```rust
use std::collections::HashMap;

fn main() {
    let mut m = HashMap::new();
    m.insert("daisy", 10);
    m.insert("tailwind", 30);

    for (k, v) in &m {
        println!("{}-{}", k, v);
    }
}
```

更新

```rust
use std::collections::HashMap;

fn main() {
    let mut m = HashMap::new();
    m.insert("daisy", 10);
    m.insert("tailwind", 20);
    m.insert("tailwind", 30);//覆盖
    // m.insert("antd", 30);

    //如果 antd 键不存在，则会插入 (antd, 20)；如果存在，则不进行更新
    //or_insert() 返回值的可变引用
    let x = m.entry("antd").or_insert(20);
    *x += 5;

    println!("{:#?}", m);
}
```

练习：统计一段文字（英文段落，所有单词之间都以空格分隔）中所有单词出现的次数

## 错误

Rust 错误可分为两类：

- 可恢复的错误，比如文件未找到
- 不可恢复的错误，比如访问数组越界

### 不可恢复错误

当发生 panic 时，可以通过查看**回溯信息**定位引起问题的代码。

```rust
fn main() {
    let v = vec![1, 2, 3];

    v[1000];
}
```

通过 `cargo run --bin xxx` 运行，得到输出为

```
➜  rust-pratices git:(master) ✗ cargo run --bin main_09_vector
    Finished dev [unoptimized + debuginfo] target(s) in 0.02s
     Running `target/debug/main_09_vector`
thread 'main' panicked at src/bin/main_09_vector.rs:4:6:
index out of bounds: the len is 3 but the index is 1000
note: run with `RUST_BACKTRACE=1` environment variable to display a backtrace
```

进一步，可以设置环境变量 `RUST_BACKTRACE=1` 以得到回溯信息

注意：为了获取带有调试信息的回溯，必须使用调试模式（默认使用的是调试模式；添加 `--release` 参数时表示非调试模式）

### 可恢复错误

通常情况下，如果一个方法在调用时可能出现可恢复错误，那么它的返回值通常是 Result 枚举，Result 枚举有两个变体，Ok 表示成功，Err 表示错误，且两个变体都可以附加数据。

Result 枚举及其变体（Result、Ok、Err 三者都由 prelude 模块带入作用域）

```rust
enum Result<T, E> {
	Ok(T),
	Err(E),
}
//T: 操作成功时，Ok 变体里返回的数据的类型
//E: 操作失败时，Err 变体里返回的错误的类型
```

示例

```rust
use std::fs::File;

fn main() {
    let res = File::open("hello.txt");
    match res {
        Ok(file) => println!("{:?}", file.metadata().unwrap()),
        Err(err) => println!("{}", err),
    }
}
```

进一步匹配错误类型

```rust
use std::{fs::File, io::ErrorKind};

fn main() {
    let res = File::open("hello.txt");
    let f = match res {
        Ok(file) => file,
        Err(err) => match err.kind() {
            //匹配不同的错误类型
            ErrorKind::NotFound => match File::create("hello.txt") {
                Ok(file) => file,
                Err(err) => panic!("Error creating file:{:?}", err),
            },
            other_err => panic!("Error opening file:{:?}", other_err),
        },
    };
    println!("file:{:?}", f);

    //等价写法（基于闭包）
    let f = File::open("hello.txt").unwrap_or_else(|err| {
        if err.kind() == ErrorKind::NotFound {
            File::create("hello.txt").unwrap_or_else(|err| {
                panic!("Error creating file:{:?}", err);
            })
        } else {
            panic!("Error opening file:{:?}", err);
        }
    });
    println!("file:{:?}", f);
}
```

Result 枚举的 unwrap 和 expect 两个方法（和 Option 枚举类似）

- `unwrap()`：解包，如果 Result 是 Err 变体，那么程序会 panic；如果是 Ok 变体，那么返回 T
- `expect(msg)`：解包，如果 Result 是 Err 变体，那么程序会 panic，错误消息是传递的 msg；如果是 Ok 变体，那么返回 T

**错误传播**：定义函数返回值类型为 Result，将函数中的可恢复错误传播到函数调用者。

```rust
fn read_content_from_file(file_name: String) -> Result<String, io::Error> {
    //打开文件
    let mut f = match File::open(file_name) {
        Ok(f) => f,
        Err(e) => return Err(e),
    };

    //读取文件
    let mut content = String::new();
    match f.read_to_string(&mut content) {
        Ok(_) => Ok(content),
        Err(e) => Err(e),
    }
}
```

使用 **? 运算符**简化错误传播。`result?` 表达式表示如果 result 是 Err 变体那么将该 Err 变体作为函数返回值进行返回；如果是 Ok 变体那么表达式的结果是 Ok 变体中的数据。即下边的代码等价于上边的：

```rust
fn read_content_from_file(file_name: String) -> Result<String, io::Error> {
    //打开文件
    let mut f = File::open(file_name)?;

    //读取文件
    let mut content = String::new();
    f.read_to_string(&mut content)?

    //返回数据
    Ok(content)
}
```

Q：是否可以使用 `?` 运算符处理所有错误，即将所有可能出现的错误都转换为函数返回类型中所定义的错误类型？

A：默认不会转换所有错误，而需要为错误添加转换逻辑。假设函数内可能出现错误 E1，要转为函数返回类型中所定义的错误 E2，那么需要为 E1 实现 `std::convert::From` trait，当 `?` 运算符处理错误 E1时，会隐式调用 from 将 E1 转换为 E2

**使用链式调用进一步简化代码。**

```rust
fn read_content_from_file(file_name: String) -> Result<String, io::Error> {
    let mut content = String::new();
    File::open(file_name)?.read_to_string(&mut content)?;
    Ok(content)
}
```

注意：? 运算符只能用于返回 Result 类型的函数。比如以下代码在编译时会报错

```rust
use std::fs::File;

fn main() {//默认返回 () 类型
    let f = File::open(file_name)?;
}
```

改进

```rust
use std::{
    error::Error,
    fs::File,
};

//可将 Box<dyn Error> 理解为任何可能的错误类型
fn main() -> Result<(), Box<dyn Error>> {
    let f = File::open("hello.txt")?;
    Ok(())
}
```

### 何时用 panic

[p42](https://www.bilibili.com/video/BV1hp4y1k7SV/?p=42)

总体原则：

- 定义一个函数，如果它在执行时可能会出现错误，那么优先考虑返回 Result
- 否则出错就 panic!

场景建议：

- 如果别人调用你的代码时传入无意义的参数值，建议 panic
- 当你调用外部不可控代码获得了时非法状态，你无法修复，建议 panic
- 如果失败是可预期的，建议返回 Result
- 当你的代码中需要使用索引下标访问的数组、向量的某个元素时，首先应该验证这些值是否存在，建议 panic

示例：改造之前猜数游戏部分代码

```rust
use std::io;

fn main() {
    loop {
        //..

        let mut guess = String::new();
        let _ = io::stdin().read_line(&mut guess).expect("读取出错");
        let guess: i32 = match guess.trim().parse() {
            Ok(data) => data,
            Err(err) => {
                println!("类型转换出错:{}", err);
                continue;
            }
        };

        if guess < 1 || guess > 21 {
            println!("请输入 0-20 之间的数字！");
            continue;
        }

        //..
    }
}
```

改造后的

```rust
use std::io;
pub struct Guess {
    num: i32,
}
impl Guess {
    pub fn new(num: i32) -> Guess {
        //验证参数是否合法
        if num < 0 || num > 20 {
            panic!("请输入 0-20 之间的数字");
        }
        Guess { num }
    }
    pub fn value(&self) -> i32 {
        self.num
    }
}

fn main() {
    loop {
        //..

        let mut guess = String::new();
        let _ = io::stdin().read_line(&mut guess).expect("读取出错");
        let guess: i32 = match guess.trim().parse() {
            Ok(data) => data,
            Err(err) => {
                println!("类型转换出错:{}", err);
                continue;
            }
        };

        //将验证逻辑放在构造中
        let guess = Guess::new(guess);
        let guessed_num = guess.value();

        //..
    }
}
```

## 泛型

泛型时具体类型或其它属性的抽象代替，可以提高代码的复用能力以减少重复代码

编写的泛型代码不是最终的代码，而是一个模板，里边有一些“占位符”；编译器在编译时会将“占位符”替换为具体的类型。

**函数泛型**

常用函数泛型来指定参数类型和返回类型。需要注意的一点是泛型在使用前需要先声明

示例

```rust
fn largest(list: &[i32]) -> i32 {
    if list.len() == 0 {
        panic!("no element in list.");
    } else {
        let mut largest = list[0];
        for &item in list {
            if item > largest {
                largest = item;
            }
        }
        return largest;
    }
}

// 从前到后三个 T：声明泛型、在函数参数中使用泛型、在函数返回值中使用泛型
// 假设 T 类型数据存放在 stack 中
fn largest<T: PartialOrd + Copy>(list: &[T]) -> T {
    if list.len() == 0 {
        panic!("no element in list.");
    } else {
        let mut largest = list[0];
        for &item in list {
            if item > largest {
                largest = item;
            }
        }
        return largest;
    }
}

// 假设 T 类型数据存放在 heap 中
fn largest<T: PartialOrd + Clone>(list: &[T]) -> &T {
    if list.len() == 0 {
        panic!("no element in list.");
    } else {
        let mut largest = &list[0];
        for item in list {
            if item > largest {
                largest = item;
            }
        }
        return largest;
    }
}
```

**结构体泛型**

常用结构体泛型来指定其中字段的类型。

示例

```rust
struct Point<T> {
    x: T,
    y: T,
}
```

**枚举泛型**

```rust
Option<T> {
    Some(T),
    None,
}

Result<T, E> {
    Ok<T>,
    Err(E),
}
```

**方法泛型**

```rust
struct Point<T> {
    x: T,
    y: T,
}

//使用泛型时需要先声明
impl<T> Point<T> {
    fn x(&self) -> &T {
        &self.x
    }
}
```

注意：struct 中的泛型参数可以和方法的泛型类型参数不同。示例：

```rust
fn main() {
    let p1: Point<char, i32> = Point { x: 'a', y: 1 };
    let p2: Point<&str, bool> = Point { x: "x", y: true };
    let p3: Point<&str, i32> = p1.mixup(p2);
    println!("p3:{:?}", p3);
}

#[derive(Debug)]
struct Point<T, U> {
    x: T,
    y: U,
}

impl<T, U> Point<T, U> {
    fn mixup<V, W>(self, other: Point<V, W>) -> Point<V, U> {
        Point {
            x: other.x,
            y: self.y,
        }
    }
}
```

**泛型代码的性能**

Rust 中使用泛型的代码和使用具体类型的代码运行速度是一样的。

因为 Rust 在编译时会进行单态化（monomorphization），在此过程会将泛型替换为具体类型。

## Trait

翻译为特征、特质

trait 用于定义某一类的类型的共同行为，比如可以为整数类型定义比较大小的行为。trait 类似于其它语言的接口（interface）。

trait 约束：可为泛型类型参数指定它实现了哪些特定的 trait，比如 `fn max<T: Copy>(a:T, b:T) {...}`

### 定义 trait

使用 trait 关键字定义一个块结构，其中包括多个方法签名，也可以定义方法实现

```rust
trait Behavior {
    fn do_task1(&self) -> String;
    fn do_task2(&self) -> String;
}
```

### 为类型实现 trait

```rust
impl TraitA for StructB {
    //...
}
```

示例

```rust
pub struct NewsArticle {
    pub headline: String,
    pub location: String,
    pub author: String,
    pub content: String,
}

pub struct Tweet {
    pub username: String,
    pub content: String,
    pub reply: bool,
    pub retweet: bool,
}

trait Summary {
    fn summarize(&self) -> String;
}

//为两个类型实现 Summary trait

impl Summary for NewsArticle {
    fn summarize(&self) -> String {
        format!("{}, by {} ({})", self.headline, self.author, self.location)
    }
}
impl Summary for Tweet {
    fn summarize(&self) -> String {
        format!("{}: {}", self.username, self.content)
    }
}

fn main() {
    let n = NewsArticle {
        headline: "a big announcement".to_string(),
        location: "alaska".to_string(),
        author: "bob".to_string(),
        content: "blablabla...".to_string(),
    };
    let s = n.summarize();
    println!("summary:{}", s);
}
```

**为类型实现 trait 的约束条件**：类型或 trait 来自本地 crate，或者二者都来自本地 crate

相同的，无法为外部类型实现外部 trait。

### 默认实现

```rust
trait Summary {
    //添加方法体，其中定义默认实现逻辑
    fn summarize(&self) -> String {
        return "read more...".to_string();
    }
}

impl Summary for NewsArticle {
    //重写默认实现
    fn summarize(&self) -> String {
        format!("{}, by {} ({})", self.headline, self.author, self.location)
    }
}
impl Summary for Tweet {
    fn summarize(&self) -> String {
        format!("{}: {}", self.username, self.content)
    }
}
```

可以在默认实现中调用 trait 中其它的方法，即使这些方法没有默认实现。比如

```rust
trait Summary {
    fn summarize_author(&self) -> String;

    fn summarize(&self) -> String {
        return format!("read more from {}...", self.summarize_author());
    }
}
```

注意：无法从方法的重写实现里调用默认的实现。

### 作为方法参数

提供了两种写法

- 简单写法
- trait bound 写法

示例

```rust
trait Summary {
    fn summarize(&self) -> String;
}

//1. 简单写法
fn do_task1(sum: impl Summary) {}

//2. trait bound 写法
fn do_task2<T: Summary>(sum: T) {}
```

推荐第二种写法。why？第二种等直观简洁：

```rust
fn do_task1(sum1: impl Summary, sum2: impl Summary) {}
fn do_task2<T: Summary>(sum1: T, sum2: T) {}
```

**使用 + 操作符指定多个 trait bounds**

```rust
fn do_task1(sum: impl Summary + Display) {}

fn do_task2<T: Summary + Display>(sum: T) {}
```

**使用 where 语句指定 trait bounds**，将 trait bounds 移动到参数列表后，让函数更加直观

```rust
fn do_task2<T: Summary + Display, U: Clone + Debug>(sum: T, check: U) {}

fn do_task3<T, U>(sum: T, check: U)
where
    T: Summary + Display,
    U: Clone + Debug,
{
}
```

### 作为方法返回值

```rust
fn do_task4() -> impl Summary {
    Tweet {
        username: "elon".to_string(),
        content: "yeah".to_string(),
        reply: true,
        retweet: false,
    }
}
```

注意：如果 trait 作为方法返回值，那么**必须保证函数体“返回一种类型”**，返回的类型超过一种时会报错，比如

```rust
fn do_task4() -> impl Summary {
    let flag = true;
    if flag {
        Tweet {
            username: "elon".to_string(),
            content: "yeah".to_string(),
            reply: true,
            retweet: false,
        }
    } else {
        //这里报错；如果这里返回 Tweet 类型实例则不报错
        NewsArticle {
            headline: "a big announcement".to_string(),
            location: "alaska".to_string(),
            author: "bob".to_string(),
            content: "blablabla...".to_string(),
        }
    }
}
```

### 选择性的实现方法

为 impl 块的泛型参数指定 trait bounds，可以选择性地为**实现了特定 trait 的类型**实现方法。示例：

```rust
struct Category<T> {
    id_list: Vec<T>,
}

//impl 块的泛型参数没有指定 trait bounds
impl<T> Category<T> {
    fn new() -> Self {
        Category { id_list: vec![] }
    }
    fn add(&mut self, item: T) {
        self.id_list.push(item);
    }
}

//impl 块的泛型参数指定了 trait bounds，实现了这些 trait 的类型可以调用其中定义的方法，比如 i32
impl<T: PartialOrd + Copy> Category<T> {
    fn largest(&self) -> T {
        let mut max = self.id_list[0];
        for i in 1..self.id_list.len() {
            if self.id_list[i] > max {
                max = self.id_list[i]
            }
        }
        max
    }
}

//impl 块的泛型参数指定了 trait bounds，实现了这些 trait 的类型可以调用其中定义的方法，比如 String 类型
impl<T: PartialOrd + Clone> Category<T> {
    fn largest_one(&self) -> &T {
        let mut max = &self.id_list[0];
        for i in 1..self.id_list.len() {
            if &self.id_list[i] > max {
                max = &self.id_list[i]
            }
        }
        max
    }
}

fn main() {
    let mut cat = Category::new();
    cat.add("apple");
    cat.add("peach");
    let m = cat.largest();
    println!("{}", m);

    let mut cat = Category::new();
    cat.add("apple".to_string());
    cat.add("peach".to_string());
    let m = cat.largest_one();
    println!("{}", m);

    //the method `larger_copy` exists for struct `Pair<String>`,
    //but its trait bounds were not satisfied
    //错误，因为 String 类型没有实现 Copy trait，不能满足 trait bounds，故不能调用 impl 块中定义的函数
    cat.largest();//Error
}
```

可以为**实现了其他 trait 的任意类型**有条件的实现某个 trait。为满足 trait bounds 的所有类型上实现 trait 叫做覆盖实现（blanket implementations）。

```rust
pub trait ToString {
    fn to_string(&self) -> String;
}

//为实现了 Display trait 的任意类型实现 ToString trait
impl<T: fmt::Display> ToString for T {
    fn to_string(&self) -> String {
        //..
    }
}
```

## 生命周期

### 基础概念

Rust 中的生命周期是指引用保持有效的作用域

特点：引用的生命周期在多数情况下是隐式的、可推断的；但在一些情况下，多个引用的生命周期可能以不同的方式**互相关联**，此时需要**手动标注**生命周期

主要目标是避免悬垂引用（dangling reference），比如

```rust
fn main() {
    let r;
    {
        let x = 5;
        r = &x;// r 和 x 二者以引用的形式进行了关联
    }
    println!("{}", r);
}
```

这段代码编译不通过，因为**最后使用 r 时已经离开了它引用的变量 x 的作用域**。

> Rust 通过**借用检查器**比较作用域以判断所有的引用是否合法。

改进：使 x 的生命周期比引用它的变量 r 的生命周期更长

```rust
fn main() {
    let x = 5;//x 开始
    let r = &x;//r 开始
    println!("{}", r);//x 结束、r 结束
}
```

### 标注语法

生命周期参数命名：

- 以 `'` 开头
- 通常全小写，且非常短
- 很多时候用 `'a'`

生命周期标注的位置：

- 在引用符号 & 后
- 在标注时，需要使用空格将标注和引用类型分开

比如

```rust
&i32  //一个引用
&'a i32 //带有显式生命周期的引用
&'a mut i32 //带有显示生命周期的可变引用
```

Q：为何要标注引用的生命周期？

A：用来描述多个引用生命周期之间的关系，而不会影响引用的生命周期长度

### 函数中的生命周期

函数的**泛型生命周期参数**声明在函数名和参数列表之间的 `<>` 中。为啥叫泛型生命周期参数？因为 a 也可以是其他字符串，类似泛型中定义模板时使用的 T。

```rust
fn main() {
    let s1 = String::from("abc");
    let s2 = "wxyz";
    let res = longest(s1.as_str(), s2);
    println!("res:{}", res);
}

fn longest<'a>(x: &'a str, y: &'a str) -> &'a str {
    if x.len() > y.len() {
        x
    } else {
        y
    }
}
```

在该例子中，`longest` 函数中生命周期参数 `'a` 的**实际生命周期是 x 和 y 两个生命周期中较小的那个**。错误示例

```rust
fn main() {
    let s1 = String::from("abc");
    let res;
    {
        let s2 = String::from("wxyz");
        res = longest(s1.as_str(), s2.as_str());//本行编译错误
    }
    println!("res:{}", res);
}

fn longest<'a>(x: &'a str, y: &'a str) -> &'a str {
    if x.len() > y.len() {
        x
    } else {
        y
    }
}
```

错误解释：

1. `s1.as_str()` 作用域为 main 函数
2. `s2.as_str()` 作用域只有两行
3. 根据 1 和 2 可知，返回值 res 的生命周期只有两行，只能在这两行使用 res

从函数返回引用时，返回类型的生命周期参数需要与其中一个参数的生命周期匹配。如果返回的引用没有指向任何定义的生命周期参数，那么它只能引用了函数内创建的值，这就发生了悬垂引用。比如

```rust
fn longest<'a>(x: &'a str, y: &'a str) -> &'a str {
    String::from("mn").as_str()
}
```

解决方法：返回创建的对象，将所有权交给调用者

```rust
fn longest<'a>(x: &'a str, y: &'a str) -> String {
    String::from("mn")
}
```

### 结构体中的生命周期

struct 中除了可以包含自持有的字段，还可以包含引用类型字段。但**要为每个引用添加生命周期标注**。

示例

```rust
fn main() {
    let novel = String::from("Call me Ishmael. Some years ago ...");
    let first_sentence = novel.split('.').next().expect("Could not found a '.'");
    let i = ImportantExcerpt {
        part: first_sentence,
    };
    //...

    // 这段代码中，first_sentence 的存活时间比 i 的存活时间长，所以不会出错
}

//在结构体名后声明生命周期参数
struct ImportantExcerpt<'a> {
    //需要为每个引用类型添加生命周期标注
    part: &'a str,
}
```

在 impl 中为结构体实例添加方法。来自 [p50](https://www.bilibili.com/video/BV1hp4y1k7SV?p=50)

```rust
struct ImportantExcerpt<'a> {
    part: &'a str,
}

impl<'a> ImportantExcerpt<'a> {
    fn level(&self) -> i32 {
        3
    }
    fn annouce_and_return_part(&self, announcement: &str) -> &str {
        println!("attention:{}", announcement);
        self.part
    }
}
```

### 生命周期参数的省略

**生命周期省略规则**是指在一些特殊的情况下，由编译器自动标注生命周期参数；如果你的代码符合这些情况，那么就无需显式标注生命周期。比如

```rust
//这个例子无需为函数参数和返回值标注生命周期参数
fn first_char(s: &str) -> &str {
    let bs = s.as_bytes();
    for (i, &item) in bs.iter().enumerate() {
        if item == b' ' {
            return &s[0..i];
        }
    }
    return &s[..];
}
```

如果你的代码不符合**生命周期省略规则**中定义的特殊情况，那么可能会出现引用的生命周期模糊不清的情况，此时会出现编译错误。解决方法时添加生命周期标注，表明引用间的相互关系。

**输入生命周期和输出生命周期**

- 输入生命周期：来自函数或方法的参数
- 输出生命周期：来自函数或方法的返回值

**三个生命周期省略规则**

在没有显示标注生命周期时，编译器使用这三个规则来确定引用的生命周期。如果应用完这三个规则后仍然无法确认，那么会编译报错。这三条规则是：

1. 每个引用类型的参数都有自己的生命周期。（用来确定输入生命周期）
2. 如果只有一个输入生命周期参数，那么该生命周期被赋值给所有输出生命周期参数。（用来确定输出生命周期）
3. 如果有多个输入生命周期参数且其中一个是 `&self` 或 `&mut self`，那么 self 的生命周期会被赋给所有的输出生命周期参数。（用来确定输出生命周期）

这些规则适用于 fn 定义和 impl 块。

示例 1

```rust
fn first_word(s: &str) -> &str { ... }

//-----------------------------------------------
//步骤 1：为每个参数都指定一个生命周期
fn first_word<'a>(s: &'a str) -> &str { ... }

//步骤 2：只存在一个生命周期参数时，该生命周期被赋值给输出生命周期参数
fn first_word<'a>(s: &'a str) -> &'a str { ... }
```

示例二

```rust
fn longest(x: &str, y: &str) -> &str { ... }

//---------------------------------------------
//步骤 1：为每个参数都指定一个生命周期
fn longest<'a, 'b>(x: &'a str, y: &'b str) -> &str { ... }

//步骤 2：无法确定返回值生命周期。报错
```

### 静态生命周期

`'static` 是一个特殊的生命周期，表示整个程序的持续时间。例如，所有字符串字面值都拥有该生命周期

```rust
let s: &'static str = "I have a dream";
```

为引用指定 `'static` 生命周期前要考虑：是否需要引用在程序整个生命周期内都存活。

### 一个综合示例

同时使用泛型参数、trait bounds、泛型生命周期参数

```rust
fn longest_with_an_announcement<'a, T>(x: &'a str, y: &'a str, ann: T) -> &'a str
where
    T: Display + Clone,
{
    println!("Announcement: {}", ann);
    if x.len() > y.len() {
        x
    } else {
        y
    }
}
```
