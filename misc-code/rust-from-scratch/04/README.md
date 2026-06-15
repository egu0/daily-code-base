# 第四部分

## 高级特性

本章内容：

1. 不安全 Rust
2. 高级 Trait
3. 高级类型
4. 高级函数和闭包
5. 宏

### 不安全 Rust

默认情况下，普通的 Rust 代码（之前章节中的代码）在编译期都被强制实施了内存安全的检查。

Rust 提供了 **unsafe 功能**，这类代码和普通 Rust 代码一样，但是没有强制内存安全的检查。



unsafe Rust 存在的原因：

- 静态分析非常保守，有些情况下需要放弃编译器提供的内存检查，告诉编译器“我知道我在做什么并承担责任”
- 计算机硬件本身是不安全的，Rust 需要能够进行底层系统编程



unsafe Rust 优缺点：

- 提供了一些高级功能，也被称为“超能力”
- 开发者需要为 unsafe 代码负责，缺少了编译器的静态分析，程序可能出现各种问题



unsafe Rust 使用：使用 unsafe 关键字定义一个代码块，其中的代码切换到 unsafe 模式

```rust
fn main() {
    unsafe {
	    //blabla...
    }
}
```



**unsafe 超能力**：

1. **解引用原始指针**
2. **调用 unsafe 函数或方法**
3. **访问或修改可变的静态变量**
4. **实现 unsafe trait**

注意：

1. 即便是在 unsafe 代码块中，开发者依然可以获得一定程度的安全性，因为编译器并没有关闭 unsafe 块内的借用检查或其它安全检查
2. 任何内存安全相关的错误必须留在 unsafe 块中
3. 尽可能隔离 unsafe 代码，最好将其封装在安全抽象里并向外提供安全 API



**一、解引用原始指针**

原始指针

- 可变的：`*mut T`
- 不可变的：`*const T`，指针在解引用后不能直接对其赋值

注意：

1. 这里的 `*` 不是解引用符号，而是类型名的一部分
2. 可以在 unsafe 代码块**外**创建原始指针，但只能在 unsafe 代码块内对其解引用



与引用不同，原始指针：

1. 可以忽略借用规则：允许有不可变和可变指针指向同一位置，允许多个指向同一位置的可变指针
2. 可以指向不合理的内存
3. 允许为 null
4. 不会自动被清理

总结：以安全为代价换取了更好的性能和与其他语言或硬件接口交互的能力



```rust
fn main() {
    let mut num = 5;

    let p1 = &mut num as *mut i32;
    let p2 = &num as *const i32;

    println!("p1:{:?}", p1);
    println!("p2:{:?}", p2);

    unsafe {
        *p1 = 2;
        println!("*p1:{}", *p1);
        println!("*p2:{}", *p2);
    }

    //---------------------
    let address = 0x666777888i64;
    let p3 = address as *const i32;
    println!("p3:{:?}", p3);
    unsafe {
        println!("*p3:{}", *p3); //segmentation fault
    }
}
```



为什么要用原始指针？

1. 提供了与 C 语言接口交互的能力
2. 允许开发者构建借用检查器无法理解的安全抽象



**二、调用 unsafe 函数或方法**

unsafe 函数或方法：在定义前加上了 unsafe 关键字

调用条件：

1. 需要手动满足一些条件，因为 Rust 无法对这些条件进行验证（靠查文档）
2. 需要在 unsafe 块里进行调用

```rust
unsafe fn dangerous() {}

fn main() {
    unsafe {
        dangerous();
    }
}
```



示例

```rust
fn split_at_mut(slice: &mut [i32], mid: usize) -> (&mut [i32], &mut [i32]) {
    let len = slice.len();
    assert!(mid <= len);

    //编译错误：不可进行两次可变借用
    (&mut slice[..mid], &mut slice[mid..])
}

fn main() {
    let mut v = vec![1, 2, 3, 4, 5, 6];
    let r: &mut [i32] = &mut v[..];
    let (a, b) = split_at_mut(r, 3);//切割可变切片
    assert_eq!(a, &mut [1, 2, 3]);
    assert_eq!(b, &mut [4, 5, 6]);
}
```

