use std::{
    fs,
    io::{Read, Write},
    net::{TcpListener, TcpStream},
    thread,
    time::Duration,
};

use web_server::ThreadPool;

fn main() {
    let pool = ThreadPool::new(4);

    let listener: TcpListener = TcpListener::bind("127.0.0.1:7878").unwrap();
    // .take(2)  :   接收两个请求后自动结束监听（模拟停止服务器）
    for stream in listener.incoming().take(2) {
        let stream = stream.unwrap();

        //解决方法二：线程池
        pool.execute(|| {
            handle_connection(stream);
        })
    }
}

fn handle_connection(mut stream: TcpStream) {
    let mut buffer = [0; 512];

    stream.read(&mut buffer).unwrap();

    //请求
    // Method Request-URI HTTP-Version CRLF
    // Headers CRLF
    // Message-Body

    // println!(
    //     "------------------Request-----------------\n{:?}",
    //     String::from_utf8_lossy(&buffer[..])
    // );

    //响应
    // HTTP-Version Status-Code Reason-Phrase CRLF
    // Headers CRLF
    // Message-Body

    let req_line_of_index = b"GET / HTTP/";
    let req_line_of_sleep = b"GET /sleep HTTP/";

    let (status_line, filename) = if buffer.starts_with(req_line_of_index) {
        ("HTTP/1.1 200 OK\r\n\r\n", "hello.html")
    } else if buffer.starts_with(req_line_of_sleep) {
        //处理 [get /sleep] 请求，模拟耗时业务
        //单线程 web 服务器存在的问题：
        // 1. 同一时刻只能处理一个请求，后者需要等前者处理完才能处理
        // 2. 效率低下，不能发挥多核 cpu 优势
        thread::sleep(Duration::from_secs(5));
        ("HTTP/1.1 200 OK\r\n\r\n", "hello.html")
    } else {
        ("HTTP/1.1 404 NOT FOUND\r\n\r\n", "404.html")
    };

    let content = fs::read_to_string(filename).unwrap();
    let response = format!("{}{}", status_line, content);

    stream.write(response.as_bytes()).unwrap();
    stream.flush().unwrap();
    // println!(
    //     "------------------Response-----------------\n{:?}\n\n",
    //     String::from_utf8_lossy(response.as_bytes())
    // );
}
