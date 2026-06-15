/*
stack vs heap
1.二者都是可用的内存，但他俩的结构不同
2.访问数据方面，stack 快于 heap

stack:
1.按值的接收顺序来存储，先进后出
2.所有存储在 stack 上的数据必须拥有已知的固定大小
3.指针的大小是固定的，所以把指针存放在 stack 上
4.把数据存在 stack 上要比存在 heap 上快得多，因为对于 stack 来说永远放在栈顶

所有权解决的问题：
1.跟踪代码的哪些部分正在使用 heap 的哪些数据
2.最小化 heap 上的重复数据
3.清理 heap 上未使用的数据以避免空间不足

所有权规则
1.每个值都有一个变量，这个变量就是该值的所有者
2.每个值同时只能有一个所有者
3.当所有者超出作用域（scope）时，该值将被删除

main_04_type.rs 中介绍的标量类型数据和复合类型数据都存在 stack 中
String 变量的数据存在 heap 中

         STACK                              HEAP
--------------------------------------------------------------------

                                       offset    char     addr
                                     |--------|--------|--------|
|----------|----------|              |   0    |    j   | 0x1988 |
|   name   |     s    |              |   1    |    e   |        |
|   ptr    |  0x1988  |  ------->    |   2    |    l   |        |
|   len    |     5    |              |   3    |    l   |        |
| capacity |     5    |              |   4    |    y   |        |
|----------|----------|              |--------|--------|--------|

--------------------------------------------------------------------

同一时刻 heap 中的数据只能对应 stack 中一个变量，存在所有权问题，解决方法可以是 clone，即将 heap 中的数据再复制一份

stack 上数据复制时无需 clone，而是使用 copy:
1.如果一个类型实现了 Copy，那么旧的变量在赋值后任然可用
2.如果一个类型或该类型的一部分实现了 Drop，那么 Rust 不允许他再实现 Copy
3.实现了 Copy 的类型：
 1.常见的比如：整形/布尔/字符/浮点/Tuple(要求所有字段都是 Copy 类型)
 2.任何简单标量的组合类型都可以是实现 Copy
 3.任何需要分配内存或某种资源的都不是

*/
fn main() {
    //String 数据保存在 heap 上，有所有权转移问题，解决方法是 clone
    fn1();
    fn3();

    //整型数据实现了 Copy，可被 copy，赋值后旧变量仍旧可用（无所有权问题）
    fn4();

    // 如果一个组合类型中所有类型都实现了 Copy，那么这个组合类型可被 Copy
    // 如果一个组合类型（元组或数组）中包含实现了 Drop 的类型，比如 String，那么这个组合类型不可被 copy
    fn5();
    fn6();
}

fn fn6() {
    let t1 = (1, "holy", true);
    let t2 = t1;
    println!("{:?}, {:?}", t1, t2);

    let t3 = (String::from("shit"), b't');
    let _t4 = t3; //t3 中包含的 String 类型实现了 Drop，所以 t3 不可被 copy

    //error[E0382]: borrow of moved value: `t3`
    // println!("{:?}, {:?}", t3, t4);
}

fn fn5() {
    let arr1 = [1, 2, 5, 12];
    let arr2 = arr1;
    println!("{:?}, {:?}", arr1, arr2);

    let arr1 = [String::from("jelly"), String::from("strawberry")];
    let _arr2 = arr1; //arr1 不可被 copy

    //error[E0382]: borrow of moved value: `arr1`
    //println!("{:?}, {:?}", arr1, arr2);
}

fn fn4() {
    let x = 28;
    //
    let y = x;
    println!("{}", y);
}

fn fn1() {
    let s = String::from("jelly");
    fn2(s);

    // 所有权转移到了 fn2 函数内部
    // println!("{}", s);
}

fn fn2(_s: String) {}

fn fn3() {
    let s = String::from("jelly");
    //克隆 heap 上数据，新数据和旧数据互不影响
    let c = s.clone();
    fn2(c);
}
