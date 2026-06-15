package im.engure.list;

public class SortList148 {

    /*
    归并。快慢指针分两半，对两半再进行排序
    */
    public ListNode sortList(ListNode head) {
        sort(head);
        return HEAD_DUMMY.next;
    }

    //哨兵
    ListNode HEAD_DUMMY = new ListNode(-1);
    ListNode TAIL_DUMMY = new ListNode(-1);

    /**
     * head 链表头
     * <p>
     * 链表排序后，链表头放在 headDummy.next，链表尾放在 tailDummy.next
     */
    public void sort(ListNode head) {
        // base cases
        if (head == null || head.next == null) HEAD_DUMMY.next = TAIL_DUMMY.next = head;//0或1
        else if (head.next.next == null) {//2
            if (head.val > head.next.val) {
                head.next.next = head;
                head = head.next;
                head.next.next = null;
            }
            HEAD_DUMMY.next = head;
            TAIL_DUMMY.next = head.next;
        } else {//345...
            ListNode fast = head.next.next, slow = head.next;
            while (fast.next != null && fast.next.next != null) {
                fast = fast.next.next;
                slow = slow.next;
            }
            ListNode tail, tmp = slow.next;
            slow.next = null;
            //左半部分
            sort(head);
            head = HEAD_DUMMY.next;
            tail = TAIL_DUMMY.next;

            //右半部分
            sort(tmp);

            //将两个链表连接起来，放在 head 和 tail 上
            HEAD_DUMMY.next = mergeTwoLists(head, HEAD_DUMMY.next);
            TAIL_DUMMY.next = (tail.val > TAIL_DUMMY.val) ? tail : TAIL_DUMMY;
        }

    }

    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode head, cur = new ListNode(-1);
        head = cur;
        while (true) {
            if (l1 == null || l2 == null) {
                cur.next = (l1 == null) ? l2 : l1;
                break;
            } else {
                if (l1.val < l2.val) {
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

}