改进：使用 unsafe 改进 split_at_mut 方法

```rust
//函数包含 unsafe 代码时不必把整个函数表记为 unsafe
//将 unsafe 代码包裹在安全函数中是一个常见的抽象
fn split_at_mut(slice: &mut [i32], mid: usize) -> (&mut [i32], &mut [i32]) {
    let len = slice.len();
    let ptr = slice.as_mut_ptr();
    assert!(mid <= len);

    unsafe {
        (
            slice::from_raw_parts_mut(ptr, mid),
            slice::from_raw_parts_mut(ptr.add(mid), len - mid),
        )
    }
}
```



另一个示例

```rust
fn main() {
    let address = 0x0123456usize;
    let r = address as *mut i32;
    let slice: &[i32] = unsafe { slice::from_raw_parts_mut(r, 1000) };
}
```



<u>使用 extern 函数调用外部代码</u>

extern 关键字：简化创建和使用外部函数接口（FFI）的过程

外部函数接口：它允许一种编程语言定义函数，并让其它编程语言能调用这些函数

```rust
extern "C" {// C 表示 ABI
    fn abs(input: i32) -> i32;
}
fn main() {
    unsafe {
        //需要在 safe 中调用外部代码
        let x = abs(-3);
        println!("abs(-3) is {}", x);
    }
}
```

应用二进制接口（ABI，application binary interface）：定义函数在汇编层的调用方式



<u>从其他语言调用 Rust 函数</u>

- 使用 extern 创建接口，其他语言通过它们可以调用 Rust 函数
- 在 fn 前添加 extern 关键字并指定 ABI
- 需要为函数添加 `#[no_mangle]` 注解，避免 Rust 在编译时改变它的名称

```rust
#[no_mangle]
pub extern "C" fn call_from_c() {
    println!("called a rust function from c!");
}
```



**三、访问或修改一个可变的静态变量**

Rust 静态变量：

- 也叫全局变量；区别于常量
- 命名规则为：大写字母，使用 `_` 相连各部分
- 必须标注类型
- 可以分为可变的和不可变的；对于可变静态变量，可能会因为所有权产生某些问题
- 只能存储 `'static` 生命周期的引用，无需显式标注

```rust
static RUNNER: &str = "gyu";

fn main() {
    println!("name is {}", RUNNER);
}
```



<u>常量 vs 静态变量</u>

- 常量：允许在使用时对其进行复制。定义常量示例 `const NUMBER: i32 = 100;`
- 不可变静态变量：类似于常量
- 可变静态变量：读写需要在 unsafe 代码中进行



示例：读写可变的静态类型数据

```rust
//可变静态变量
static mut COUNTER: i32 = 0;

fn add_to_count(inc: i32) {
    unsafe {
        COUNTER += inc;
    }
}

fn main() {
    add_to_count(3);

    unsafe {
        //访问可变静态类型是 unsafe 的
        assert_eq!(3, COUNTER)
    };
}
```



**四、实现不安全 trait**

当某个 trait 中存在至少一个拥有编译器无法校验的不安全因素时，就称这个 trait 是不安全的

声明 unsafe trait 的方式：在定义前添加 unsafe 关键字，比如 `unsafe trait Draw { }`

unsafe trait 只能在 unsafe 代码中实现

```rust
unsafe trait Foo {}

unsafe impl Foo for i32 {}

fn main() {}
```

### 高级 Trait

**在 trait 定义中使用关联类型来指定占位类型**

关联类型（associated type）是 trait 中的类型占位符，可以在 trait 的方法签名中使用它

```rust
pub trait Iterator {
    //定义 Item 关联类型
    type Item;

    //在方法签名中使用 Item 类型
    fn next(&mut self) -> Option<Self::Item>;
}
```



关联类型同泛型一样都是一个“占位符”，由开发者指定类型，二者区别：

- 泛型：实现 trait 时需要标注类型；可以为一个类型多次实现某个 trait
- 关联类型：实现 trait 时无需标注类型；最多只能为单个类型实现一次某个 trait

示例说明：

