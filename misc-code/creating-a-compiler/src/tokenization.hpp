#pragma once

#include <string>
#include <vector>

enum class TokenType {
    exit, // exit 关键字
    int_lit, // 数字字面量
    semi, // ;
    open_paren, // (
    close_paren, // )
    ident, // 变量标识
    let, // let 关键字
    eq, // =
    plus, // +
    star, // *
    minus, // -
    fslash, // /
    open_curly, // {
    close_curly, // }
    if_, // if
    elif, // elif
    else_, // else
};

std::optional<int> bin_prec(TokenType type)
{
    switch (type) {
    case TokenType::minus:
    case TokenType::plus:
        return 0;
    case TokenType::fslash:
    case TokenType::star:
        return 1;
    default:
        return {};
    }
}

struct Token {
    TokenType type;
    std::optional<std::string> value{};
};

class Tokenizer {
public:
    inline Tokenizer(std::string src)
        : m_src(std::move(src))
    {
    }

    /**
     * 词法分析。将代码字符串解析为 vector<Token>
    */
    inline std::vector<Token> tokenize()
    {
        std::vector<Token> tokens;
        std::string buf;
        while (peek().has_value()) {
            // alpha -> read token
            if (std::isalpha(peek().value())) {
                buf.push_back(consume());
                while (peek().has_value() && std::isalnum(peek().value())) {
                    buf.push_back(consume());
                }

                if (buf == "exit") {
                    tokens.push_back({ .type = TokenType::exit });
                    buf.clear();
                    continue;
                }
                else if (buf == "let") {
                    tokens.push_back({ .type = TokenType::let });
                    buf.clear();
                    continue;
                }
                else if (buf == "if") {
                    tokens.push_back({ .type = TokenType::if_ });
                    buf.clear();
                    continue;
                }
                else if (buf == "elif") {
                    tokens.push_back({ .type = TokenType::elif });
                    buf.clear();
                }
                else if (buf == "else") {
                    tokens.push_back({ .type = TokenType::else_ });
                    buf.clear();
                }
                else {
                    tokens.push_back({ .type = TokenType::ident, .value = buf });
                    buf.clear();
                    continue;
                }
            }
            else if (std::isdigit(peek().value())) {
                // digit -> read int literal
                buf.push_back(consume());
                while (peek().has_value() && std::isdigit(peek().value())) {
                    buf.push_back(consume());
                }

                tokens.push_back({ .type = TokenType::int_lit, .value = buf });
                buf.clear();
                continue;
            }
            else if (peek().value() == '(') {
                consume();
                tokens.push_back({ .type = TokenType::open_paren });
                continue;
            }
            else if (peek().value() == ')') {
                consume();
                tokens.push_back({ .type = TokenType::close_paren });
                continue;
            }
            else if (peek().value() == ';') {
                consume();
                tokens.push_back({ .type = TokenType::semi });
                continue;
            }
            else if (peek().value() == '=') {
                consume();
                tokens.push_back({ .type = TokenType::eq });
                continue;
            }
            else if (peek().value() == '+') {
                consume();
                tokens.push_back({ .type = TokenType::plus });
                continue;
            }
            else if (peek().value() == '-') {
                consume();
                tokens.push_back({ .type = TokenType::minus });
                continue;
            }
            else if (peek().value() == '*') {
                consume();
                tokens.push_back({ .type = TokenType::star });
                continue;
            }
            else if (peek().value() == '/' && peek(1).has_value() && peek(1).value() == '/') {
                consume();
                consume();
                while (peek().has_value() && peek().value() != '\n') {
                    consume();
                }
            }
            else if (peek().value() == '/' && peek(1).has_value() && peek(1).value() == '*') {
                consume();
                consume();
                while (peek().has_value()) {
                    if (peek().value() == '*' && peek(1).has_value() && peek(1).value() == '/') {
                        break;
                    }
                    consume();
                }
                // 允许未闭合的多行注释，但它必须放在文件最后
                if (peek().has_value()) {
                    consume();
                }
                if (peek().has_value()) {
                    consume();
                }
            }
            else if (peek().value() == '/') {
                consume();
                tokens.push_back({ .type = TokenType::fslash });
                continue;
            }
            else if (peek().value() == '{') {
                consume();
                tokens.push_back({ .type = TokenType::open_curly });
                continue;
            }
            else if (peek().value() == '}') {
                consume();
                tokens.push_back({ .type = TokenType::close_curly });
                continue;
            }
            else if (std::isspace(peek().value())) {
                consume();
                continue;
            }
            else {
                std::cerr << "You messed up!" << std::endl;
                exit(EXIT_FAILURE);
            }
        }
        m_index = 0;
        return tokens;
    }

private:

    inline std::optional<char> peek(std::size_t offset = 0) const
    {
        if (m_index + offset >= m_src.length()) {
            return {};
        }
        else {
            return m_src.at(m_index + offset);
        }
    }

    inline char consume()
    {
        return m_src.at(m_index++);
    }

    const std::string m_src;
    std::size_t m_index = 0;
};