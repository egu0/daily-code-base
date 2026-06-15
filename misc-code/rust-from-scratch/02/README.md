# 第二部分

## 测试

### 编写和运行测试

测试：创建测试函数来验证非测试代码的功能是否和预期一致

一般情况下，测试函数体要执行的三个操作：（通常被称为 3A 操作）

1. 准备数据/状态（arrange）
2. 运行测试代码（act）
3. 断言结果（assert）



**测试函数**

需要使用 test 属性进行标注



**运行测试**

使用 `cargo test` 运行所有测试函数：rust 会构建一个 test runner 可执行文件，它在运行时会运行标注了 test 的函数并报告其运行是否成功



**示例**

使用 `cargo new xxx --lib` 创建 library 项目，其中会生成一个 test module，里面有一个 test 函数。

```rust
pub fn add(left: usize, right: usize) -> usize {
    left + right
}

#[cfg(test)]
mod tests {//测试模块
    use super::add;

    #[test]//使用该属性标注本函数为测试函数
    fn it_works() {
        let result = add(2, 2);
        assert_eq!(result, 4);
    }
}
```

使用 `cargo test` 运行项目中的所有测试函数



**测试失败**

每个测试函数都运行在一个新线程里，如果测试函数在执行过程中遇到了 panic，那么主线程就会将该测试标记为失败。

### 断言

- `assert!( bool_expr )` 接收一个布尔表达式，当表达式结果为 true 时表示断言成功，会继续执行；反之结果为 false 时表示断言失败，会 panic 出错误
- `assert_eq!( left_value, right_value)` 相等断言，接收两个值，分别成为左值和右值；如果断言失败，则会自动打印出两个值，所以要求参数实现了 PartialEq 和 Debug 两个 trait（所有的基本类型和标准库里大部分类型都实现了）
- `assert_ne!( left_value, right_value)` 不相等断言，用法同 `assert_eq!()`

三者都来自标准库，可以直接使用。示例

```rust
pub fn add_two(val: i32) -> i32 {
    val + 2
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    pub fn it_add_two_works() {
        assert!(3 == add_two(1));
        assert_eq!(3, add_two(1));
        assert_ne!(4, add_two(1));
    }
}
```

**为断言添加自定义错误信息**

三个断言可以往后继续添加字段，用来自定义错误信息。传递的一个或多个参数会被传递给 `format!()` 宏

```rust
    assert!(2 == add_two(1), "断言失败，add_two(1) 结果是 {}，不等于 2", add_two(1));

    assert_eq!(2, add_two(1), "相等断言失败，因为 2 != {}", add_two(1));

    assert_ne!(3, add_two(1), "不相等断言失败");
```

### 检查是否 panic

测试除了验证代码的返回值是否正确，还需要验证代码是否**如预期地发生了错误**。

做法：使用 `should_panic` 属性

示例

```rust
    #[test]
    #[should_panic]
    pub fn panic_01() {
        panic!("error");
    }
```

执行 `cargo test`，这个测试的结果是 `passed`

如果测试代码在执行的过程中没有发生 panic，那么测试结果是 `failed`



**为 should_panic 属性添加一个可选的 expected 参数，使 panic 断言更精确**

`expected` 参数的作用：检查失败消息中是否包含所指定的文字

```rust
//断言：发生 panic 并且 panic 中的消息包含 expected
#[test]
#[should_panic(expected = "network error")]
pub fn panic_02() {
    let is_network_error = true;
    if is_network_error {
        panic!("network error, please check your internet connection.");
    } else {
        panic!("server error, please contact the system administrator.");
    }
}
```

### 使用 Result<T, E>

无需 panic，可以使用 `Result<T, E>` 作为测试函数的返回类型编写测试：

- 返回 Ok：表示测试通过
- 返回 Err：表示测试失败

注意：将 Result 作为测试函数的返回值时不要标注 should_panic 属性，因为无需在其中进行 panic。

示例

```rust
//返回 Ok 是结果是 passed，返回 Err 时结果是 failed
#[test]
pub fn panic_03() -> Result<(), String> {
    let flag = false;
    if flag {
        Ok(())
    } else {
        Err("error happened.".to_string())
    }
}
```

### 控制测试运行

执行 `cargo test` 命令时的默认行为：

- Rust 会构建一个 test runner 可执行文件，并通过执行它来**并行**运行所有测试
- 测试的输出：不显示 passed 的测试输出，显示 failed 的测试输出

命令行参数：

- 针对 `cargo test` 命令的参数：紧跟 cargo test，比如 `cargo test --help`
- 针对可执行程序的参数：放在 `--` 之后，比如 `cargo test -- --help`



**并行运行测试**

默认使用多个线程并行运行多个测试，速度快。

此时**需要确保**：

- 测试之间没有依赖关系；
- 多个测试不依赖于某个共享状态，比如环境、工作目录、环境变量等



Q：如何控制测试**顺序执行**呢？

答：`cargo test -- --test-threads=1`



Q：默认情况下，Rust 会在测试通过时隐藏测试函数的输出内容。如何不隐藏呢？

A：`cargo test -- --show-output`



Q：默认情况下执行 `cargo test` 时会运行所有模块中的所有测试，如何只运行指定的测试？

