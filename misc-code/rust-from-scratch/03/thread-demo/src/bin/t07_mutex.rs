use std::sync::Mutex;

fn main() {
    let m = Mutex::new(5);
    {
        let mut num: std::sync::MutexGuard<'_, i32> = m.lock().unwrap();
        //num是一个智能指针，所有可以通过解引用符修改其中的数据
        *num = 6;
    }
    println!("m = {:?}", m);
}
