package im.engure.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ToHex405Test {

    @Test
    void t1() {
        ToHex405 o = new ToHex405();
        Assertions.assertEquals("1a", o.toHex(26));
    }

    @Test
    void t2() {
        ToHex405 o = new ToHex405();
        Assertions.assertEquals("ffffffff", o.toHex(-1));
        Assertions.assertEquals("fffffffe", o.toHex(-2));
    }

    @Test
    void t3() {
        ToHex405 o = new ToHex405();
        Assertions.assertEquals("0", o.toHex(0));
    }
}