```rust
pub trait Iterator<T> {
    fn next(&mut self) -> Option<T>;
}

pub struct Person {}
impl Iterator<i32> for Person {
    fn next(&mut self) -> Option<i32> {
        Some(1)
    }
}
impl Iterator<String> for Person {
    fn next(&mut self) -> Option<String> {
        Some(String::new())
    }
}

//--------------------------------------
pub trait Iterator {
    type Item;

    fn next(&mut self) -> Option<Self::Item>;
}

pub struct Stu {}
impl Iterator for Stu {//只能为 Stu 实现一次 Iterator
    type Item = i32;

    fn next(&mut self) -> Option<Self::Item> {
        Some(1)
    }
}
```



**默认泛型参数和运算符重载**

在定义泛型参数时为泛型指定一个默认的类型，语法：

```
<T = CONCRETE_TYPE>
```

比如 `std::ops::Add`

```rust
//Rhs 泛型默认类型为 Slef
pub trait Add<Rhs = Self> {
    type Output;
    
    fn add(self, rhs: Rhs) -> Self::Output;
}
//Add trait 同时具有泛型参数和关联类型
```



这种为泛型参数指定默认类型的技术常用于运算符重载。虽然 Rust 不允许开发者创建自己的运算符及重载任意的运算符，但可以通过实现 `std::ops` 中的那些 trait 来重载一部分相关的运算符。

示例一：相同类型 struct 实例相加，为某类型实现 Add trait

```rust
use std::ops::Add;

#[derive(Debug)]
struct Point {
    x: i32,
    y: i32,
}

impl Add for Point {//省略写法，完整写法为 impl Add<Self> for Point，其中 Self 就是 Point
    type Output = Point;

    fn add(self, rhs: Self) -> Self::Output {
        Point {
            x: self.x + rhs.x,
            y: self.y + rhs.y,
        }
    }
}

fn main() {
    let p3 = Point { x: 1, y: 1 } + Point { x: 2, y: 2 };
    println!("{:?}", p3);
}
```

示例二：使两个不相同类型的 struct 实例相加，做法是实现 Add trait 时为它指定泛型参数

```rust
#[derive(Debug)]
struct Millimeters(u32);
struct Meters(u32);

// 为 Add 指定泛型参数 Meters，表示允许：Millimeters {..} + Meters {..}
impl Add<Meters> for Millimeters {
    type Output = Millimeters;

    fn add(self, rhs: Meters) -> Self::Output {
        Millimeters(self.0 + (rhs.0 * 1000))
    }
}

fn main() {
    let m = Millimeters(8) + Meters(1);
    println!("millimeters:{:?}", m);
}
```



**调用 struct 实例的同名方法**

示例：struct 实例拥有三个同名方法，分别来自自身和所实现的 trait

```rust
trait Pilot {
    fn fly(&self);
}
trait Wizard {
    fn fly(&self);
}

//----------------
struct Human;

impl Pilot for Human {
    fn fly(&self) {
        println!("This is yor captain speaking.")
    }
}

impl Wizard for Human {
    fn fly(&self) {
        println!("Up!")
    }
}

impl Human {
    fn fly(&self) {
        println!("*waving arms furiously*")
    }
}

fn main() {
    let h = Human {};
    h.fly();
}
```

结果：通过 `.` 调用时，会调用 `impl Human {}` 中定义的 `fly` 方法

问题：如何调用所实现 trait 中的 fly 方法呢？

答：使用<u>完全限定语法</u>（fully qualified syntax），格式为 `Trait名::方法名(&实例, 方法参数)`

示例：

```rust
fn main() {
    let h = Human {};
    h.fly();

    Pilot::fly(&h);
    Wizard::fly(&h);
    Human::fly(&h); //等同于 h.fly()
}
```

问题：如果某个类型包含多个<u>同名的关联函数</u>，该如何调用呢

答：还是通过完全限定语法，格式为 `<类型名 as Trait名>::关联函数名(参数)`

