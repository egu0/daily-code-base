package im.engure.array;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BfprtUtilTest {

    @Test
    void simple() {
        int[] arr = {1};
        Assertions.assertEquals(1, BfprtUtil.bfprt(arr, 0, arr.length, 1));
    }

    @Test
    void testBfprt() {
        int[] arr = {5, 4, 3, 2, 1, 6};
        Assertions.assertEquals(1, BfprtUtil.bfprt(arr, 0, arr.length, 1));
        Assertions.assertEquals(2, BfprtUtil.bfprt(arr, 0, arr.length, 2));
        Assertions.assertEquals(3, BfprtUtil.bfprt(arr, 0, arr.length, 3));
        Assertions.assertEquals(4, BfprtUtil.bfprt(arr, 0, arr.length, 4));
        Assertions.assertEquals(5, BfprtUtil.bfprt(arr, 0, arr.length, 5));
        Assertions.assertEquals(6, BfprtUtil.bfprt(arr, 0, arr.length, 6));
    }
}