package translator;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import lexer.LexicalException;
import parser.Parser;
import parser.util.ParseException;
import translator.symbol.SymbolTable;

public class TransExprTests {

    public static void assertInstructions(String[] lines, ArrayList<TAInstruction> instructions) {
        for (int i = 0; i < instructions.size(); i++) {
            assertEquals(lines[i], instructions.get(i).toString());
        }
    }

    @Test
    public void transExpr() throws LexicalException, ParseException {
        var source = "a+(b-c)+d*(b-c)*2";
        var p = Parser.parse(source);
        // p.print(0);

        var exprNode = p.getChild(0);

        var symbolTable = new SymbolTable();
        var program = new TAProgram();

        var translator = new Translator();
        translator.translateExpr(program, exprNode, symbolTable);
        // System.out.println(program);

        var expectedResults = new String[] {
                "p0 = b - c",
                "p1 = b - c",
                "p2 = p1 * 2",
                "p3 = d * p2",
                "p4 = p0 + p3",
                "p5 = a + p4",
        };
        assertInstructions(expectedResults, program.getInstructions());
    }

    @Test
    public void assignStmt() throws LexicalException, ParseException {
        var source = "a=1.0*2.0*3.0";
        var astNode = Parser.parse(source);
        // astNode.print(0);

        var translator = new Translator();
        var program = translator.translate(astNode);
        // System.out.println(program);

        var expectedResult = new String[] {
                "p0 = 2.0 * 3.0",
                "p1 = 1.0 * p0",
                "a = p1" };
        assertInstructions(expectedResult, program.getInstructions());
    }

    @Test
    public void testAssignStmt1() throws LexicalException, ParseException {
        var source = "a=1";
        var astNode = Parser.parse(source);

        var translator = new Translator();
        var program = translator.translate(astNode);
        var code = program.toString();
        // System.out.println(code);

        assertEquals("a = 1", code);
    }
}
