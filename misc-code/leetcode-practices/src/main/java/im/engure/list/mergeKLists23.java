package im.engure.list;

public class mergeKLists23 {

}

/**
 * 给你一个链表数组，每个链表都已经按升序排列。
 * <p>
 * 请你将所有链表合并到一个升序链表中，返回合并后的链表。
 */
class Solution23 {

    ListNode[] lists;

    public ListNode mergeKLists(ListNode[] lists) {

        if (lists == null || lists.length == 0) return null;

        this.lists = lists;

        int len = lists.length;

        int left = 0, right = len - 1;

        return mergeFromCenter(left, right);
    }

    public ListNode mergeFromCenter(int left, int right) {

        if (left > right) return null;
        if (left == right) return lists[left];

        int middle = (right - left) / 2 + left;

        return mergeTwoLists(mergeFromCenter(left, middle),
                mergeFromCenter(middle + 1, right));
    }

    /**
     * 21题两个链表合并
     *
     * @param l1 list-1
     * @param l2 list-2
     * @return merged-list
     */
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

        while (true) {

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
