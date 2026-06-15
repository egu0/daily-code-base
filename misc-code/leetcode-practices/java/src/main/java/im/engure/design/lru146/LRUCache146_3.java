package im.engure.design.lru146;

import java.util.LinkedHashMap;
import java.util.Map;

/*
使用 linkedhashmap
比 LRUCache146_2 好了一点点，击败 33% -> 45%，58ms -> 53ms
* LRUCache 继承 LinkedHashMap，重写 removeEldestEntry 控制元素容量。
* 换一种思路，移除头部节点，利用 put 方法将节点放在尾部的特点？验证。LRUCache146_4
 */

public class LRUCache146_3 extends LinkedHashMap<Integer, Integer> {

    int capacity;

    public LRUCache146_3(int capacity) {
        super(7000);
        this.capacity = capacity;
    }

    /*
     在 LinkedHashMap 添加元素后，会调用 removeEldestEntry 防范，传递的参数是最久没有被访问的键值对，
     * 如果方法返回 true，这个最久的键值对就会被删除。
     * LinkedHashMap 中的实现总返回 false，该子类重写后即可实现对容量的控制
    */
    @Override
    protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
        //return super.removeEldestEntry(eldest); 默认总是 false，表示不控制容量
        return size() > capacity;//控制容量。元素放满时返回 true
    }

    public int get(int key) {
        Integer val = super.get(key);//节点在链表和 hashmap 上的位置不变
        if (val == null) return -1;

        //手动将元素移动到链表头部
        remove(key);
        super.put(key, val);

        return val;
    }

    public void put(int key, int value) {
        Integer val = super.get(key);
        if (val != null) {
            //手动将元素移动到链表头部
            super.remove(key);
            super.put(key, value);
            return;
        }

        super.put(key, value);
        //调用put后会使用 removeEldestEntry 控制容量
    }

    public static void main(String[] args) {

        LRUCache146_3 lRUCache = new LRUCache146_3(2);

        lRUCache.put(1, 1); // 缓存是 {1=1}
        lRUCache.put(2, 2); // 缓存是 {1=1, 2=2}
        lRUCache.get(1);    // 返回 1
        lRUCache.put(3, 3); // 该操作会使得关键字 2 作废，缓存是 {1=1, 3=3}
        lRUCache.get(2);    // 返回 -1 (未找到)
        lRUCache.put(4, 4); // 该操作会使得关键字 1 作废，缓存是 {4=4, 3=3}
        lRUCache.get(1);    // 返回 -1 (未找到)
        lRUCache.get(3);    // 返回 3
        lRUCache.get(4);    // 返回 4

    }
}
