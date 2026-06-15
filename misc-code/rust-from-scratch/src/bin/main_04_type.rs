fn main() {
    //标量类型：整数/浮点/布尔/字符

    let _i: u32 = 76u32;
    let _j: i32 = 100_000_000;
    let _k: i32 = 0o12;
    let _b: u8 = b'f';
    let _c: char = '😄';

    //复合类型：可以将多个值放在一个类型里，Rust提供了两个基础复合类型即元组、数组

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
