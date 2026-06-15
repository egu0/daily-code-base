package im.engure.heap;

/**
 * 优先级队列：元素通过优先级排列，高优先级元素靠前，低优先级靠后
 * * 和堆没关系。
 * 可以使用链表和堆等等数据结构来实现。
 */


public class PriorityQueueByListDemo {

    /**
     * 基于链表的优先级队列
     * * 插入：O(N)
     * * 取头：O(1)
     */

    public static void main(String[] args) {
        PriorityQueueByListDemo pq = new PriorityQueueByListDemo();
        int[] priorities = {1, 8, 3, 2, 1, 10, 4, 20};
        for (int i = 0; i < priorities.length; i++) {
            pq.add(priorities[i], priorities[i]);
        }
        while (true) {
            System.out.println(pq.poll() + " ");
        }

    }

    //=========================

    Node head;

    static class Node {
        public Node next;
        public int val;
        public int priority;

        public Node(int val, int priority) {
            this.val = val;
            this.priority = priority;
        }
    }

    public PriorityQueueByListDemo() {
    }

    /*
    取队头
     */
    public int peek() {
        if (head == null) throw new RuntimeException("empty priority queue");
        return head.val;
    }

    /*
    取队头并删除它
     */
    public int poll() {
        if (head == null) throw new RuntimeException("empty priority queue");
        int tmp = head.val;
        head = head.next;
        return tmp;
    }

    /*
    添加元素
     */
    public void add(int val, int priority) {
        if (head == null) {
            head = new Node(val, priority);
            return;
        }

        //根据 priority 将 Node 进行排序
        // priority 值越小，优先级越大

        if (priority <= head.priority) {
            Node newHead = new Node(val, priority);
            newHead.next = head;
            head = newHead;
            return;
        }

        Node cur = head;
        while (cur.next != null && cur.priority < priority) {
            if (priority <= cur.next.priority) {
                Node newNode = new Node(val, priority);
                newNode.next = cur.next;
                cur.next = newNode;
                return;
            } else {
                cur = cur.next;
            }
        }
        if (cur.next == null) {
            cur.next = new Node(val, priority);
        }

    }

}
