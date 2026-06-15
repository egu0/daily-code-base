package translator;

import org.junit.jupiter.api.Test;
import static translator.TransExprTests.assertInstructions;

import lexer.LexicalException;
import parser.Parser;
import parser.util.ParseException;

public class TransIfStmtTests {
    @Test
    public void ifStmt() throws ParseException, LexicalException {
        var source = """
                if (a) {
                    b = 1
                }
                """;

        var astNode = Parser.parse(source);
        // astNode.print(0);

        var translator = new Translator();
        var program = translator.translate(astNode);
        // System.out.println(program);

        var expectedResults = new String[] {
                "IF a ELSE L0",
                "b = 1",
                "L0:",
        };
        assertInstructions(expectedResults, program.getInstructions());
    }

    @Test
    public void ifElseStmt() throws ParseException, LexicalException {
        var source = """
                if (a) {
                    b = 1
                } else {
                    c = 2
                }
                """;

        var astNode = Parser.parse(source);
        // astNode.print(0);

        var translator = new Translator();
        var program = translator.translate(astNode);
        // System.out.println(program);

        var expectedResults = new String[] {
                "IF a ELSE L0",
                "b = 1",
                "GOTO L1",
                "L0:",
                "c = 2",
                "L1:",
        };
        assertInstructions(expectedResults, program.getInstructions());
    }

    @Test
    public void ifElseIfStmt() throws ParseException, LexicalException {
        var source = """
                if (a) {
                    b = 1
                } else if (b) {
                    c = 2
                } else {
                    d = 3
                }
                """;

        var astNode = Parser.parse(source);
        // astNode.print(0);

        var translator = new Translator();
        var program = translator.translate(astNode);
        // System.out.println(program);

        var expectedResults = new String[] {
                "IF a ELSE L0",
                "b = 1",
                "GOTO L3",
                "L0:",
                "IF b ELSE L1",
                "c = 2",
                "GOTO L2",
                "L1:",
                "d = 3",
                "L2:",
                "L3:",
        };
        assertInstructions(expectedResults, program.getInstructions());
    }

    @Test
    public void complex() throws LexicalException, ParseException {
        var source = """
                if(a == 1) {
                    b = 100
                } else if(a == 2) {
                    b = 500
                } else if(a == 3) {
                    b = a * 1000
                } else {
                    b = -1
                }
                """;
        var astNode = Parser.parse(source);
        // astNode.print(0);

        var translator = new Translator();
        var program = translator.translate(astNode);
        System.out.println(program);

        var expectedResults = new String[] {
                "p0 = a == 1",
                "IF p0 ELSE L0",
                "b = 100",
                "GOTO L5",
                "L0:",
                "p1 = a == 2",
                "IF p1 ELSE L1",
                "b = 500",
                "GOTO L4",
                "L1:",
                "p2 = a == 3",
                "IF p2 ELSE L2",
                "p1 = a * 1000",
                "b = p1",
                "GOTO L3",
                "L2:",
                "b = -1",
                "L3:",
                "L4:",
                "L5:"
        };
        assertInstructions(expectedResults, program.getInstructions());
    }

}
