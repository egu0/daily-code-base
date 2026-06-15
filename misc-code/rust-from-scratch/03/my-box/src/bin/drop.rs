struct MySmartPointer {
    value: String,
}

impl Drop for MySmartPointer {
    fn drop(&mut self) {
        println!(
            "Dropping MySmartPointer instance, value is {:?}",
            self.value
        );
    }
}

fn main() {
    let i1 = MySmartPointer {
        value: "jacy".to_string(),
    };
    std::mem::drop(i1);
    let _i2 = MySmartPointer {
        value: "totis".to_string(),
    };
    println!("Creating Finished.")
}
