package im.engure.list;

public class ReverseKGroup25 {
}

/**
 *
 *
 * 25. K 个一组翻转链表
 *
 * 给你一个链表，每 k 个节点一组进行翻转，请你返回翻转后的链表。
 *
 * k 是一个正整数，它的值小于或等于链表的长度。
 *
 * 如果节点总数不是 k 的整数倍，那么请将最后剩余的节点保持原有顺序。
 *
 * 进阶：
 *
 *     你可以设计一个只使用常数额外空间的算法来解决此问题吗？
 *     你不能只是单纯的改变节点内部的值，而是需要实际进行节点交换。
 *
 *
 *
 *
 *
 * 题解：https://leetcode-cn.com/problems/reverse-nodes-in-k-group/solution/di-gui-fang-fa-shi-yong-tou-bu-wei-jie-d-rnrf/
 *
 *
 * 关键：
 *  使用”伪结点“，简化操作，
 *  使用递归，易读性好
 *
 *
 */

class Solution {

    public ListNode reverseKGroup(ListNode head, int k) {

        if (k == 1) return head;

        ListNode cur = head;
        int count = 0;
        while (count < k) {
            if (cur == null) return head;
            count++;
            cur = cur.next;
        }

        ListNode fakeHead = new ListNode(-1);
        fakeHead.next = head;

        return whatsUp(fakeHead, k).next;
    }

    public ListNode whatsUp(ListNode head, int k) {

        //伪节点 head 后节点数小于 k 个，不用反转
        ListNode cur = head;
        int count = 0;
        while (count < k + 1) {
            if (cur == null) return head;
            count++;
            cur = cur.next;
        }

        //此时cur指向 head 第 k+1 个节点
        // head 1 2 3 .... k  k+1
        //                    cur

        ListNode next = head.next;//head+1位置
        ListNode nextToNext = head.next.next;//head+2位置
        next.next = cur;
        ListNode nextBegin = next;// 1 位置作为下一次开始的伪节点
        //count和cur使命完成
        cur = nextToNext;
        count = 1;
        while (count < k) {
            nextToNext = cur.next;
            cur.next = next;
            next = cur;
            cur = nextToNext;
            count++;
        }
        head.next = cur;
        nextBegin.next = whatsUp(nextBegin, k).next;

        return head;
    }

}
