package im.engure.string;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScoreOfParentheses856Test {

    @Test
    void t1() {
        ScoreOfParentheses856 o = new ScoreOfParentheses856();
        Assertions.assertEquals(1, o.scoreOfParentheses("()"));
    }

    @Test
    void t2() {
        ScoreOfParentheses856 o = new ScoreOfParentheses856();
        Assertions.assertEquals(2, o.scoreOfParentheses("()()"));
    }

    @Test
    void t3() {
        ScoreOfParentheses856 o = new ScoreOfParentheses856();
        Assertions.assertEquals(2, o.scoreOfParentheses("(())"));
    }

    @Test
    void t4() {
        ScoreOfParentheses856 o = new ScoreOfParentheses856();
        Assertions.assertEquals(4, o.scoreOfParentheses("(())()()"));
    }

    @Test
    void t5() {
        ScoreOfParentheses856 o = new ScoreOfParentheses856();
        Assertions.assertEquals(8, o.scoreOfParentheses("((())(()))"));
    }
}