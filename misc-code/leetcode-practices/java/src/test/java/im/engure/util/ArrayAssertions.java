package im.engure.util;

import org.junit.jupiter.api.Assertions;

public class ArrayAssertions {

    /**
     * 断言：长度相同，内容相同。空数组和 null 数组视为相同
     */
    public static void assertEqual(int[] expected, int[] actual) {
        if (actual == expected) {
            return;
        }
        if (expected != null && actual != null) {
            Assertions.assertEquals(expected.length, actual.length);
            for (int i = 0; i < expected.length; i++) {
                Assertions.assertEquals(expected[i], actual[i]);
            }
        } else {
            if (expected == null) {
                Assertions.assertEquals(0, actual.length);
            }
            if (actual == null) {
                Assertions.assertEquals(0, expected.length);
            }
        }
    }

    public static void assertEqual(Integer[] expected, Integer[] actual) {
        if (actual == expected) {
            return;
        }
        if (expected != null && actual != null) {
            Assertions.assertEquals(expected.length, actual.length);
            for (int i = 0; i < expected.length; i++) {
                Assertions.assertEquals(expected[i], actual[i]);
            }
        } else {
            if (expected == null) {
                Assertions.assertEquals(0, actual.length);
            }
            if (actual == null) {
                Assertions.assertEquals(0, expected.length);
            }
        }
    }
}
