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

    //完全限定语法
    Pilot::fly(&h);
    Wizard::fly(&h);
    Human::fly(&h); //等同于 h.fly()
}
