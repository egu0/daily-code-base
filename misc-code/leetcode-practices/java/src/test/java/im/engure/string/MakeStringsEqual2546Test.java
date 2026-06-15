package im.engure.string;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MakeStringsEqual2546Test {
    static MakeStringsEqual2546 o;

    @BeforeAll
    static void setup() {
        o = new MakeStringsEqual2546();
    }

    @Test
    void makeStringsEqual1() {
        Assertions.assertTrue(o.makeStringsEqual("0111", "1111"));
    }

    @Test
    void makeStringsEqual2() {
        Assertions.assertFalse(o.makeStringsEqual("0111", "0000"));
    }
}