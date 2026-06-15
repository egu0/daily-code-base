package im.engure.list;

public class ListUtil {
    public static int[] arrayOf(ListNode node) {
        if (node == null) {
            return new int[0];
        }
        int length = 0;
        ListNode cur = node;
        while (cur != null) {
            length++;
            cur = cur.next;
        }
        // build array
        int[] res = new int[length];
        int i = 0;
        cur = node;
        while (cur != null) {
            res[i++] = cur.val;
            cur = cur.next;
        }
        return res;
    }

    public static ListNode buildList(int[] arr) {
        if (arr == null || arr.length == 0) {
            return null;
        }
        ListNode first = new ListNode();
        ListNode cur = first;
        int n = arr.length;
        int i = 0;
        while (i < n) {
            cur.next = new ListNode(arr[i++]);
            cur = cur.next;
        }
        return first.next;
    }

    public static void printList(ListNode head) {
        System.out.print("[ ");
        if (head == null) {
            System.out.print("]\n");
            return;
        }
        while (head != null) {
            System.out.printf("%d", head.val);
            head = head.next;
            if (head != null) {
                System.out.print(" --> ");
            }
        }
        System.out.print(" ]\n");
    }
}
