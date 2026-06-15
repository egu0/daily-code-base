package im.engure.list;

import im.engure.util.ArrayAssertions;
import org.junit.jupiter.api.Test;

class DeleteMiddle2095Test {

    @Test
    void t1() {
        DeleteMiddle2095 o = new DeleteMiddle2095();
        ListNode l = o.deleteMiddle(ListUtil.buildList(new int[]{1, 3, 4, 7, 1, 2, 6}));
        int[] resArr = ListUtil.arrayOf(l);
        int[] expected = {1, 3, 4, 1, 2, 6};

        ArrayAssertions.assertEqual(expected, resArr);
    }

    @Test
    void t2() {
        DeleteMiddle2095 o = new DeleteMiddle2095();
        ListNode l = o.deleteMiddle(ListUtil.buildList(new int[]{1, 2, 3, 4}));
        int[] resArr = ListUtil.arrayOf(l);
        int[] expected = {1, 2, 4};

        ArrayAssertions.assertEqual(expected, resArr);
    }

    @Test
    void t3() {
        DeleteMiddle2095 o = new DeleteMiddle2095();
        ListNode l = o.deleteMiddle(ListUtil.buildList(new int[]{1, 2}));
        int[] resArr = ListUtil.arrayOf(l);
        int[] expected = {1};

        ArrayAssertions.assertEqual(expected, resArr);
    }

    @Test
    void t4() {
        DeleteMiddle2095 o = new DeleteMiddle2095();
        ListNode l = o.deleteMiddle(ListUtil.buildList(new int[]{1}));
        int[] resArr = ListUtil.arrayOf(l);

        ArrayAssertions.assertEqual(null, resArr);
    }
}