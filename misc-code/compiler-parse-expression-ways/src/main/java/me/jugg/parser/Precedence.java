package me.jugg.parser;

import me.jugg.token.TokenType;

import java.util.HashMap;

/**
 * 优先级关系
 */
public class Precedence {

    public static final int LOWEST = 0;
    public static final int EQUALS = 1;
    public static final int LESS_GREATER = 2;
    public static final int SUM = 3;

    public static final int PRODUCT = 4;

    public static final int POWER = 5;

    public static final int PREFIX = 6;
    public static final int CALL = 7;

    static HashMap<TokenType, Integer> precedences = new HashMap<>();

    static {
        precedences.put(TokenType.PLUS, SUM);// 3
        precedences.put(TokenType.MINUS, SUM);// 3
        precedences.put(TokenType.SLASH, PRODUCT);// 4
        precedences.put(TokenType.ASTERISK, PRODUCT);// 4
        precedences.put(TokenType.HAT, POWER);// 5
        precedences.put(TokenType.LPAREN, CALL);// 7
    }

    /**
     * 获取对应类型的优先级
     *
     * @param t token 的类型
     * @return
     */
    static int getPrecedence(TokenType t) {
        return precedences.getOrDefault(t, LOWEST);
    }

}
