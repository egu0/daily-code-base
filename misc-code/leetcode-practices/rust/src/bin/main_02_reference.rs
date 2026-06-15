/*
引用是一个指针，指向拥有数据所有权的变量，所以引用没有数据的所有权

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
|   ptr    |  0x2024  |-----    注：0x2024 是变量 s 的栈内存地址
|----------|----------|

--------------------------------------------------------------------
*/
fn main() {
    //引用和借用
    test1();
    test2();
    test3();
}

fn test1() {
    let s = String::from("value");
    //所有权转移到了函数内部，之后 s 就不可用了
    fn1(s);
    //error[E0382]: borrow of moved value: `s`
    // println!("{}", s);
}

fn test2() {
    let mut s = String::from("value");
    //借用：把引用作为函数参数传递给函数，不会转移所有权。默认为不可变借用
    fn2(&s); //不可变借用
    fn2(&s);
    fn3(&mut s); //可变借用
    fn3(&mut s);
    s.push('c');
    println!("{}", s);
}

fn test3() {
    let mut s = String::from("value");
    let _f1 = &s;
    let _f2 = &s;
    fn2(&s);
    fn3(&mut s);
    let _f3 = &mut s;
    let _f4 = &mut s;
    println!("{}", s);
}

/*
fn dangle(s: &String) -> &String {
    let s = String::from("value");
    // 悬空引用：一个指针指向内存中某个地址，而这个数据在离开作用域时被销毁了
    // cannot return reference to local variable `s`
    &s
}
*/

#[allow(clippy::ptr_arg)]
fn fn1(s: String) {
    print!("{}", s.len());
}

#[allow(clippy::ptr_arg)]
fn fn2(s: &String) {
    print!("{}", s.len());
}

#[allow(clippy::ptr_arg)]
fn fn3(s: &mut String) {
    print!("{}", s.len());
}
