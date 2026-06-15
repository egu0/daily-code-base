package me.jugg.lexer;

import me.jugg.token.Token;
import me.jugg.token.TokenType;

public class Lexer {

    final String input;

    char ch;
    int curPos, peekPos;

    public Lexer(String input) {
        this.input = input;
        curPos = 0;
        peekPos = 0;
        readChar();
    }

    public char peekChar() {
        return input.charAt(peekPos);
    }

    /**
     * 读取下一个 token
     *
     * @return 读取的 token，可能是操作符或数字字符串
     */
    public Token nextToken() {
        Token token;
        skipWhitespace();
        switch (ch) {
            case '+': {
                token = new Token(TokenType.PLUS, "+");
                break;
            }
            case '-': {
                token = new Token(TokenType.MINUS, "-");
                break;
            }
            case '*': {
                token = new Token(TokenType.ASTERISK, "*");
                break;
            }
            case '/': {
                token = new Token(TokenType.SLASH, "/");
                break;
            }
            case '(': {
                token = new Token(TokenType.LPAREN, "(");
                break;
            }
            case ')': {
                token = new Token(TokenType.RPAREN, ")");
                break;
            }
            case '^': {
                token = new Token(TokenType.HAT, "^");
                break;
            }
            case 0: {
                token = new Token(TokenType.EOF, "");
                break;
            }
            default: {
                if (isDigit(ch)) {
                    String num = readNum();
                    token = new Token(TokenType.NUM, num);
                    return token;
                } else {
                    throw new RuntimeException("Lexer error");
                }
            }
        }
        readChar();
        return token;
    }

    /**
     * 从当前位置（{@code this.curPos} ）往后读取数字
     *
     * @return 读取的数字
     */
    String readNum() {
        StringBuilder num = new StringBuilder();
        while (isDigit(ch)) {
            num.append(ch);
            readChar();
        }
        return num.toString();
    }

    boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * 跳过空白区域（空格、制表符、换行）
     */
    void skipWhitespace() {
        while (hasNext()) {
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                readChar();
            } else
                break;
        }
    }

    public boolean hasNext() {
        return peekPos <= input.length();
    }

    /**
     * 向前读一个字符
     */
    void readChar() {
        if (peekPos >= input.length()) {
            ch = 0;
        } else {
            ch = input.charAt(peekPos);
        }
        curPos = peekPos;
        peekPos += 1;
    }

}