```rust
trait Animal {
    fn baby_name() -> String;
}
trait Friend {
    fn baby_name() -> String;
}

//---------------------
struct Dog;

impl Dog {
    fn baby_name() -> String {
        String::from("Spot")
    }
}

impl Animal for Dog {
    fn baby_name() -> String {
        String::from("puppy")
    }
}

impl Friend for Dog {
    fn baby_name() -> String {
        String::from("homie")
    }
}

fn main() {
    println!("{}", Dog::baby_name()); // from impl Dog {..}
    println!("{}", <Dog as Animal>::baby_name()); // from impl Animal for Dog {..}
    println!("{}", <Dog as Friend>::baby_name()); // from impl Friend for Dog {..}
}
```



**要求某个 trait 附带其它 trait 的功能**

相当于 trait 的继承。示例：

```rust
use std::fmt::Display;

//OutlinePrint 继承 Display
//实现 OutlinePrint 的类型也需要实现 Display
trait OutlinePrint: Display {
    fn outline_print(&self) {
        let output = self.to_string();
        let len = output.len();
        println!("{}", "*".repeat(len + 4));
        println!("*{}*", " ".repeat(len + 2));
        println!("* {} *", output);
        println!("*{}*", " ".repeat(len + 2));
        println!("{}", "*".repeat(len + 4));
    }
}

struct Point {
    x: i32,
    y: i32,
}

impl OutlinePrint for Point {}

// Point 需要实现 OutlinePrint 依赖的 Display trait
impl Display for Point {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "({}, {})", self.x, self.y)
    }
}

fn main() {
    let p = Point { x: 1, y: 2 };
    p.outline_print();
}
```



**为外部类型实现外部 trait**

孤儿规则：只有当 trait 或类型在本地定义时，才能为该类型实现这个 trait；无法为外部类型直接实现外部 trait

解决方法：通过 newtype 模式绕过这个规则，具体的做法是利用元组结构体创建一个新的类型

示例：为 Vec 实现 Display trait

```rust
use std::fmt::Display;

struct WrapperType(Vec<String>);

impl Display for WrapperType {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "[{}]", self.0.join(" | "))
    }
}

fn main() {
    let n = WrapperType(vec![
        String::from("tomy"),
        String::from("jetty"),
        String::from("katy"),
    ]);
    println!("{}", n);//[tomy | jetty | katy]
}
```

### 高级类型

**类型别名**

语法：`type 新类型名 = 旧类型名;`

作用：为现有的某个类型创建一个另外的名称，减少重复代码

注意：类型别名不是一个独立的类型，而和旧名相同（在编译期做替换）

示例一：简单示例

```rust
type Kilometers = i32;
fn main() {
    let x: i32 = 5;
    let y: Kilometers = 5;
    assert_eq!(x, y);
}
```

示例二：简化代码

```rust
fn takes_long_type(f: Box<dyn Fn() + Send + 'static>) {}
fn returns_long_type() -> Box<dyn Fn() + Send + 'static> {
    Box::new(|| println!("hi"))
}
fn main() {
    let f: Box<dyn Fn() + Send + 'static> = Box::new(|| println!("hi"));
}

//---------------------------使用类型别名优化
type Thunk = Box<dyn Fn() + Send + 'static>;
fn takes_long_type(f: Thunk) {}
fn returns_long_type() -> Thunk {
    Box::new(|| println!("hi"))
}
fn main() {
    let f: Thunk = Box::new(|| println!("hi"));
}
```

示例三

```rust
use std::io::Error;
pub trait Write {
    fn write(&mut self, buf: &[u8]) -> Result<usize, Error>;
    fn flush(&mut self) -> Result<(), Error>;
    fn write_all(&mut self, buf: &[u8]) -> Result<(), Error>;
    fn write_fmt(&mut self, fmt: core::fmt::Arguments) -> Result<(), Error>;
}

//----------优化
//std::io::Result<T> 的定义：pub type Result<T> = result::Result<T, Error>;
//引入 std::io::Result<T>
type Result<T> = std::io::Result<T>;

pub trait Write {
    fn write(&mut self, buf: &[u8]) -> Result<usize>;
    fn flush(&mut self) -> Result<()>;
    fn write_all(&mut self, buf: &[u8]) -> Result<()>;
    fn write_fmt(&mut self, fmt: core::fmt::Arguments) -> Result<()>;
}
```



