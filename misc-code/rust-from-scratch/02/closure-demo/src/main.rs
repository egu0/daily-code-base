use std::{thread, time::Duration};

fn main() {
    let x = vec![1, 2, 3];

    let equal_to_x = move |z| z == x;

    assert!(equal_to_x(vec![1, 2]));

    // println!("{:?}", x);

    generate_workout(10, 4);
    // generate_workout(30, 4);
    // generate_workout(30, 3);
}

struct Cacher<T>
where
    T: Fn(u32) -> u32,
{
    calculation: T,
    value: Option<u32>,
}

impl<T> Cacher<T>
where
    T: Fn(u32) -> u32,
{
    fn new(calculation: T) -> Self {
        Cacher {
            calculation: calculation,
            value: None,
        }
    }

    fn value(&mut self, arg: u32) -> u32 {
        match self.value {
            Some(val) => val,
            None => {
                let result = (self.calculation)(arg);
                self.value = Some(result);
                result
            }
        }
    }
}

//模拟耗时的业务
#[allow(dead_code)]
fn simulated_expensive_calculation(indensity: u32) -> u32 {
    println!("calculating solwly...");
    thread::sleep(Duration::from_secs(2));
    indensity
}

fn generate_workout(indensity: u32, random_number: u32) {
    let mut cacher = Cacher::new(|num| {
        println!("calculating solwly...");
        thread::sleep(Duration::from_secs(2));
        num
    });

    if indensity < 25 {
        println!("Today, do {} pushups!", cacher.value(indensity));
        println!("Next, do {} situps!", cacher.value(indensity));
    } else {
        if random_number == 3 {
            println!("Take a break today! Remember to stay hydrated!");
        } else {
            println!("Today, run for {} minutes!", cacher.value(indensity));
        }
    }
}
