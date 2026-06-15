fn main() {
    //字符串切片，也叫字符串字面值
    //在编译时就知道了它的内容了，文本内容直接被硬编码到最终的可执行文件里
    //优点是速度快高效，缺点是不可变
    let t: &str = "jelly";
    fn1(t);
    println!("t:{}", t);

    //String 类型，在 heap 上分配内存保存文本内容
    //操作系统在运行时请求内存来存储文本
    //使用完后会调用 drop 函数释放内存
    let s: String = String::from("jewelry");
    fn1(s.as_str());
    fn1(&s[..]);
}

fn fn1(s: &str) {
    _ = s.len();
}
