package me.jugg.parser;

import me.jugg.ast.Expression;
import me.jugg.ast.InfixExpression;
import me.jugg.ast.IntegerExpression;
import me.jugg.ast.PrefixExpression;
import me.jugg.lexer.Lexer;
import me.jugg.token.Token;
import me.jugg.token.TokenType;

import java.math.BigInteger;
import java.util.HashMap;

public class Parser {

    final Lexer lexer;

    Token cur, peek;

    HashMap<TokenType, InfixParseFn> infixParseFnHashMap = new HashMap<>();

    HashMap<TokenType, PrefixParseFn> prefixParseFnHashMap = new HashMap<>();

    public Parser(Lexer lexer) {
        this.lexer = lexer;

        // 前缀表达式解析器。不同的前缀（负号、数字和左括号）对应的解析方法不同
        prefixParseFnHashMap.put(TokenType.NUM, this::parseInteger);
        prefixParseFnHashMap.put(TokenType.MINUS, this::parsePrefixExpression);
        prefixParseFnHashMap.put(TokenType.LPAREN, this::parseGroupExpression);

        // 中缀表达式解析器。解析方法是同一个
        infixParseFnHashMap.put(TokenType.PLUS, this::parseInfixExpression);// 加
        infixParseFnHashMap.put(TokenType.MINUS, this::parseInfixExpression);// 减
        infixParseFnHashMap.put(TokenType.ASTERISK, this::parseInfixExpression);// 乘
        infixParseFnHashMap.put(TokenType.SLASH, this::parseInfixExpression);// 除
        infixParseFnHashMap.put(TokenType.HAT, this::parseInfixExpression);// 幂

        // 读取前两个 token 到 cur、peek 上
        nextToken();
        nextToken();
    }

    void nextToken() {
        cur = peek;
        peek = lexer.nextToken();
    }

    @SuppressWarnings("unused")
    private boolean curTokenIs(TokenType type) {
        return cur.type == type;
    }

    private boolean peekTokenIs(TokenType type) {
        return peek.type == type;
    }

    public Expression parseMain() {
        return parseExpression(Precedence.LOWEST);
    }

    private int peekPrecedence() {
        return Precedence.getPrecedence(peek.type);
    }

    /**
     * 核心方法
     */
    public Expression parseExpression(int precedence) {

        System.out.println("cur token type: " + cur.type + ", value: " + cur.value);

        // 解析前缀表达式，cur 可能是数字、负号、左括号
        PrefixParseFn prefixParseFn = prefixParseFnHashMap.get(cur.type);
        if (prefixParseFn == null) {
            throw new RuntimeException("No prefix parse function for " + cur.type);
        }
        Expression left = prefixParseFn.parse();

        // 解析优先级高于 precedence 的中缀表达式
        while (!peekTokenIs(TokenType.EOF) && precedence < peekPrecedence()) {
            InfixParseFn infixParseFn = infixParseFnHashMap.get(peek.type);
            if (infixParseFn == null) {
                break;
            }

            nextToken();

            // infixParseFn.parse(left) 作用：将上边解析到的前缀表达式 left 作为中缀表达式的左子树
            left = infixParseFn.parse(left);// cur 是 '+ - * / ^' 的一种
        }

        return left;
    }

    // 解析中缀表达式（通用方法）
    Expression parseInfixExpression(Expression left) {
        InfixExpression infixExpression = new InfixExpression();

        infixExpression.left = left;

        infixExpression.operator = cur.value;

        int precedence = Precedence.getPrecedence(cur.type);
        nextToken();
        infixExpression.right = parseExpression(precedence);

        return infixExpression;
    }

    // 解析前缀表达式，整个前缀可能的情况有：-1, --1, ---1, -(xxx), -fn(xx)
    Expression parsePrefixExpression() {
        PrefixExpression prefixExpression = new PrefixExpression();

        // 设置前缀
        prefixExpression.operator = cur.value;

        // 解析表达式，表达式中的操作符优先级必须高于 PREFIX，比如函数调用
        nextToken();
        prefixExpression.right = parseExpression(Precedence.PREFIX);

        return prefixExpression;
    }

    // 解析数字
    public Expression parseInteger() {// TokenType.NUM
        BigInteger val = new BigInteger(cur.value);

        return new IntegerExpression(val);
    }

    // 解析括号中的内容
    Expression parseGroupExpression() {
        nextToken();
        Expression exp = parseExpression(Precedence.LOWEST);
        nextToken();
        return exp;
    }

}
