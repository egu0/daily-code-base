package im.engure.string;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SmallestNumber2375Test {

    @Test
    void t1() {
        SmallestNumber2375 o = new SmallestNumber2375();
        Assertions.assertEquals("123549876", o.smallestNumber("IIIDIDDD"));
    }

    @Test
    void t2() {
        SmallestNumber2375 o = new SmallestNumber2375();
        Assertions.assertEquals("4321", o.smallestNumber("DDD"));
    }

    @Test
    void t3() {
        SmallestNumber2375 o = new SmallestNumber2375();
        Assertions.assertEquals("21", o.smallestNumber("D"));
    }

    @Test
    void t4() {
        SmallestNumber2375 o = new SmallestNumber2375();
        Assertions.assertEquals("321", o.smallestNumber("DD"));
    }

    @Test
    void t5() {
        SmallestNumber2375 o = new SmallestNumber2375();
        Assertions.assertEquals("12", o.smallestNumber("I"));
    }

    @Test
    void t6() {
        SmallestNumber2375 o = new SmallestNumber2375();
        Assertions.assertEquals("123", o.smallestNumber("II"));
    }
}