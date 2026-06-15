import me.jugg.ast.Expression;
import me.jugg.ast.InfixExpression;
import me.jugg.ast.IntegerExpression;
import me.jugg.ast.PrefixExpression;
import me.jugg.evaluator.Evaluator;
import me.jugg.lexer.Lexer;
import me.jugg.parser.Parser;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LexerTest {

    public static void printExpr(Expression expr, int level) {
        if (expr instanceof IntegerExpression e) {
            BigInteger val = e.getValue();
            for (int i = 0; i < level; i++) {
                System.out.print("\t");
            }
            System.out.println(val + "");
        } else if (expr instanceof InfixExpression e) {
            Expression el = e.left;
            Expression er = e.right;
            String op = e.operator;

            printExpr(er, level + 1);

            for (int i = 0; i < level; i++) {
                System.out.print("\t");
            }
            System.out.println(op);

            printExpr(el, level + 1);
        } else if (expr instanceof PrefixExpression e) {
            Expression er = e.right;
            var op = e.operator;

            printExpr(er, level + 1);

            for (int i = 0; i < level; i++) {
                System.out.print("\t");
            }
            System.out.println(op);
        }
    }

    public static void main(String[] args) {
        String code = "(-4 + 8*2 - 4^2) - (-2 * 2^3)";
        Lexer lexer = new Lexer(code);
        Parser p = new Parser(lexer);
        Expression expression = p.parseMain();
        printExpr(expression, 0);
    }

    @Test
    public void basic_add() {
        String code = "1+2";
        Lexer lexer = new Lexer(code);
        Parser p = new Parser(lexer);
        Expression expression = p.parseMain();
        printExpr(expression, 0);

        /*
                +
              /   \
             1     2
         */
        var root = (InfixExpression) expression;
        assertEquals(root.operator, "+");
        assertEquals(((IntegerExpression) root.left).getValue().toString(), "1");
        assertEquals(((IntegerExpression) root.right).getValue().toString(), "2");
    }

    @Test
    public void prefix_expr() {
        String code = "-1+2";
        Lexer lexer = new Lexer(code);
        Parser p = new Parser(lexer);
        Expression expression = p.parseMain();
        printExpr(expression, 0);

        /*
               +
             /   \
             -     2
              \
               1
         */
        var root = (InfixExpression) expression;
        assertEquals(root.operator, "+");

        var left = (PrefixExpression) root.left;
        assertEquals(left.operator, "-");
        assertEquals(((IntegerExpression) (left.right)).getValue().toString(), "1");

        var right = ((IntegerExpression) root.right);
        assertEquals(((IntegerExpression) right).getValue().toString(), "2");
    }

    @Test
    public void prefixAndGroup() {
        String code = "-(1+2)";
        Lexer lexer = new Lexer(code);
        Parser p = new Parser(lexer);
        Expression expression = p.parseMain();
        printExpr(expression, 0);

        var root = (PrefixExpression) expression;
        assertEquals(root.operator, "-");

        /*
              -
                \
                  +
                /   \
               1     2
         */
        var right = (InfixExpression) root.right;
        assertEquals(right.operator, "+");
        assertEquals(((IntegerExpression) right.left).getValue().toString(), "1");
        assertEquals(((IntegerExpression) right.right).getValue().toString(), "2");

    }

    @Test
    public void complex_one() {
        String code = "1-(-2*2^3)";
        Lexer lexer = new Lexer(code);
        Parser p = new Parser(lexer);
        Expression expression = p.parseMain();
        printExpr(expression, 0);
        var result = Evaluator.eval(expression);
        assertNotNull(result);
        assertEquals(result.toString(), "17");
    }

    @Test
    public void complex_one2() {
        String code = "(-4 + 8*2 - 4^2) - (-2 * 2^3)";
        Lexer lexer = new Lexer(code);
        Parser p = new Parser(lexer);
        Expression expression = p.parseMain();
        printExpr(expression, 0);
        var result = Evaluator.eval(expression);
        assertNotNull(result);
        assertEquals(result.toString(), "12");
    }
}
