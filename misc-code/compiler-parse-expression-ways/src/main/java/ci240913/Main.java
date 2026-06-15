package ci240913;

import java.io.IOException;
import java.util.List;

public class Main {
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
//        assertEqualStr(parseAndEvaluate("10 - 2 * 3"), "4");
//        assertEqualStr(parseAndEvaluate("(10 - 2) * 3"), "24");
//        parseAndEvaluate("2 * (3 / -\"muffin\")");
        parseAndEvaluate("1 / 0");
    }

    private static String parseAndEvaluate(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        if (hadError) return null;

        System.out.println("original expression: " + source);
        System.out.println("parsed   expression: " + new AstPrinter().print(expression));

        var res = interpreter.interpret(expression);
        System.out.println();
        return res;
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }

    private static void assertEqualStr(String real, String expected) {
        if (expected == null || !expected.equals(real)) {
            throw new AssertionError("Expected " + expected + " but got " + real);
        }
    }
}