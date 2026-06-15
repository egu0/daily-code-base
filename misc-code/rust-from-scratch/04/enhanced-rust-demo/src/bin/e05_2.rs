use std::ops::Add;

#[derive(Debug)]
struct Point {
    x: i32,
    y: i32,
}

// impl Add<Self> for Point {
// impl Add<Point> for Point {
impl Add for Point {
    type Output = Point;

    fn add(self, rhs: Self) -> Self::Output {
        Point {
            x: self.x + rhs.x,
            y: self.y + rhs.y,
        }
    }
}

fn main() {
    let p3 = Point { x: 1, y: 1 } + Point { x: 2, y: 2 };
    println!("{:?}", p3);

    let m = Millimeters(8) + Meters(1);
    println!("millimeters:{:?}", m);
}

#[derive(Debug)]
struct Millimeters(u32);
struct Meters(u32);

impl Add<Meters> for Millimeters {
    type Output = Millimeters;

    fn add(self, rhs: Meters) -> Self::Output {
        Millimeters(self.0 + (rhs.0 * 1000))
    }
}
