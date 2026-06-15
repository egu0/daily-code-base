use minigrep;
use minigrep::InputParam;
use std::env;
use std::process;

fn main() {
    // r.unwrap_or_else(c): 如果 r 是 Ok 那么正常解包；否则会走传递的闭包 c 的逻辑
    let params = InputParam::parse_params(env::args()).unwrap_or_else(|err| {
        eprintln!("error when parsing arguments: {}", err);
        process::exit(1);
    });

    if let Err(e) = minigrep::run(params) {
        eprintln!("Application error: {}", e);
        process::exit(1);
    }
}