A：执行  `cargo test KEYWORD` 会运行所有测试函数名中包含 `KEYWORD` 的测试，也会运行模块名中包含 `KEYWORD` 的模块中的所有测试



**执行时忽略某些测试**

做法：为测试函数标注 ignored 属性

```rust
#[test]
#[ignore]
#[should_panic]
pub fn abc() {
    panic!("ppp");
}
```

Q：如何运行所有被忽略的测试？

A：`cargo test -- --ignored`

### 测试分类-0

- **单元测试**：小而专注；一次仅测试一个模块；可测试 private 接口
- **集成测试**：在库外部测试，像外部代码使用你的代码一样；可能在测试中同时使用到多个模块；只能使用 public 接口

### 单元测试-1

单元测试的主要目的是确保软件中的最小可测试单元，如函数、方法或类，能够独立正常工作。这有助于提高整个程序的质量、可靠性和可维护性

我们通常把单元测试和被测试的代码都放在 `src` 目录下的同一个文件中，并且约定每个源代码文件都要建立一个 `test` 模块来放这些测试函数，并使用 `#[cfg(test)]` 对该模块进行标注。

**为模块标注 #[cfg(test)] 的作用**：

1. 告诉 Rust 该模块在指定 `test` 配置选项时**才被包含、编译和运行测试**（指定 test 配置选项即使用 `cargo test` 命令）
2. 告诉 Rust 该模块在指定 `build` 配置选项时**不进行包含和编译等操作**（执行 `cargo build` 命令）



示例：（`lib.rs`）

```rust
fn plus_one(val: i32) -> i32 {
    val + 1
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    pub fn it_plus_one() {
        assert!(2 == plus_one(1));//可以测试 private 接口
    }
}
```



### 集成测试-2

在 Rust 中，集成测试完全位于被测试库的外部，调用库的方式和和其他代码调用这个库的方式是一样的。这也意味着，集成测试只能测试被测试库对外公开的接口。

集成测试的目的：测试被测试库的多个部分是否能正确的一起工作。因为经过单元测试的代码在集成在一起运行时也会发生各种问题。

集成测试的**覆盖率**是一个很重要的指标。

集成测试所在目录不同于单元测试，不需要使用 `#[cfg(test)]` 标注。

**示例**

创建与 `src` 同级的目录 `tests`；在其中创建测试文件 `integration-test.rs`

> 注意：Rust 会将 tests 目录下的所有测试文件都看成是一个 crate

```rust
use adder;//导入被测试库

#[test]//使用 test 属性标注
fn it_add_two() {
    assert!(3 == adder::add_two(1));
}
```

运行 `cargo test`，此时会编译并执行 `tests` 目录下的测试文件。输出为三部分，分别是单元测试输出、集成测试输出、其他内容。



Q：如何运行某个集成测试文件中的所有测试函数？

A：`cargo test --test 集成测试文件名`，比如 `cargo test --test integration_tests`



Q：如何只运行某个集成测试函数？

A：`cargo test 函数名`



**如何在多个集成测试函数之间共享条目？**

一、创建 common 模块：在 tests 目录中创建 common 子目录，并在其中创建 mod.rs，即 `tests/common/mod.rs`，并在其中创建用于共享的条目，例如

```rust
pub fn setup() {}
```

二：在集成测试文件中导入 common 模块并使用其中定义的共享条目，比如

```rust
use adder;

mod common;//导入 common 模块

#[test]
fn it_add_two() {
    common::setup();//使用 common 模块中定义的条目
    assert!(3 == adder::add_two(1));
}
```



### 注意

- `tests` 下的每一个 rs 文件都是一个 crate，在其中可以引入 library crate 中的条目，即 `src/lib.rs` 中的条目；而无法引入 `src/main.rs` 中的条目

- 如果一个 package 只有 `src/main.rs` 、没有 `src/lib.rs`，那么它无法对它做集成测试。要想对它做集成测试，通常需要：1将核心逻辑放到 `src/lib.rs` 中，2在 `src/main.rs` 中使用胶水代码将逻辑组合起来，3在 tests 下进行集成测试

- 可以在 binary crate 中做单元测试

## 实战

本章内容：构建功能类似 `grep` 命令的可执行程序，用于搜索文本文件中包含 keyword 的所有行，具体划分为六小节：

1. 接收命令行参数
2. 读取文件
3. 重构代码：模块化和错误处理
4. 使用 TDD（测试驱动开发）开发库功能
5. 使用环境变量
6. 将错误消息写入标准错误而不是标准输出

最终效果：执行 `cargo run <keyword> <file_path>` 进行搜索

### 接收命令行参数

创建项目

```sh
cargo new minigrep
```

`main.rs`

```rust
use std::env;

fn main() {
    let args = env::args();
    let command_line_args: Vec<String> = args.collect();
    // &command_line_args[0] 是可执行文件
    let keyword = &command_line_args[1];
    let filepath = &command_line_args[2];
    println!("{:?}, {:?}", keyword, filepath);
}
```

### 读取文件

添加读取文本文件的代码逻辑

