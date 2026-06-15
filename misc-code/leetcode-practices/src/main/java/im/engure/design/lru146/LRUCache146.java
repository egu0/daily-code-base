package im.engure.design.lru146;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;


/**
 * https://leetcode-cn.com/problems/lru-cache/
 * 使用 PriorityQueue + hashmap + 版本号 实现。
 * 567ms。
 * 时间复杂度高。
 */

public class LRUCache146 {

    /**
     * RUN：
     * 第 15 个用例出现错误：
     * ["LRUCache","put","put","put","put","put","get","put","get","get","put","get","put","put","put","get","put","get","get","get","get","put","put","get","get","get","put","put","get","put","get","put","get","get","get","put","put","put","get","put","get","get","put","put","get","put","put","put","put","get","put","put","get","put","put","get","put","put","put","put","put","get","put","put","get","put","get","get","get","put","get","get","put","put","put","put","get","put","put","put","put","get","get","get","put","put","put","get","put","put","put","get","put","put","put","get","get","get","put","put","put","put","get","put","put","put","put","put","put","put"]
     * [[10],[10,13],[3,17],[6,11],[10,5],[9,10],[13],[2,19],[2],[3],[5,25],[8],[9,22],[5,5],[1,30],[11],[9,12],[7],[5],[8],[9],[4,30],[9,3],[9],[10],[10],[6,14],[3,1],[3],[10,11],[8],[2,14],[1],[5],[4],[11,4],[12,24],[5,18],[13],[7,23],[8],[12],[3,27],[2,12],[5],[2,9],[13,4],[8,18],[1,7],[6],[9,29],[8,21],[5],[6,30],[1,12],[10],[4,15],[7,22],[11,26],[8,17],[9,29],[5],[3,4],[11,30],[12],[4,29],[3],[9],[6],[3,4],[1],[10],[3,29],[10,28],[1,20],[11,13],[3],[3,12],[3,8],[10,9],[3,26],[8],[7],[5],[13,17],[2,27],[11,15],[12],[9,19],[2,15],[3,16],[1],[12,17],[9,1],[6,19],[4],[5],[5],[8,1],[11,7],[5,2],[9,28],[1],[2,2],[7,4],[4,22],[7,24],[9,26],[13,28],[11,26]]
     *
     * @param args
     */

    public static int version = 0;//版本号，不使用 System.currentTimeMillis() 因为可能相同

    public static void main(String[] args) {

        /*
        补充：
        一个无界阻塞队列，它使用与类PriorityQueue相同的排序规则并提供阻塞检索操作。
        虽然这个队列在逻辑上是无界的，但由于资源耗尽（导致OutOfMemoryError ），尝试添加可能会失败。 此类不允许null元素。
        依赖于自然排序的优先级队列也不允许插入不可比较的对象（这样做会导致ClassCastException ）。
         */
        PriorityBlockingQueue<String> priorityBlockingQueue = new PriorityBlockingQueue<>();

        /////////////////////////////

        LRUCache146 l = new LRUCache146(10);
        l.put(10, 13);
        l.put(3, 17);
        l.put(6, 11);
        l.put(10, 5);
        l.put(9, 10);
        System.out.print(" " + l.get(13));
        l.put(2, 19);
        System.out.print(" " + l.get(2));
        System.out.print(" " + l.get(3));
        l.put(5, 25);
        System.out.print(" " + l.get(8));
        l.put(9, 22);
        l.put(5, 5);
        l.put(1, 30);
        System.out.print(" " + l.get(11));
        l.put(9, 12);
        System.out.print(" " + l.get(7));
        System.out.print(" " + l.get(5));
        System.out.print(" " + l.get(8));
        System.out.print(" " + l.get(9));
        l.put(4, 30);

        l.put(9, 3);
        System.out.print(" " + l.get(9));
        System.out.print(" " + l.get(10));
        System.out.print(" " + l.get(10));
        l.put(6, 14);
        l.put(3, 1);
        System.out.print(" " + l.get(3));
        l.put(10, 11);
        System.out.print(" " + l.get(8));
        l.put(2, 14);
        System.out.print(" " + l.get(1));
        System.out.print(" " + l.get(5));
        System.out.print(" " + l.get(4));
        l.put(11, 4);
        l.put(12, 24);
        l.put(5, 18);
        System.out.print(" " + l.get(13));
        l.put(7, 23);
        System.out.print(" " + l.get(8));
        System.out.print(" " + l.get(12));
        l.put(3, 27);
        l.put(2, 12);
        System.out.print(" " + l.get(5));

        l.put(2, 9);
        l.put(13, 4);
        l.put(8, 18);
        l.put(1, 7);
        System.out.print(" " + l.get(6));
        l.put(9, 29);
        l.put(8, 21);
        System.out.print(" " + l.get(5));
        l.put(6, 30);
        l.put(1, 12);
        System.out.print(" " + l.get(10));
        l.put(4, 15);
        l.put(7, 22);
        l.put(11, 26);
        l.put(8, 17);
        l.put(9, 29);
        System.out.print(" " + l.get(5));
        l.put(3, 4);
        l.put(11, 30);
        System.out.print(" " + l.get(12));

        l.put(4, 29);
        System.out.print(" " + l.get(3));
        System.out.print(" " + l.get(9));
        System.out.print(" " + l.get(6));
        l.put(3, 4);
        System.out.print(" " + l.get(1));
        System.out.print(" " + l.get(10));
        l.put(3, 29);
        l.put(10, 28);
        l.put(1, 20);
        l.put(11, 13);
        System.out.print(" " + l.get(3));
        l.put(3, 12);
        l.put(3, 8);
        l.put(10, 9);
        l.put(3, 26);
        System.out.print(" " + l.get(8));
        System.out.print(" " + l.get(7));
        System.out.print(" " + l.get(5));
        l.put(13, 17);
        l.put(2, 27);
        l.put(11, 15);

        System.out.print(" " + l.get(12));
        l.put(9, 19);
        l.put(2, 15);
        l.put(3, 16);
        System.out.print(" " + l.get(1));
        l.put(12, 17);
        l.put(9, 1);
        l.put(6, 19);
        System.out.print(" " + l.get(4));
        System.out.print(" " + l.get(5));
        System.out.print(" " + l.get(5));
        l.put(8, 1);
        l.put(11, 7);
        l.put(5, 2);
        l.put(9, 28);
        System.out.print(" " + l.get(1));
        l.put(2, 2);
        l.put(7, 4);
        l.put(4, 22);
        l.put(7, 24);
        l.put(9, 26);

        l.put(13, 28);
        l.put(11, 26);

    }

