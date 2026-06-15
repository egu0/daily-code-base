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