```rust
use std::{env, fs};

fn main() {
    let args = env::args();
    let command_line_args: Vec<String> = args.collect();
    // &command_line_args[0] 是可执行文件
    let keyword = &command_line_args[1];
    let filepath = &command_line_args[2];
    println!("args: {:?}, {:?}", keyword, filepath);

    let content = fs::read_to_string(filepath).expect("Something went wrong when reading file");
    println!("text from target file:\n{}", content);
}
```



在项目根目录下准备文本文件 `poem.txt`

```
I'm nobody! Who are you?
Are you nobody, too?
Then there's a pair of us - don't tell!
They'd banish us, you know.

How dreary to be somebody!
How public, like a frog
To tell your name the livelong day
To an admiring bog!
```



测试功能：`cargo run whatever poem.txt`

### 重构代码

上一节代码存在的问题：（分为两类，程序架构类和错误处理类）

1. 参数解析和文件读取两个功能的代码耦合在了一起。应该做单独拆分
2. 随着代码的增加，其中的变量也会增多，结果是在编写业务时难以追踪变量。应该将变量组织到结构体中
3. 读取文件出错时总会打印 expect 的信息，错误提示不明确，不知道具体是什么原因。应该将具体的错误原因打印出来
4. 参数解析：如果没有给出两个参数，则会报越界错误。应该首先检查参数数量



**补充：二进制程序关注点分离的指导性原则**

1. 将程序拆分为 main.rs 和 lib.rs，并将业务逻辑放入 lib.rs
2. 如果 main.rs 程序逻辑较少时，可以将它放在 main.rs；如果逻辑较复杂时，需要将它从 main.rs 提取到 lib.rs



