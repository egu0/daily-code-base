package im.engure.array;

import im.engure.math.CountGoodArrays3405;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CountGoodArrays3405Test {

    @Test
    void t1() {
        CountGoodArrays3405 o = new CountGoodArrays3405();
        Assertions.assertEquals(4, o.countGoodArrays(3, 2, 1));
    }

    @Test
    void t2() {
        CountGoodArrays3405 o = new CountGoodArrays3405();
        Assertions.assertEquals(6, o.countGoodArrays(4, 2, 2));
    }

    @Test
    void t3() {
        CountGoodArrays3405 o = new CountGoodArrays3405();
        Assertions.assertEquals(2, o.countGoodArrays(5, 2, 0));
    }

    @Test
    void t4() {
        CountGoodArrays3405 o = new CountGoodArrays3405();
        Assertions.assertEquals(2160, o.countGoodArrays(7, 4, 3));
    }
}