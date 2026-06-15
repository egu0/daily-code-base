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