**改进**，参考 [p63](https://www.bilibili.com/video/BV1hp4y1k7SV/?p=63)-65，最终结果：（老师重构代码的思路让俺耳目一新，推荐观看）

`lib.rs`

```rust
use std::error::Error;
use std::fs;

pub fn run(params: InputParam) -> Result<(), Box<dyn Error>> {
    let content = fs::read_to_string(params.filepath)?;
    println!("text from target file:\n{}", content);
    Ok(())
}

pub struct InputParam {
    pub keyword: String,
    pub filepath: String,
}

impl InputParam {
    pub fn parse_params(args: &[String]) -> Result<InputParam, &str> {
        if args.len() < 3 {
            return Err("no enough arguments!");
        }

        //放弃一些性能来换取简洁性，省去管理生命周期的代码
        let keyword = args[1].clone();
        let filepath = args[2].clone();

        Ok(InputParam { keyword, filepath })
    }
}
```

`main.rs`

```rust
use minigrep;
use minigrep::InputParam;
use std::env;
use std::process;

fn main() {
    let args: Vec<String> = env::args().collect();

    // r.unwrap_or_else(c): 如果 r 是 Ok 那么正常解包；否则会走传递的闭包 c 的逻辑
    let params = InputParam::parse_params(&args).unwrap_or_else(|err| {
        println!("error when parsing arguments: {}", err);
        process::exit(1);
    });

    if let Err(e) = minigrep::run(params) {
        println!("Application error: {}", e);
        process::exit(1);
    }
}
```

### 使用 TDD 开发库功能

TDD（Test-Driven Development），测试驱动开发，是中众多开发方式中的一种，能对代码的设计工作起到指导和帮助的作用。通过先编写测试、再编写使测试能够通过的代码，有助于开发过程中保持较高的测试覆盖率。步骤为：

1. 编写一个会失败的测试，运行该测试确保它因为预期的原因失败
2. 编写或修改刚好足够的代码，让 1 中的测试通过
3. 重构 2 中添加的或修改的代码，确保测试能始终通过
4. 返回 1 继续



`lib.rs` 最终部分代码（参考 [p66](https://www.bilibili.com/video/BV1hp4y1k7SV?p=66)）

```rust
use std::error::Error;
use std::fs;

pub fn run(params: InputParam) -> Result<(), Box<dyn Error>> {
    let content = fs::read_to_string(params.filepath)?;

    let result_lines = search(&params.keyword, &content);
    for line in result_lines {
        println!("{}", line);
    }

    Ok(())
}

fn search<'a>(keyword: &str, content: &'a str) -> Vec<&'a str> {
    let mut result = Vec::new();

    for line in content.lines() {
        if line.contains(keyword) {
            result.push(line);
        }
    }

    result
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_search() {
        let keyword = "duct";
        let content = "\n
rust:
safe, fast, productive.
provide you another experience.";

        assert_eq!(vec!["safe, fast, productive."], search(keyword, content));
    }
}
```

### 使用环境变量

继续使用 TDD 开发功能：判断环境变量 `CASE_INSENSITIVE` 是否存在，如果存在则忽略大小写，否则不忽略大小写

`lib.rs` 最终代码

```rust
use std::error::Error;
use std::{env, fs};

pub fn run(params: InputParam) -> Result<(), Box<dyn Error>> {
    let content = fs::read_to_string(params.filepath)?;

    let result_lines = if params.case_sensitive {
        search(&params.keyword, &content)
    } else {
        search_case_insensitive(&params.keyword, &content)
    };

    for line in result_lines {
        println!("{}", line);
    }

    Ok(())
}

pub struct InputParam {
    pub keyword: String,
    pub filepath: String,
    pub case_sensitive: bool,
}

impl InputParam {
    pub fn parse_params(args: &[String]) -> Result<InputParam, &str> {
        if args.len() < 3 {
            return Err("no enough arguments!");
        }

        let keyword = args[1].clone();
        let filepath = args[2].clone();

        //查看环境变量 CASE_INSENSITIVE 是否存在，进而得到 case_sentitive 变量
        let case_sensitive = env::var("CASE_INSENSITIVE").is_err();

        Ok(InputParam {
            keyword,
            filepath,
            case_sensitive,
        })
    }
}

fn search<'a>(keyword: &str, content: &'a str) -> Vec<&'a str> {
    let mut result = Vec::new();

    for line in content.lines() {
        if line.contains(keyword) {
            result.push(line);
        }
    }

    result
}

fn search_case_insensitive<'a>(keyword: &str, content: &'a str) -> Vec<&'a str> {
    let keyword = keyword.to_lowercase();
    let mut result = Vec::new();

    for line in content.lines() {
        if line.to_lowercase().contains(&keyword) {
            result.push(line);
        }
    }

    result
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn case_sensitive() {
        let keyword = "duct";
        let content = "\n
Rust:
safe, fast, productive.
provide you another experience.
Duct tape.";

        assert_eq!(vec!["safe, fast, productive."], search(keyword, content));
    }

    #[test]
    fn case_insensitive() {
        let keyword = "rUsT";
        let content = "\n
Rust:
safe, fast, productive.
provide you another experience.
Trust me.";

        assert_eq!(
            vec!["Rust:", "Trust me."],
            search_case_insensitive(keyword, content)
        );
    }
}
```

然后运行 `CASE_INSENSITIVE=1 cargo run to poem.txt` 测试

### 将错误信息写入标准错误

当前的错误信息都是通过 `println` 输出，它将内容输出到了标准输出（`stdout`）

需要实现的效果：将错误信息输出到标准错误（`stderr`）

方法：通过 `eprintln` 宏输出

`main.rs`

```rust
fn main() {
    let args: Vec<String> = env::args().collect();

    let params = InputParam::parse_params(&args).unwrap_or_else(|err| {
        eprintln!("error when parsing arguments: {}", err);//替换为 eprintln
        process::exit(1);
    });

    if let Err(e) = minigrep::run(params) {
        eprintln!("Application error: {}", e);//替换为 eprintln
        process::exit(1);
    }
}
```

测试：`cargo run > output.txt`，其中的 `> output.txt` 表示将标准输出重定向到 `output.txt` 文件

效果：错误信息出现在屏幕上，而非 `output.txt` 中

## 闭包

闭包是可以捕获其所在环境的匿名函数。

闭包的特点：

- 是匿名函数
- 可以保存为变量、作为参数和作为返回值
- 可以在一个地方创建闭包，然后在另一个上下文中调用闭包完成运算
- 可以从其定义的作用域捕获值



### 类型推断和标注

- 闭包不要求标注参数类型和返回值类型，而是由编译器推断
- 可以手动为闭包标注类型



注意：闭包的定义最终只会为参数或返回值推断出唯一的具体类型。比如

```rust
fn main() {
    let return_it_back = |x| x;
    return_it_back(1);//会推断出闭包的参数类型和返回值类型都为 i32
    return_it_back("String"); //错误，因为 &str 和 i32 类型不匹配
}
```



### 示例：优化代码

```rust
//模拟耗时的业务
fn simulated_expensive_calculation(indensity: u32) -> u32 {
    println!("calculating solwly...");
    thread::sleep(Duration::from_secs(2));
    indensity
}

fn generate_workout(indensity: u32, random_number: u32) {
    if indensity < 25 {
        println!(
            "Today, do {} pushups!",
            simulated_expensive_calculation(indensity)
        );
        println!(
            "Next, do {} situps!",
            simulated_expensive_calculation(indensity)
        );
    } else {
        if random_number == 3 {
            println!("Take a break today! Remember to stay hydrated!");
        } else {
            println!(
                "Today, run for {} minutes!",
                simulated_expensive_calculation(indensity)
            );
        }
    }
}
```

分析以上代码：`generate_workout` 中多次调用了 `simulated_expensive_calculation` ，可以将后者的调用提出来，然后用一个局部变量接收它，代码如下

```rust
fn generate_workout(indensity: u32, random_number: u32) {
    //将方法调用提取出来
    let expensive_result = simulated_expensive_calculation(indensity);
    
    if indensity < 25 {
        println!("Today, do {} pushups!", expensive_result);
        println!("Next, do {} situps!", expensive_result);
    } else {
        if random_number == 3 {
            println!("Take a break today! Remember to stay hydrated!");
        } else {
            println!("Today, run for {} minutes!", expensive_result);
        }
    }
}
```

分析以上代码：在 `indensity >=25 && random_number == 3` 时没有使用到 `expensive_result` 这个变量，无需调用对应的计算方法。解决方法：定义闭包，只有在使用时才调用处理业务

```rust
fn generate_workout(indensity: u32, random_number: u32) {
    //定义闭包，将业务放到闭包体中。好处：只有调用时才执行闭包体中的代码
    let expensive_result = |num| {
        return simulated_expensive_calculation(num);
    };
    
    if indensity < 25 {
        println!("Today, do {} pushups!", expensive_result(indensity));
        println!("Next, do {} situps!", expensive_result(indensity));//重复调用
    } else {
        if random_number == 3 {
            println!("Take a break today! Remember to stay hydrated!");
        } else {
            println!("Today, run for {} minutes!", expensive_result(indensity));
        }
    }
}
```

分析以上代码：在 `indensity < 25` 的 if 块中又出现了重复计算

解决方法一：

```rust
fn generate_workout(indensity: u32, random_number: u32) {
    let expensive_result = |num| {
        return simulated_expensive_calculation(num);
    };
    
    if indensity < 25 {
        let res = expensive_result(indensity);//缺点：可能造成大量的代码重复
        println!("Today, do {} pushups!", res);
        println!("Next, do {} situps!", res);
    } else {
 ...
```

解决方法二：**记忆化**（memorization）或**延迟计算**（lazy evaluation）

做法：**创建一个 struct，它持有闭包和闭包调用结果**。只有在需要时才调用闭包，同时将计算结果保存到 struct 中。

```rust
struct Cacher<T>
where
    T: Fn(u32) -> u32,
{
    calculation: T,
    value: Option<u32>,
}

impl<T> Cacher<T>
where
    T: Fn(u32) -> u32,
{
    //用于创建 Cacher
    fn new(calculation: T) -> Self {
        Cacher {
            calculation: calculation,
            value: None,
        }
    }

    //用于计算值或获取值
    fn value(&mut self, arg: u32) -> u32 {
        match self.value {
            Some(val) => val,
            None => {
                let result = (self.calculation)(arg);
                self.value = Some(result);
                result
            }
        }
    }
}

fn generate_workout(indensity: u32, random_number: u32) {
    let mut expensive_result = Cacher::new(|num| {
        println!("calculating solwly...");
        thread::sleep(Duration::from_secs(2));
        num
    });

    if indensity < 25 {
        println!("Today, do {} pushups!", expensive_result.value(indensity));
        println!("Next, do {} situps!", expensive_result.value(indensity));//这里调用方法时获取了缓存的值
    } else {
        if random_number == 3 {
            println!("Take a break today! Remember to stay hydrated!");
        } else {
            println!(
                "Today, run for {} minutes!",
                expensive_result.value(indensity)
            );
        }
    }
}
```

分析以上代码存在的问题：

1. 为 Cacher 实例的 value 方法传入不同的参数，总会返回一样的结果。解决方法：使用 hashmap 存储所有已经计算的结果
2. 通过 `Cacher::new` 创建 Cacher 实例时传递的闭包类型是确定的，即只能接收一个 u32 类型的参数和 u32 类型的返回值。解决方法：引入多个泛型参数。一个示例：

```rust
struct Cacher<T, U, W>
where
    T: Fn(U) -> W,
{
    calculation: T,
    source: Option<U>,
    result: Option<W>,
}

impl<T, U, W> Cacher<T, U, W>
where
    T: Fn(U) -> W,
{
    fn new(calculation: T) -> Self {
        Cacher {
            calculation: calculation,
            source: None,
            result: None,
        }
    }
}
```



### 让 struct 持有闭包

定义 struct 时需要定义其中所有字段的类型，包括用于存储闭包实例的字段

Rust 中所有的闭包都至少实现了这三个 trait 之一：`Fn`、`FnMut` 和 `FnOnce`

示例代码，见上一节末尾



### 在闭包中捕获环境

闭包可以访问<u>定义它的</u>作用域内的变量，而普通函数则不能。

注意：闭包的这种机制会产生内存开销。函数没有这种机制所以没有这部分的内存开销

示例

```rust
fn main() {
    let x = 4;

    let equal_to_x = |z| z == x;

    let y = 4;

    assert!(equal_to_x(y));
}
```



**闭包从所在环境捕获值的三种方式**

- `FnOnce`：取得所有权
- `FnMut`：取得可变借用
- `Fn`：取得不可变借用

这三种捕获值的方式和**在函数中获得参数的三种方式**类似，即为函数传递所有权、可变借用、不可变借用



**move 关键字**

在 `||` 前使用 move 关键字可以强制闭包取得它所使用的环境值的所有权。

常见场景：将闭包传递给新线程的同时将数据的所有权交给新线程

示例

```rust
    let x = vec![1, 2, 3];

    let equal_to_x = move |z| z == x;//将 x 的所有权交给闭包

    assert!(equal_to_x(vec![1, 2]));

    println!("{:?}", x);//错误
```

## 迭代器

迭代器的职责：

- 遍历每一项
- 确定遍历何时完成

Rust 中的迭代器是**懒惰的**，即除非调用迭代器的消费方法，否则迭代器本身没有任何效果



示例

```rust
fn main() {
    let v = vec![1, 2, 3];
    let v_iter = v.iter();

    for item in v_iter {
        println!("Got {}", item);
    }
}
```



### Iterator trait

- 所有迭代器都实现了 Iterator trait
- Iterator 定义于标准库，它的大致定义如下

```rust
pub trait Iterator {
    type Item;
    
    fn next(&mut self) -> Option<Self::Item>;
    
    //其他的默认实现的方法
}
```

注：`type Item` 和 `Self::Item` 定义了与该 trait 关联的类型



实现 Iterator trait 的类型时仅需实现 next 方法，调用该方法时：

- 每次返回迭代器中的一项，并将它包裹在 Some 中
- 迭代结束时返回 None



示例

```rust
    #[test]
    fn next() {
        let x = vec![1, 2, 4];
        let mut iterator = x.iter();

        assert_eq!(iterator.next(), Some(&1));
        assert_eq!(iterator.next(), Some(&2));
        assert_eq!(iterator.next(), Some(&4));
        assert_eq!(iterator.next(), None);
    }
```



### 获取迭代器

获取迭代器的三种方法

- `iter`：创建迭代器，调用 next 方法获取的值是指向 vector 中元素的不可变引用
- `into_iter`：创建迭代器时会将元素以及所有权移动到新的作用域
- `iter_mut`：创建迭代器，调用 next 方法获取的值是指向 vector 中元素的可变引用



### 消耗迭代器的方法

在标准库的定义中，Iterator trait 有一些默认实现的方法，其中一部分会调用 next 方法，我们把这些调用 next 的方法叫做 “**消耗型适配器**”，因为调用它们会把迭代器消耗尽。

例如 sum 方法：它会在调用时会取得迭代器的所有权，并通过反复调用 next 遍历所有元素，每次遍历都把元素添加到一个总和中，结束时返回总和

sum 方法使用示例

```rust
    #[test]
    fn sum() {
        let v = vec![1, 2, 3];
        let v_iter = v.iter();
        let sum: i32 = v_iter.sum();
        assert!(sum == 6);

        println!("next: {:?}", v_iter.next()); //错误，因为所有权移动了
    }
```

> 有时候，我们把定义在 Iterator trait 上的除去 next 的另一些方法叫做 “迭代器适配器”



另一个消耗型适配器：filter 方法

- 接收一个闭包，用于处理每一个元素，返回 bool 类型
- 如果闭包返回 true，那么当前元素将会包含在 filter 产生的迭代器中
- 如果闭包返回 false，那么当前元素将不会包含在 filter 产生的迭代器中

示例

```rust
#[derive(PartialEq)]
pub struct Shoe {
    pub size: u32,
    pub style: String,
}

pub fn shoes_in_my_size(shoes: Vec<Shoe>, shoe_size: u32) -> Vec<Shoe> {
    shoes
        .into_iter()
        .filter(|item| item.size == shoe_size)
        .collect()
}

#[cfg(test)]
mod tests {
    use std::vec;
    use crate::{shoes_in_my_size, Shoe};

    #[test]
    fn filter() {
        let v = vec![
            Shoe { size: 32, style: "Deli".to_string(), },
            Shoe { size: 43, style: "Peak".to_string(), },
            Shoe { size: 43, style: "Nike".to_string(), },
        ];
        let v2 = shoes_in_my_size(v, 43);
        assert!(
            v2 == vec![
                Shoe { size: 43, style: "Peak".to_string(), },
                Shoe { size: 43, style: "Nike".to_string(), },
            ]
        );
    }
}
```



### 产生迭代器的方法

Iterator trait 中的一些默认实现的方法可以把调用者转换为其它类型的迭代器。

例如 map 方法：接收一个闭包参数，该闭包用于处理每个元素；最终返回一个新的迭代器

示例

```rust
    #[test]
    fn map() {
        let v = vec![1, 2, 3];
        // Vec<_>：不指定类型，交给编译器自行推断
        let v2: Vec<_> = v.iter().map(|x| x * 2).collect();//注：这里的 collect 是一个消耗型适配器方法
        assert!(v2 == vec![2, 4, 6]);
    }
```



### 创建自定义迭代器

功能：从 1 遍历到 5

```rust
fn main() {}

pub struct Counter {
    pub value: u32,
}

impl Counter {
    pub fn new() -> Self {
        Counter { value: 0 }
    }
}

impl Iterator for Counter {
    type Item = u32;

    fn next(&mut self) -> Option<Self::Item> {
        if self.value < 5 {
            self.value += 1;
            Some(self.value)
        } else {
            None
        }
    }
}

#[test]
fn test_counter() {
    let mut counter = Counter::new();
    assert_eq!(counter.next(), Some(1));
    assert_eq!(counter.next(), Some(2));
    assert_eq!(counter.next(), Some(3));
    assert_eq!(counter.next(), Some(4));
    assert_eq!(counter.next(), Some(5));
    assert_eq!(counter.next(), None);
}
```

使用该迭代器，示例：两个向量相乘和过滤

```rust
#[test]
fn test_fun() {
    let v: Vec<_> = Counter::new()
        .zip(Counter::new().skip(1))
        .map(|(m, n)| m * n)
        .filter(|n| n % 3 == 0)
        .collect();
    assert!(v == vec![6, 12]);
    //  1,2,3,4,5
    //  2,3,4,5,N
    //              *
    //  2,6,12,20,N
}
```



### 重构实战一章的代码

<https://gitee.com/egu0/rust_practices/commit/6a8465cfbab0afe401658bc1ca199b1f044d7c59>

## cargo 使用

### 开发配置和发布配置

开发配置或发布配置（`dev profile` 或 `release profile`），是一系列预定义配置，可以使开发人员对代码编译拥有更多的控制权

这两个配置：

- dev profile，适用于开发环境，执行 `cargo build` 命令时会使用该配置
- release profile，适用于发布代码时，执行 `cargo build --release` 命令时会使用该配置



**覆盖默认配置**：在 `Cargo.toml` 中添加 `[profile.xxx]` 区域，在块中定义所需配置。示例：

```toml
[package]
name = "profile-demo"
version = "0.1.0"
edition = "2021"

[dependencies]

[profile.dev]
opt-level = 1

[profile.release]
opt-level = 3
```



更多关于 cargo profiles 的[参考](https://doc.rust-lang.org/stable/cargo/reference/profiles.html)

### 文档注释

- 使用 markdown 语法，用来说明公共 API 如何使用
- 最终会生成 HTML 文档
- 使用 `///` 注释



示例（`lib.rs`）

```rust
/// Adds one to the given number.
///
/// # Examples
/// ```
/// let arg = 5;
/// let answer = my_crate::add_one(arg);
///
/// assert_eq!(6, answer);
/// ```
pub fn add_one(x: i32) -> i32 {
    x + 1
}
```

查看效果：运行 `cargo doc --open` 命令，会先生成 html 文档然后用默认浏览器打开它



示例代码中的 `# Examples` 表示**示例章节**，用来放置示例代码段。除了它之外还有一些**其它章节**：

- `Panics`：用来说明可能发生 panic 的情况
- `Errors`：函数返回 Result 时在该章节描述可能的错误种类以及可导致错误的条件
- `Safety`：函数处于 unsafe 调用时在该章节解释函数 unsafe 的原因，以及调用者的使用前提



运行 `cargo test` 时，会把注释中（示例章节中）的示例代码块作为测试来运行。示例 lib.rs

```rust
/// Adds one to the given number.
///
/// # Examples
/// ```
/// let arg = 5;
/// let answer = profile_demo::add_one(arg);
///
/// assert_eq!(6, answer);
/// ```
pub fn add_one(x: i32) -> i32 {
    x + 1
}
```



用于描述**外部条目**的注释：

- 区别于用于描述内部条目（比如函数、struct、impl 块）的注释，这种注释用于描述外部条目，比如 crate 或模块
- 使用 `//!` 作为注释开头

示例：

```rust
//! # profile-demo
//!
//! `profile-demo` is a collection of utilities to make performing calculations
//! more conveninent.

/// Adds one to the given number.
///
/// # Examples
/// ```
/// let arg = 5;
/// let answer = profile_demo::add_one(arg);
///
/// assert_eq!(6, answer);
/// ```
pub fn add_one(x: i32) -> i32 {
    x + 1
}
```

通过 `cargo doc --open` 查看效果



### pub use 重导出内部条目

问题：开发者通常会把程序结构分为很多层，但使用者想找到深层结构中的某个类型会很费劲。

比如：

- 实际的结构：`my_crate::some_module::another_module::UsefuleType`
- 使用者期望的：`my_crate::UsefulType`

解决：使用 pub use 重新导出，创建一个与内部私有结构不同的对外公共结构



示例：**改进代码**

```rust
//lib.rs
//-------------------------------------------------

//! # Art
//!
//! A library for modeling artistic concepts.

pub mod kinds {
    /// The primary colors according to the RYB color model.
    pub enum PrimaryColor {
        Red,
        Yellow,
        Blue,
    }

    /// The secondary colors according to the RYB color model.
    pub enum SecondaryColor {
        Orange,
        Green,
        Purple,
    }
}

pub mod utils {
    use crate::kinds::*;

    /// Combines two primary colors in equal amounts to create
    /// a secondary color.
    pub fn mix(_c1: PrimaryColor, _c2: PrimaryColor) -> SecondaryColor {
        SecondaryColor::Green
    }
}


//------------------------------------
//main.rs
use profile_demo::kinds::PrimaryColor;
use profile_demo::utils::mix;

fn main() {
    let red = PrimaryColor::Red;
    let yellow = PrimaryColor::Yellow;
    mix(red, yellow);
}
```

分析问题：`main.rs` 中使用 use 导入的类型结构太深

解决方法：在 lib.rs 中使用 pub use 重导出，在 main.rs 中通过简短的路径引入

示例

```rust
//lib.rs
//-------------------------------------------------

//! # Art
//!
//! A library for modeling artistic concepts.

pub use self::kinds::PrimaryColor;
pub use self::kinds::SecondaryColor;
pub use self::utils::mix;

pub mod kinds {
    /// The primary colors according to the RYB color model.
    pub enum PrimaryColor {
        Red,
        Yellow,
        Blue,
    }

    /// The secondary colors according to the RYB color model.
    pub enum SecondaryColor {
        Orange,
        Green,
        Purple,
    }
}

pub mod utils {
    use crate::kinds::*;

    /// Combines two primary colors in equal amounts to create
    /// a secondary color.
    pub fn mix(_c1: PrimaryColor, _c2: PrimaryColor) -> SecondaryColor {
        SecondaryColor::Green
    }
}


//------------------------------------
//main.rs
use profile_demo::PrimaryColor;
use profile_demo::mix;

fn main() {
    let red = PrimaryColor::Red;
    let yellow = PrimaryColor::Yellow;
    mix(red, yellow);
}
```



### 发布 crate

创建账号，获取 API token。将 token 写入本地：

```
cargo login <api-token>
```



在发布 crate 之前，需要在 `Cargo.toml` 的 `[package]` 区域为 crate 添加一些元数据：

- `name`：crate 的唯一标识
- `description`：crate 的描述
- `license`：许可标识（可在[此处](https://spdx.org/licenses/)查看所有许可）；多个许可用 OR 连接
- `version`
- `author`

示例

```rust
[package]
name = "profile-demo"
version = "0.1.0"
edition = "2021"
description = "a brief introduction"
license = "MIT"

# See more keys and their definitions at https://doc.rust-lang.org/cargo/reference/manifest.html

[dependencies]

[profile.dev]
opt-level = 1

[profile.release]
opt-level = 3
```



发布：`cargo publish --allow-dirty`



**发布已存在 crate 的新版本**

做法：修改 `Cargo.toml` 中的 version 值，然后进行发布

参考该[网站](https://semver.org/)来更新语义版本



**撤回版本**

crates.io 没有提供 crate 删除功能，但提供了版本撤回功能，它的作用是：

- 防止其他项目依赖于该版本（将来生成的 `Cargo.lock` 不会使用撤回版本）
- 已经使用该版本的项目可以继续将其作为依赖下载使用（正在使用中的依旧可以正常使用）

命令：

- 撤回版本：`cargo yank --vers 1.0.1`
- 取消撤回：`cargo yank --vers 1.0.1 --undo`



### 工作空间

cargo 工作空间：可以看成包含了多个包的项目，这些包之间共享同一个 `Cargo.lock` 和一个输出文件夹。

它的作用：帮助管理多个相互关联、需要协同开发的 crate



示例：

1. 创建工作空间，其中包括三个包，一个用于编写 binary crate 、另两个用于编写 library crate
2. 其中 binary crate 的 main 函数分别使用另外两个 crate 中提供的 add_one 函数和 add_two 函数

具体步骤：

一、创建工作空间 `workspace-demo`，其中只包含一个空的 `Cargo.toml` 文件

二、通过如下命令在其中创建三个包：`cargo new adder`，`cargo new add-one --lib`，`cargo new add-two --lib`；然后在后两个包的 library crate 中添加函数

三、修改 `workspace-demo/Cargo.toml`：

```toml
[workspace]

resolver = "1"
members = [ "add-one", "add-two","adder"]
```

四、在 adder 中依赖另外两个 crate

`adder/Cargo.toml`

```toml
[package]
name = "adder"
version = "0.1.0"
edition = "2021"

[dependencies]
add-one = { path = "../add-one" }
add-two = { path = "../add-two" }
```

`adder/src/main.rs`

```rust
use add_one::add_one;
use add_two::add_two;

fn main() {
    let i1 = add_one(1);
    println!("{}", i1);

    let i2 = add_two(1);
    println!("{}", i2);
}
```

五、测试执行。`cargo run`



Q：工作空间中的不同包依赖一个相同外部 crate 的不同版本（比如 rand），会发生什么？

A：**cargo 会确保工作空间内所有包中的 crate 使用的依赖都相同**，这个相同的版本记录在工作空间顶层目录的 `Cargo.lock` 中（ cargo 的兼容性设计）

验证：在上边实例中，add-one 依赖 `rand:0.3.14`、add-two 依赖 `rand:0.3.15`，运行 `cargo build` 后查看工作空间顶层目录中的 `Cargo.lock` 验证二者是否最终依赖了相同的版本

结果：是，`/Cargo.lock` 部分内容为

```
[[package]]
name = "add-one"
version = "0.1.0"
dependencies = [
 "rand 0.3.23",
]

[[package]]
name = "add-two"
version = "0.1.0"
dependencies = [
 "rand 0.3.23",
]

[[package]]
name = "adder"
version = "0.1.0"
dependencies = [
 "add-one",
 "add-two",
]
```



Q：如何对工作空间做单元测试

A：执行 `cargo test` 命令，会对工作空间内的所有包的 crate 做单元测试、对所有包做集成测试（需要提前在包中 tests 目录下添加集成测试代码）

Q：如何只对某一个包做测试？

A：`cargo test -p <package-name>`，比如 `cargo test -p add-one`



Q：是否通过 `cargo publish` 发布工作空间？

A：不能。只能发布包含 crate 的包，所以需要手动、挨个进入不同的包然后通过 `cargo publish` 发布

### 二进制 crate

通过 `cargo install xxx` 从 crates.io 安装二进制目标，即可执行程序。

最终的可执行程序默认存在目录是 `$HOME/.cargo/bin/`



注意：

- 执行 `cargo install xxx` 并不会直接下载二进制目标，而是 1拉取代码，2本地编译，3将编译得到的可执行程序移动到指定目录以供使用
- 使用 `cargo install xxx` 安装的包必须包含 binary crate，如果没有则会报错，比如运行 `cargo install rand` 会报错



**通过自定义命令扩展 cargo 命令**

如果 `$PATH` 中的某个二进制名为 `cargo-xxx`，那么用户可以像使用 cargo 的子命令一样运行该程序：`cargo xxx`

查看所有这样的自定义命令：`cargo --list`

优点：通过 `cargo install` 安装扩展，然后像使用内置工具一样使用它



