package im.engure.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UseMapDemo {
    public static void main(String[] args) {
        hashmap();
        //concurrenthashmap();

    }

    private static void concurrenthashmap() {
        //key, val 都不可为null
        Map<String, Integer> m = new ConcurrentHashMap<>();

        // key 和 val 都不能为 null
        //m.put(null, 1);
        //m.put("a", null);
        m.put("null", 1);
    }

    private static void hashmap() {
        Map<String, String> m = new HashMap<>();
        m.put("a", "b");
        m.get("a");
        m.containsKey("a");
        m.size();
        m.remove("key");
        m.keySet();// Set<String>
        m.clear();

        // key 和 val 都可为 null
        m.put(null, "1");
        m.put(null, null);
        System.out.println(m.get(null));
    }
}