    int capacity;
    HashMap<String, Pair> map = new HashMap<>();
    PriorityQueue<Pair> priorityQueue = new PriorityQueue<>();//使用 comparable 接口而非 comparator 接口！！

    public LRUCache146(int capacity) {
        this.capacity = capacity;
    }

    public int get(int key) {
        Pair pair = map.get(String.valueOf(key));
        if (pair != null) {
            Pair pair4 = new Pair(key, pair.value, version++);
            priorityQueue.remove(pair);
            priorityQueue.add(pair4);
            map.put(String.valueOf(key), pair4);
            return pair4.value;
        }
        return -1;
    }

    public void put(int key, int value) {

        Pair pair0 = map.get(String.valueOf(key));

        if (pair0 != null) {
            priorityQueue.remove(pair0);
            Pair pair1 = new Pair(key, value, version++);
            map.put(String.valueOf(key), pair1);//覆盖
            priorityQueue.add(pair1);
            return;
        }

        if (map.size() == capacity) {
            Pair pair2 = priorityQueue.poll();
            String kmap = String.valueOf(pair2.key);
            map.remove(kmap);
        }

        Pair pair3 = new Pair(key, value, version++);
        map.put(String.valueOf(key), pair3);
        priorityQueue.add(pair3);
    }

}

class Pair implements Comparable<Pair> {

    public int key;
    public int value;
    public long timeStamp;//机器运行速度快，不能使用 System.currentTimeMillis()，导致 timestamp 相同无法比较

    public Pair() {
    }

    public Pair(int k, int v, long ts) {
        this.key = k;
        this.value = v;
        this.timeStamp = ts;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "key=" + key +
                ", value=" + value +
                ", timeStamp=" + timeStamp +
                '}';
    }

    @Override
    public int compareTo(Pair o) {
        return (int) (timeStamp - o.timeStamp);
    }
}

class Test146 {

    public static void main(String[] args) {

        PriorityQueue<Pair> pq = new PriorityQueue<>();

        pq.add(new Pair(1, 1, 1L));
        pq.add(new Pair(2, 1, 2L));
        pq.add(new Pair(3, 1, 3L));
        pq.poll();
        pq.add(new Pair(4, 1, 4L));
        pq.poll();
        pq.add(new Pair(5, 1, 5L));
        pq.add(new Pair(6, 1, 6L));


        System.out.println("---------------------");

        Pair p;
        while ((p = pq.poll()) != null) {
            System.out.println(p);
        }

    }


}

class Test146_2 {
    public static void main(String[] args) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();

        pq.add(1);
        pq.add(2);
        pq.add(3);
        pq.add(4);
        pq.add(5);
        pq.add(6);

        Integer p;
        while ((p = pq.poll()) != null) {
            System.out.println(p);
        }

    }
}


