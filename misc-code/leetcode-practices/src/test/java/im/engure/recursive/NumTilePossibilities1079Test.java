package im.engure.recursive;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NumTilePossibilities1079Test {

    @Test
    void t1() {
        NumTilePossibilities1079 o = new NumTilePossibilities1079();
        Assertions.assertEquals(8, o.numTilePossibilities("AAB"));
    }

    @Test
    void t2() {
        NumTilePossibilities1079 o = new NumTilePossibilities1079();
        Assertions.assertEquals(188, o.numTilePossibilities("AAABBC"));
    }

    @Test
    void t3() {
        NumTilePossibilities1079 o = new NumTilePossibilities1079();
        Assertions.assertEquals(1, o.numTilePossibilities("V"));
    }
}