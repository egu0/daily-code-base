package translator;

import org.junit.jupiter.api.Test;
import static translator.TransExprTests.assertInstructions;

import lexer.LexicalException;
import parser.Parser;
import parser.util.ParseException;

public class TransBlockTests {

    @Test
    public void block() throws ParseException, LexicalException {
        var source = """
                var a = 1
                {
                    var b = a * 100
                }
                {
                    var b = a * 100
                }
                """;
        var astNode = Parser.parse(source);
        // astNode.print(0);

        var translator = new Translator();
        var program = translator.translate(astNode);
        System.out.println(program);

        var expectedResults = new String[] {
                "a = 1",
                "p1 = a * 100",
                "b = p1",
                "p1 = a * 100",
                "b = p1"
        };
        assertInstructions(expectedResults, program.getInstructions());
    }
}
