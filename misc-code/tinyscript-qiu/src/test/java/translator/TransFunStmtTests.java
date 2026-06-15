package translator;

import static translator.TransExprTests.assertInstructions;

import org.junit.jupiter.api.Test;

import lexer.LexicalException;
import parser.Parser;
import parser.util.ParseException;

public class TransFunStmtTests {
    @Test
    public void simple() throws LexicalException, ParseException {
        var source = """
                func add(int a, int b) int {
                    return a + b
                }
                """;
        var astNode = Parser.parse(source);
        // astNode.print(0);

        var translator = new Translator();
        var program = translator.translate(astNode);
        // System.out.println(program);

        var expectedResults = new String[] {
                "L0:",
                "FUNC_BEGIN",
                "p1 = a + b",
                "RETURN p1"
        };
        assertInstructions(expectedResults, program.getInstructions());
    }

    @Test
    public void call() throws LexicalException, ParseException {
        var source = """
                func add(int a, int b) int {
                    return a + b
                }
                var s = add(1, 2)
                """;
        var astNode = Parser.parse(source);
        // astNode.print(0);

        var translator = new Translator();
        var program = translator.translate(astNode);
        // System.out.println(program);

        var expectedResults = new String[] {
                "L0:",
                "FUNC_BEGIN",
                "p1 = a + b",
                "RETURN p1",
                "PARAM 1 3",
                "PARAM 2 4",
                "SP -2",
                "CALL L0",
                "SP 2",
                "s = p0",
        };
        assertInstructions(expectedResults, program.getInstructions());
    }

    @Test
    public void recursive() throws LexicalException, ParseException {
        var source = """
                func fact(int n) int {
                    if (n == 0) {
                        return 1
                    }
                    return fact(n - 1) * n
                }
                """;
        var astNode = Parser.parse(source);
        // astNode.print(0);

        var translator = new Translator();
        var program = translator.translate(astNode);
        System.out.println(program);

        var expectedResults = new String[] {
                "L0:",
                "FUNC_BEGIN",
                "p1 = n == 0",
                "IF p1 ELSE L1",
                "RETURN 1",
                "L1:",
                "p2 = n - 1",
                "PARAM p2 6",
                "SP -5",
                "CALL L0",
                "SP 5",
                "p4 = p3 * n",
                "RETURN p4",
        };
        assertInstructions(expectedResults, program.getInstructions());
    }

    @Test
    public void recvRecursive() throws LexicalException, ParseException {
        var source = """
                func fact(int n) int {
                    if (n == 0) {
                        return 1
                    }
                    return fact(n - 1) * n
                }
                var res = fact(5)
                """;
        var astNode = Parser.parse(source);
        // astNode.print(0);

        var translator = new Translator();
        var program = translator.translate(astNode);
        // System.out.println(program);

        var expectedResults = new String[] {
                "L0:",
                "FUNC_BEGIN",
                "p1 = n == 0",
                "IF p1 ELSE L1",
                "RETURN 1",
                "L1:",
                "p2 = n - 1",
                "PARAM p2 6",
                "SP -5",
                "CALL L0",
                "SP 5",
                "p4 = p3 * n",
                "RETURN p4",

                "PARAM 5 3",
                "SP -2",
                "CALL L0",
                "SP 2",
                "res = p0"
        };
        assertInstructions(expectedResults, program.getInstructions());
    }
}
