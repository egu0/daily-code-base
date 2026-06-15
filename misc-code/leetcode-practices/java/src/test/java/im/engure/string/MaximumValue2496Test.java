package im.engure.string;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MaximumValue2496Test {

    @Test
    void t1() {
        int res = new MaximumValue2496().maximumValue(new String[]{"alic3", "bob", "3", "4", "00000"});
        Assertions.assertEquals(5, res);
    }

    @Test
    void t2() {
        int res = new MaximumValue2496().maximumValue(new String[]{"1","01","001","0001"});
        Assertions.assertEquals(1, res);
    }
}