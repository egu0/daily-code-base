use std::env::Args;
use std::error::Error;
use std::{env, fs};

pub fn run(params: InputParam) -> Result<(), Box<dyn Error>> {
    let content = fs::read_to_string(params.filepath)?;

    let result_lines = if params.case_sensitive {
        search(&params.keyword, &content)
    } else {
        search_case_insensitive(&params.keyword, &content)
    };

    for line in result_lines {
        println!("{}", line);
    }

    Ok(())
}

pub struct InputParam {
    pub keyword: String,
    pub filepath: String,
    pub case_sensitive: bool,
}

impl InputParam {
    pub fn parse_params(args: Args) -> Result<InputParam, &'static str> {
        if args.len() < 3 {
            return Err("no enough arguments!");
        }

        let mut arg_iter = args.into_iter();
        arg_iter.next();

        let keyword = match arg_iter.next() {
            Some(e) => e,
            None => return Err("Don't obtain keyword parameter"),
        };
        let filepath = match arg_iter.next() {
            Some(e) => e,
            None => return Err("Don't obtain filepath parameter"),
        };

        //查看环境变量 CASE_INSENSITIVE 是否存在，进而得到 case_sentitive 变量
        let case_sensitive = env::var("CASE_INSENSITIVE").is_err();

        Ok(InputParam {
            keyword,
            filepath,
            case_sensitive,
        })
    }
}

fn search<'a>(keyword: &str, content: &'a str) -> Vec<&'a str> {
    // let mut result = Vec::new();

    // for line in content.lines() {
    //     if line.contains(keyword) {
    //         result.push(line);
    //     }
    // }

    // result

    content
        .lines()
        .filter(|line| line.contains(keyword))
        .collect()
}

fn search_case_insensitive<'a>(keyword: &str, content: &'a str) -> Vec<&'a str> {
    let keyword = keyword.to_lowercase();
    let mut result = Vec::new();

    for line in content.lines() {
        if line.to_lowercase().contains(&keyword) {
            result.push(line);
        }
    }

    result
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn case_sensitive() {
        let keyword = "duct";
        let content = "\n
Rust:
safe, fast, productive.
provide you another experience.
Duct tape.";

        assert_eq!(vec!["safe, fast, productive."], search(keyword, content));
    }

    #[test]
    fn case_insensitive() {
        let keyword = "rUsT";
        let content = "\n
Rust:
safe, fast, productive.
provide you another experience.
Trust me.";

        assert_eq!(
            vec!["Rust:", "Trust me."],
            search_case_insensitive(keyword, content)
        );
    }
}
