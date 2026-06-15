package im.engure.list;

public class DeleteMiddle2095 {

    // 快慢指针
    //1->2->3->4->5
    //   |     |
    //1->2->3->4
    //   |     |
    //1->2->3
    //|
    //1->2
    //|
    public ListNode deleteMiddle(ListNode head) {
        if (head.next == null) {
            return null;
        }
        ListNode fast = head.next, slow = head;
        while (fast.next != null && fast.next.next != null) {
            fast = fast.next.next;
            slow = slow.next;
        }
        slow.next = slow.next.next;
        return head;
    }

    // 常规做法
    public ListNode deleteMiddleV1(ListNode head) {
        ListNode cur = head;
        int length = 0;
        while (cur != null) {
            length++;
            cur = cur.next;
        }

        // 0->1->2->3->4
        // 0->1->2->3
        int steps = length / 2 - 1;
        cur = head;
        while (steps-- > 0) {
            cur = cur.next;
        }
        if (cur.next == null) {
            return null;
        } else {
            cur.next = cur.next.next;
        }
        return head;
    }
}
