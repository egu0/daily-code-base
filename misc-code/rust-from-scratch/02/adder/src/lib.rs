pub fn add_two(val: i32) -> i32 {
    val + 2
}

#[allow(dead_code)]
fn plus_one(val: i32) -> i32 {
    val + 1
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    pub fn it_plus_one() {
        assert!(2 == plus_one(1));
    }

    #[test]
    pub fn it_add_two_works() {
        assert!(3 == add_two(1)); //3 = f(1)
        assert_eq!(3, add_two(1)); //3 = f(1)
        assert_ne!(4, add_two(1)); //4 != f(1)
    }

    #[test]
    #[should_panic]
    pub fn panic_01() {
        panic!("ppp");
    }

    #[test]
    #[should_panic(expected = "network error")]
    pub fn panic_02() {
        let is_network_error = true;
        if is_network_error {
            panic!("network error, please check your internet connection.");
        } else {
            panic!("server error, please contact system administrator.");
        }
    }

    #[test]
    pub fn panic_03() -> Result<(), String> {
        let flag = true;
        if flag {
            Ok(())
        } else {
            Err("error happened.".to_string())
        }
    }
}
