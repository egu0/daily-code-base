package im.engure.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PivotInteger2485Test {

    static PivotInteger2485 o;

    @BeforeAll
    static void setUp() {
        o = new PivotInteger2485();
    }

    @Test
    void pivotInteger1() {
        Assertions.assertEquals(6, o.pivotInteger(8));
    }

    @Test
    void pivotInteger2() {
        Assertions.assertEquals(1, o.pivotInteger(1));
    }

    @Test
    void pivotInteger3() {
        Assertions.assertEquals(-1, o.pivotInteger(4));
    }
}