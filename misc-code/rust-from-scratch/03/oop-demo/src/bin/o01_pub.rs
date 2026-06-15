use oop_demo::可见性::*;

fn main() {
    //可见性

    let mut c = ArtCollection::new();
    c.add_one(String::from("kiky"));
    c.set_owner(String::from("jiji"));

    let m = c.all_mut();
    m.push(String::from("tity"));

    println!("owner is {}", c.owner());
    for item in c.all().iter() {
        println!("Got: {}", item);
    }

    println!("col: {:?}", c);
}
