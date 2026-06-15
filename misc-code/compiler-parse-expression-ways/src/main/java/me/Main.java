package me;

import me.jugg.ast.Expression;
import me.jugg.evaluator.Evaluator;
import me.jugg.lexer.Lexer;
import me.jugg.parser.Parser;

public class Main {
    public static void main(String[] args) {
        String code = "1+2";
        Lexer lexer = new Lexer(code);
        Parser p = new Parser(lexer);
        Expression expression = p.parseMain();
        System.out.println(Evaluator.eval(expression));
    }
}