**never 类型**

[never - Rust (rust-lang.org)](https://doc.rust-lang.org/std/primitive.never.html)



**动态大小和 Sized trait**

> Rust 编译规则：在编译时需要确定为一个特定类型的值分配多少空间，如果不能确定则编译不通过

动态大小的类型（DST，dynamically sized types）：只有在运行时才能确定占用内存大小的值的类型

比如 `str` 就是 DST（注意不是 `&str`），只有运行时才能确定字符串的长度，在编译期无法确定

```rust
fn main() {
    //error[E0277]: the size for values of type `str` cannot be known at compilation time
    let s1: str = "hi";
}
//解决：指定类型为 &str，因为 &str 变量占用固定大小的空间，包括字符串的地址和字符串长度
```

[Dynamically Sized Types - The Rust Reference (rust-lang.org)](https://doc.rust-lang.org/reference/dynamically-sized-types.html)



为了方便处理 DST，Rust 引入了 Sized trait，用来标记一个类型的大小在编译时是否已知。

注意：

- 在编译时如果可以计算出某个类型的大小，那么 Rust 会为它自动实现这个 trait
- Rust 会为每一个泛型函数隐式的添加这个 trait

示例

```rust
fn generic<T>(t: T) {}

fn generic<T: Sized>(t: T) {}//编译后的结果
```



默认情况下，泛型函数只能被用于编译时已经知道大小的类型，但可以通过特殊的语法解除这个限制

使用 `?` 标记 `Sized` trait：表示 T 可能是确定大小的、也可能不是的。

```rust
fn generic<T: ?Sized>(t: &T) {}
```

注意：

- T 的大小可能不是确定的，所有要把 T 类型值放在指针后边，所以参数为 `&T` 类型
- `?` 只能用在 `Sized` trait 前，不能用于其它地方



本节视频见[这里](https://www.bilibili.com/video/BV1hp4y1k7SV?p=105)

### 高级函数和闭包

**函数指针**

可以把函数传递给其他函数

传递：传递的是函数指针（函数指针的类型是 fn）

```rust
fn add_one(x: i32) -> i32 {
    x + 1
}

// fn(i32) -> i32   表示函数指针类型
fn do_twice(f: fn(i32) -> i32, arg: i32) -> i32 {
    //通过函数指针调用函数
    f(arg) + f(arg)
}

fn main() {
    let fn_ptr: fn(i32) -> i32 = add_one; //函数指针
    println!("do twice: {}", do_twice(fn_ptr, 1));
}
```



fn 是函数指针类型，不是 trait，可以直接指定 fn 为参数类型；

fn 同时实现了 Fn、FnMut 和 FnOnce 三个闭包 trait，所以可以把函数指针作为参数传递给闭包作为参数的函数；

推荐的做法是，搭配闭包 trait 泛型来编写函数，这样可以同时接收闭包和普通函数



示例：map 方法的参数可以是闭包或函数

```rust
fn main() {
    /*
        fn map<B, F>(self, f: F) -> Map<Self, F>
        where
            Self: Sized,
            F: FnMut(Self::Item) -> B,
        {
            Map::new(self, f)
        }
    */
    let list1 = vec![1, 2, 3];
    let list1: Vec<String> = list1.iter().map(|i| i.to_string()).collect();

    let list2 = vec![1, 2, 3];
    let list2: Vec<String> = list2.iter().map(ToString::to_string).collect();

    println!("{:?}, {:?}", list1, list2);
}
```



示例二

```rust
fn main() {
    #[derive(Debug)]
    enum Status {
        Value(u32),
    }

    //把 Status::Value 看成函数指针
    let list: Vec<Status> = (0u32..5).map(Status::Value).collect();

    //[Value(0), Value(1), Value(2), Value(3), Value(4)]
    println!("{:?}", list);
}
```



**返回闭包**

函数无法直接返回一个闭包，但可以把一个实现了 Fn trait 的具体类型作为返回值

```rust
//编译错误：无法确定闭包占用空间的大小
fn returns_closure() -> Fn(i32) -> i32 {
    |x| x + 1
}

//解决
fn returns_closure() -> Box<dyn Fn(i32) -> i32> {
    Box::new(|x| x + 1)
}
```



### 宏

Rust 中宏是指一组相关特性的集合称谓。

几种不同的宏：

- 使用 `macro_rules!` 构建声明宏
- 三种过程宏
  - `#[derive]` 宏，可以为 struct 或 enum 指定随 derive 属性添加的代码
  - 类似于属性的宏，可以在任何条目上添加自定义属性
  - 类似于函数的宏，看起来像函数调用，可以对指定为参数的 token 进行自定义操作



**函数 VS 宏**

- 函数在定义签名时必须声明参数的个数和类型；宏可以处理可变参数；
- 宏的定义比函数复杂的多，往往难以阅读、理解和维护；
- 函数可以在任何位置定义并在任何位置使用；在调用宏之前，必须提前定义并将宏引入当前作用域；



**声明宏**

是 Rust 中最常见的宏；类似于 match 模式匹配；需要使用 `marco_rules!`

源码：`vec` 宏

```rust
#[cfg(all(not(no_global_oom_handling), not(test)))]
#[macro_export]
#[stable(feature = "rust1", since = "1.0.0")]
#[rustc_diagnostic_item = "vec_macro"]
#[allow_internal_unstable(rustc_attrs, liballoc_internals)]
macro_rules! vec {
    () => (
        $crate::__rust_force_expr!($crate::vec::Vec::new())
    );
    ($elem:expr; $n:expr) => (
        $crate::__rust_force_expr!($crate::vec::from_elem($elem, $n))
    );
    ($($x:expr),+ $(,)?) => (
        $crate::__rust_force_expr!(<[_]>::into_vec(
            #[rustc_box]
            $crate::boxed::Box::new([$($x),+])
        ))
    );
}
```



**过程宏**

三种过程宏：

- 派生宏（只能用于 struct 和 enum）
- 属性宏（可以用于任何条目）
- 函数宏



创建过程宏：必须将宏单独放在它们自己包中，并使用特殊的包类型



**自定义派生宏**

需求：

1. 创建 `hello_macro` 包，其中定义一个拥有关联函数 `hello_macro` 的 `HelloMacro` trait
2. 提供一个能自动实现 trait 的过程宏
3. 在自定义的类型上标注 `#[derive(HelloMacro)]` 以获得 `hello_macro` 的默认实现



**属性宏**示例

```rust
#[route(GET, "/")] //使用属性宏
fn index() {
    //..
}

//----------------宏定义函数
#[proc_macro_attribute]
pub fn route(attr: TokenStream, item: TokenStream) -> TokenStream {
    //..
}
```



**函数宏**示例：解析 sql 语句的宏

```rust
let sql = sql(SELECT * FROM posts WHERE id=1);


//------------------------宏定义函数
pub fn sql(input: TokenStream) -> TokenStream {
    //..
}
```



本节视频见[这里](https://www.bilibili.com/video/BV1hp4y1k7SV?p=106)

## 实战 web 服务器

目标：回顾之前学过的知识，构建多线程 Web 服务器（回顾为主，而不是最佳实践）

主要内容：

- 在 socket 上监听 TCP 连接
- 解析少量的 HTTP 请求
- 创建一个合适的 HTTP 响应
- 定义线程池并使用它改进服务器端吞吐量



`2024.4.18` 提交

```
9.8收尾工作-join前给worker线程发送终止消息
9.7收尾工作-等待所有子线程结束
9.6解决9.5中的问题
9.5编写execute及下游worker创建逻辑
9.4解决9.3中receiver无法被直接传递给多个线程的问题
9.3使用通道将线程池和各个线程联系起来
9.2定义ThreadPool结构体
9.1搭建线程池壳子
8为每个请求创建一个线程
7单线程存在的问题
6改进上一次提交的代码
5添加404功能
4写入html内容
3写入响应
2读取请求
1建立连接
web服务器-项目准备
```



### 其他

[教程链接](https://www.bilibili.com/video/BV1hp4y1k7SV)













