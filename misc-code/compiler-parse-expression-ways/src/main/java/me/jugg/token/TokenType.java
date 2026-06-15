package me.jugg.token;

import java.util.HashMap;
import java.util.Map;

public enum TokenType {

    NUM("NUM"),
    LPAREN("("),
    RPAREN(")"),

    MINUS("-"),
    PLUS("+"),

    ASTERISK("*"),
    HAT("^"),

    SLASH("/"),

    EOF("EOF");

    static Map<String, TokenType> map = new HashMap<>();

    static {
        map.put("(", LPAREN);
        map.put(")", RPAREN);
        map.put("-", MINUS);
        map.put("+", PLUS);
        map.put("*", ASTERISK);
        map.put("/", SLASH);
    }

    @SuppressWarnings("unused")
    private String name;

    TokenType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TokenType(" + this.name() + ")";
    }
}
