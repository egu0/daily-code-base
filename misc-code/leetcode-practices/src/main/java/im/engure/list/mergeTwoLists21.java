package im.engure.list;

public class mergeTwoLists21 {
}

class Solution21 {

    public ListNode mergeTwoLists(ListNode l1, ListNode l2, int v) {
        ListNode head, cur = new ListNode(-1);
        head = cur;
        while (true) {
            if (l1 == null || l2 == null) {
                cur.next = (l1 == null) ? l2 : l1;
                break;
            } else {
                if (l1.val > l2.val) {
                    cur.next = l1;
                    l1 = l1.next;
                } else {
                    cur.next = l2;
                    l2 = l2.next;
                }
                cur = cur.next;
            }
        }
        return head.next;
    }

    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {

        if (l1 == null) return l2;
        if (l2 == null) return l1;

        //l1 != null && l2 != null

        ListNode head = null;
        if (l1.val < l2.val) {
            head = l1;
            l1 = l1.next;
        } else {
            head = l2;
            l2 = l2.next;
        }
        ListNode cur = head;

        while (l1 != null || l2 != null) {

            if (l1 == null) cur.next = l2;
            if (l2 == null) cur.next = l1;

            if (l1 == null || l2 == null) break;

            if (l1.val < l2.val) {
                cur.next = l1;
                l1 = l1.next;
            } else {
                cur.next = l2;
                l2 = l2.next;
            }
            cur = cur.next;

        }

        return head;
    }
}
