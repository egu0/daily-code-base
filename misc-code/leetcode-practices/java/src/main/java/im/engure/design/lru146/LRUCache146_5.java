package im.engure.design.lru146;

import java.util.LinkedHashMap;
import java.util.Map;

/*
linkedhashmap 极简写法：linkedhashmap 的使用

简单题解：https://leetcode-cn.com/problems/lru-cache/solution/lrucachelinkedhashmap-de-jian-dan-shi-yo-jbo1/

---------

创建 linkedhashmap，参数：
* capacity
* loadFactor
* accessOrder，默认false为插入顺序，true为访问顺序（元素排序方式）

cache = new LinkedHashMap<Integer, Integer>(capacity, 0.75f, true) {
        //容量控制，默认返回false，表示不控制
        //如果返回true，那么会移除最旧的元素（按照 accessOrder 规定的顺序结果）
        //控制时机：每次 put 后
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return cache.size() > capacity;
        }
    };

猜想：
* 如果 accessOrder=true，表示按照访问顺序将元素串在链表上（越近访问的越新）
* 如果 accessOrder=false，按照插入顺序将元素串在链表上（越近插入的越新）

 */


public class LRUCache146_5 {
}

class LRUCache1465 {
    int capacity;
    LinkedHashMap<Integer, Integer> cache;

    public LRUCache1465(int capacity) {
        this.capacity = capacity;
        cache = new LinkedHashMap<Integer, Integer>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return cache.size() > capacity;
            }
        };
    }

    public int get(int key) {
        return cache.getOrDefault(key, -1);
    }

    public void put(int key, int value) {
        cache.put(key, value);
    }
}
