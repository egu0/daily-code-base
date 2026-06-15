fn main() {
    let mut ipk = IpAddrKind::Ipv4;
    println!("{:?}", ipk);

    ipk = IpAddrKind::Ipv6;
    println!("{:?}", ipk);

    let ip1 = IpAddr::V4(127, 0, 0, 1);
    let ip2 = IpAddr::V6(String::from("::1"));

    if let IpAddr::V4(d1, d2, d3, d4) = ip1 {
        println!("ipv4: {}.{}.{}.{}", d1, d2, d3, d4);
    }
    if let IpAddr::V6(add) = ip2 {
        println!("ipv4: {}", add);
    }

    test1();
}

fn test1() {
    //自动类型推断
    let _o1 = Some("hello");
    let _o2 = Some([1, 2, 3]);

    //指定类型
    let _o3: Option<i32> = None;
}

#[derive(Debug)]
enum IpAddrKind {
    Ipv4,
    Ipv6,
}

#[derive(Debug)]
enum IpAddr {
    V4(u8, u8, u8, u8),
    V6(String),
}
