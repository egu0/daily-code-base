// mod front_of_house {
//     pub mod hosting {
//         pub fn add_to_waitlist() {}
//     }
// }

//---------------------
mod front_of_house;

//导入，并将其视为 public 的
pub use front_of_house::hosting;
// pub use crate::front_of_house::hosting;

pub fn eat_at_restaurant() {
    hosting::add_to_waitlist();
    hosting::add_to_waitlist();
    hosting::add_to_waitlist();
}

/*
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
*/

/*
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
}*/

/*
// 结构：隐式 crate >> mod_a >> mode_ax >> fnx
mod mod_a {
    pub mod mod_ax {
        pub fn fnx() {
            println!("X");
        }
    }
}

pub fn fn1() {
    //绝对路径
    crate::mod_a::mod_ax::fnx();

    //相对路径
    mod_a::mod_ax::fnx();
}
*/
