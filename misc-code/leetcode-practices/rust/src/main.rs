use rust_practices::hosting;

fn main() {
    // rust_pratices::fn1();
    hosting::add_to_waitlist();

    println!("hi!");
}

//------- as_mut 和 as_ref --------------

#[allow(dead_code)]
fn fn1() {
    let mut x: Option<Box<i32>> = Some(Box::new(19));

    // mut Option<T> --> Option<&mut T>
    let _q: Option<&mut Box<i32>> = x.as_mut();

    // mut Option<T> --> Option<&T>
    let _r: Option<&Box<i32>> = x.as_ref();

    //---------------------------------------------

    let mut b: Box<i32> = Box::new(10);

    // mut Box<T> -> &mut T
    let _o: &mut i32 = b.as_mut();

    // mut Box<T> --> &T
    let _r = b.as_ref();

    //---------------------------------------------

    let mut s = String::from("holy shittt");

    // mut Strnig --> &mut str
    let _m: &mut str = s.as_mut();

    // mut String --> &str
    let _s: &str = s.as_ref();
    let _s: &str = s.as_str();
}

#[allow(dead_code)]
fn fn2() {
    // as_mut 和 as_ref 分别用来获取可变和不可变引用，返回值类型和调用方一样

    let mut b = Box::new(String::from("a"));
    // as_mut 获取可变引用
    b.as_mut().push('b');
    b.as_mut().push('b');
    println!("{}", b);
    // as_ref 获取不可变引用
    let _x: &String = b.as_ref();

    let mut o: Option<Box<String>> = Some(Box::new(String::from("1")));
    // as_mut 获取可变引用
    o.as_mut().unwrap().push('2');
    // as_ref 获取不可变引用
    let _x: Option<&Box<String>> = o.as_ref();
    println!("{}", o.unwrap());
}
