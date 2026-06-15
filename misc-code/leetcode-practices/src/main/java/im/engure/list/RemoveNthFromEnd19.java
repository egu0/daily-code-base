package im.engure.list;

public class RemoveNthFromEnd19 {
    public static void main(String[] args) {

    }
}

/**
 * 删除链表最后第n个结点
 * 连老师讲过：双指针，前者先跑，后者跟着它，相差 n 个距离。先跑的到头后，后者就是要删除的位置
 */


class Solution19 {
    public ListNode removeNthFromEnd(ListNode head, int n) {

        ListNode master = head, slave = head;

        for (int i = 0; i < n; i++) {
            if (master != null) master = master.next;
            //master==null
        }

        //n = 链表长度
        if (master == null) return head.next;

        while (master != null) {
            slave = slave.next;
            master = master.next;
        }

        //找到salve的附结点
        ListNode cur = head;
        while (cur.next != slave) cur = cur.next;
        cur.next = slave.next;

        return head;
    }
}
