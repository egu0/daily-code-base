fn main() {
    println!("{}", value_in_coin(Coin::Nickel));
    println!("{}", value_in_coin(Coin::Quarter(String::from("alaska"))));

    println!("{:?}", plus_one(Some(1)));
    println!("{:?}", plus_one(None));

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

    //--------------
    //if let

    let v = Some(4u8);
    /*
        match v {
            Some(3) => println!("three"),
            _ => (),
        }
    */
    if let Some(3) = v {
        println!("three")
    }
    if let Some(3) = v {
        println!("three");
    } else if let Some(4) = v {
        println!("four");
    } else {
        println!("other");
    }
}

fn plus_one(num: Option<i32>) -> Option<i32> {
    num.map(|n| n + 1)
    /*
        match num {
            Some(n) => Some(n + 1),
            None => None,
        }
    */
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
