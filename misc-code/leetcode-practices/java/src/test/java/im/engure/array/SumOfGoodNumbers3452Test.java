package im.engure.array;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SumOfGoodNumbers3452Test {

    @Test
    void t1() {
        SumOfGoodNumbers3452 o = new SumOfGoodNumbers3452();
        int[] arr = new int[]{1, 3, 2, 1, 5, 4};
        Assertions.assertEquals(12, o.sumOfGoodNumbers(arr, 2));
    }

    @Test
    void t2() {
        SumOfGoodNumbers3452 o = new SumOfGoodNumbers3452();
        int[] arr = new int[]{2, 1};
        Assertions.assertEquals(2, o.sumOfGoodNumbers(arr, 1));
    }

    @Test
    void t3() {
        SumOfGoodNumbers3452 o = new SumOfGoodNumbers3452();
        int[] arr = new int[]{47, 47};
        Assertions.assertEquals(0, o.sumOfGoodNumbers(arr, 1));
    }
}