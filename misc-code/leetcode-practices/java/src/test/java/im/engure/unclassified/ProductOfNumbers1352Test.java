package im.engure.unclassified;

import im.engure.design.ProductOfNumbers1352;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProductOfNumbers1352Test {

    @Test
    void t1() {
        /*
        3 0 2 5  4
        3 1 2 10 40
         */
        ProductOfNumbers1352.ProductOfNumbers o = new ProductOfNumbers1352.ProductOfNumbers();
        o.add(3);
        o.add(0);
        o.add(2);
        o.add(5);
        o.add(4);
        Assertions.assertEquals(20, o.getProduct(2));
        Assertions.assertEquals(40, o.getProduct(3));
        Assertions.assertEquals(0, o.getProduct(4));
        Assertions.assertEquals(0, o.getProduct(5));
        o.add(8);
        Assertions.assertEquals(32, o.getProduct(2));
    }

    @Test
    void t2() {
        /*
        3 1 2 5   4
        3 3 6 30 120
         */
        ProductOfNumbers1352.ProductOfNumbers o = new ProductOfNumbers1352.ProductOfNumbers();
        o.add(3);
        o.add(1);
        o.add(2);
        o.add(5);
        o.add(4);
        Assertions.assertEquals(4, o.getProduct(1));
        Assertions.assertEquals(20, o.getProduct(2));
        Assertions.assertEquals(40, o.getProduct(3));
        Assertions.assertEquals(40, o.getProduct(4));
        Assertions.assertEquals(120, o.getProduct(5));
    }

}