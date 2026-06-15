package im.engure.testcases;

import java.util.concurrent.ConcurrentHashMap;

public class ConHashMap {
    public static void main(String[] args) {
        //waitAMoment();

    }

    private static void waitAMoment() {
        Integer n1 = 97, n2 = 49, n3 = 33;
        int idx1 = spread(n1.hashCode()) & 15;// x & 15 即 x % 16
        int idx2 = spread(n2.hashCode()) & 15;
        int idx3 = spread(n3.hashCode()) & 15;
        assert idx1 == idx2 && idx1 == idx3 && idx1 == 1;

        //n1,n2,n3 作为 key 时都会得到 table 下标为 1

        ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();
        //初始化 tab[1] 位置
        map.put(n1, 1);

        new Thread(() -> {
            //早于 th2 操作 table[1] 这个链表
            map.put(n2, 1);
            System.out.println("th1 over");
        }, "th1").start();

        new Thread(() -> {
            try {
                //晚于 th1 操作 table[1]
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("th2 开始添加元素");
            map.put(n3, 1);
            System.out.println("th2 结束添加元素");
        }, "th2").start();
    }

    static final int spread(int h) {
        return (h ^ (h >>> 16)) & 0x7fffffff;
    }

}
