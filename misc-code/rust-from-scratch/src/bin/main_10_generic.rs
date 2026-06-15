fn main() {
    largest(&[1, 3, 2]);

    let p1: Point<char, i32> = Point { x: 'a', y: 1 };
    let p2: Point<&str, bool> = Point { x: "x", y: true };
    let p3: Point<&str, i32> = p1.mixup(p2);
    println!("p3:{:?}", p3);
}

#[derive(Debug)]
struct Point<T, U> {
    x: T,
    y: U,
}

impl<T, U> Point<T, U> {
    fn mixup<V, W>(self, other: Point<V, W>) -> Point<V, U> {
        Point {
            x: other.x,
            y: self.y,
        }
    }
}

/*
struct Point<T> {
    x: T,
    y: T,
}

impl<T> Point<T> {
    fn x(&self) -> &T {
        &self.x
    }
}
*/

#[allow(dead_code)]
fn largest<T: PartialOrd + Copy>(list: &[T]) -> T {
    if list.len() == 0 {
        panic!("no element in list.");
    } else {
        let mut largest = list[0];
        for &item in list {
            if item > largest {
                largest = item;
            }
        }
        return largest;
    }
}

#[allow(dead_code)]
fn largest2<T: PartialOrd + Clone>(list: &[T]) -> &T {
    if list.len() == 0 {
        panic!("no element in list.");
    } else {
        let mut largest = &list[0];
        for item in list {
            if item > largest {
                largest = item;
            }
        }
        return largest;
    }
}

#[allow(dead_code)]
fn largest_i32(list: &[i32]) -> i32 {
    if list.len() == 0 {
        panic!("no element in list.");
    } else {
        let mut largest = list[0];
        for &item in list {
            if item > largest {
                largest = item;
            }
        }
        return largest;
    }
}
