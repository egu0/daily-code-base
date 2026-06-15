use profile_demo::mix;
use profile_demo::PrimaryColor;

fn main() {
    let red = PrimaryColor::Red;
    let yellow = PrimaryColor::Yellow;
    mix(red, yellow);
    println!("hi from profile-demo!");
}
