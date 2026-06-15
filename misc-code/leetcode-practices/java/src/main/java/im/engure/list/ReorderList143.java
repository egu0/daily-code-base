package im.engure.list;

import java.util.HashMap;
import java.util.Map;

public class ReorderList143 {
    /**
     * 時間：O(N)
     */
    public void reorderList(ListNode head) {
        // 第一遍
        Map<Integer, ListNode> map = new HashMap<>();
        int index = 1;
        while (head != null) {
            map.put(index++, head);
            head = head.next;
        }
        int len = index - 1;

        // 第二遍
        for (int i = 1; i <= len / 2; i++) {
            ListNode remote = map.get(len - i + 1);
            remote.next = map.get(i + 1);
            map.get(i).next = remote;
        }
        map.get(len / 2 + 1).next = null;
    }
}

