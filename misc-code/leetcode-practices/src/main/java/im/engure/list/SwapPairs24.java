package im.engure.list;

public class SwapPairs24 {
}

/*

给定一个链表，两两交换其中相邻的节点，并返回交换后的链表。

你不能只是单纯的改变节点内部的值，而是需要实际的进行节点交换。

 */

class Solution24 {
    public ListNode swapPairs(ListNode head) {

        if (head == null || head.next == null) return head;

        ListNode fakeHead = new ListNode(-1);
        fakeHead.next = head;

        return whatsUp(fakeHead).next;
    }

    /**
     * 要点：设计链表节点顺序改变时，最好画图看一下
     * 坑：加了伪节点怎么拼接
     *
     * @param head 伪头结点（头结点不是要交换的两个节点中的第一个，而是两个节点之前的一个节点）
     * @return 伪链表
     */
    public ListNode whatsUp(ListNode head) {

        // head后结点数 < 2个
        if (head == null || head.next == null || head.next.next == null)
            return head;

        ListNode l1 = head.next;
        ListNode l2 = head.next.next;

        l1.next = l2.next;
        head.next = l2;
        l2.next = l1;
        l1.next = whatsUp(l1).next;

        return head;
    }
}


/*

 1,2,[3,4],5,6

 3->5
 2->4
 4->3
 3->from(3).next

*/




