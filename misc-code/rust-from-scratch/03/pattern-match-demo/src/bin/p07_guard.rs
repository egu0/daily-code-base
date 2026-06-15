fn main() {
    let num = Some(4);
    match num {
        Some(x) if x < 5 => println!("less than five, actual value is {}", x),
        Some(x) => println!("x is {}", x),
        None => (),
    }

    //---------------------------
    let x = Some(5);
    let y = 15;
    match x {
        Some(50) => println!("Got 50!"),
        Some(n) if n == y => println!("matched, n = {}", n),
        _ => println!("defalut arm, x is {:?}", x),
    }
    println!("finally, x is {:?}, y is {:?}", x, y);

    //---------------------------
    let x = 4;
    let y = false;
    match x {
        4 | 5 | 6 if y => println!("yes"),
        _ => println!("no"),
    }
}
