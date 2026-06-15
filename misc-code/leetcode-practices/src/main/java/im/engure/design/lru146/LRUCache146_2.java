package im.engure.design.lru146;

import java.util.HashMap;
import java.util.Map;

/*
使用 hashmap + double linked list
重点：当“缓存”放满后，移除双向链表的尾部节点
时间复杂度：O(1)，用时 59ms，击败 33%
>>> 还有很多问题~~~
*/
public class LRUCache146_2 {

    Map<String, Node> map;

    // two dummy node
    Node head;
    Node tail;

    // cache size
    int capacity;

    static class Node {

        int key;
        int val;
        Node prev;
        Node next;

        public Node(int key, int val) {
            this.key = key;
            this.val = val;
        }

    }

    public LRUCache146_2(int capacity) {
        this.capacity = capacity;
        map = new HashMap<>(capacity);
        head = new Node(-1, -1);
        tail = new Node(-1, -1);
        head.next = tail;
        tail.prev = head;
        // head -> tail
    }

    public int get(int key) {
        Node node = map.get(String.valueOf(key));
        if (node == null) return -1;
        moveToHeadsNext(node);
        return node.val;
    }

    //将 node 移动到 head.next 的位置上（理论上的首个元素）
    private void moveToHeadsNext(Node node) {
        if (head.next == node) return;
        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.next = head.next;
        head.next.prev = node;
        node.prev = head;
        head.next = node;
    }

    public void put(int key, int value) {

        Node node = map.get(String.valueOf(key));
        if (node != null) {
            node.val = value;
            moveToHeadsNext(node);
            return;
        }

        if (map.size() == capacity) {
            int k = tail.prev.key;
            removeLastElement();
            map.remove(String.valueOf(k));
        }

        //new node insert to head.next
        Node newNode = new Node(key, value);
        map.put(String.valueOf(key), newNode);
        head.next.prev = newNode;
        newNode.next = head.next;
        head.next = newNode;
        newNode.prev = head;
    }

    /*
    移除理论上的最后一个元素
     */
    private void removeLastElement() {
        Node last = tail.prev;
        /*if (last == head) return;*/
        tail.prev = last.prev;
        last.prev.next = tail;
    }

    //=============================

    public static void main(String[] args) {
        LRUCache146_2 lRUCache = new LRUCache146_2(1);
        lRUCache.put(2, 1);
        System.out.println(lRUCache.get(2));
        lRUCache.put(3, 2);
        System.out.println(lRUCache.get(2));
        System.out.println(lRUCache.get(3));

        /*lRUCache.put(1, 1); // 缓存是 {1=1}
        lRUCache.put(2, 2); // 缓存是 {1=1, 2=2}
        lRUCache.get(1);    // 返回 1
        lRUCache.put(3, 3); // 该操作会使得关键字 2 作废，缓存是 {1=1, 3=3}
        lRUCache.get(2);    // 返回 -1 (未找到)
        lRUCache.put(4, 4); // 该操作会使得关键字 1 作废，缓存是 {4=4, 3=3}
        lRUCache.get(1);    // 返回 -1 (未找到)
        lRUCache.get(3);    // 返回 3
        lRUCache.get(4);    // 返回 4*/

        /*
        来源：力扣（LeetCode）
        链接：https://leetcode-cn.com/problems/lru-cache
        著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
        */
    }

}
