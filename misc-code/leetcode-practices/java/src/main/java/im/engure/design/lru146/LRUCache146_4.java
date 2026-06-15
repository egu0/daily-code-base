package im.engure.design.lru146;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache146_4 {

    int cap;
    Map<Integer, Integer> map;

    public LRUCache146_4(int capacity) {
        cap = capacity;
        map = new LinkedHashMap<>(capacity, 0.75F, false);//按照插入顺序进行排序
    }

    public int get(int key) {
        Integer val;
        if ((val = map.get(key)) != null) {
            map.remove(key);
            map.put(key, val);
            return val;
        }
        return -1;
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) {
            map.remove(key);
        }

        if (cap == map.size()) {
            Iterator<Map.Entry<Integer, Integer>> iterator = map.entrySet().iterator();
            iterator.next();
            iterator.remove();//移除头结点
        }

        map.put(key, value);
    }


    public static void main(String[] args) {

        //Map<Integer, Integer> lRUCache = new LinkedHashMap<>(3);
        LRUCache146_4 lRUCache = new LRUCache146_4(2);

        lRUCache.put(1, 1); // 缓存是 {1=1}
        lRUCache.put(2, 2); // 缓存是 {1=1, 2=2}
        lRUCache.get(1);    // 返回 1
        lRUCache.put(3, 3); // 该操作会使得关键字 2 作废，缓存是 {1=1, 3=3}
        lRUCache.get(2);    // 返回 -1 (未找到)
        lRUCache.put(4, 4); // 该操作会使得关键字 1 作废，缓存是 {4=4, 3=3}
        lRUCache.get(1);    // 返回 -1 (未找到)
        lRUCache.get(3);    // 返回 3
        lRUCache.get(4);    // 返回 4

        System.out.println("===============");
        /////////////////////

        /*
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(1, 1);
        map.put(2, 2);
        map.put(3, 3);
        map.put(4, 4);

        map.entrySet().forEach(new Consumer<Map.Entry<Integer, Integer>>() {
            @Override
            public void accept(Map.Entry<Integer, Integer> integerIntegerEntry) {
                String s = integerIntegerEntry.getKey() + "," + integerIntegerEntry.getValue();
                //System.out.println(s);
            }
        });
        */

    }

}
