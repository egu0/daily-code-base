package im.engure.testcases;

import java.util.*;

public class WTFHashMap {
    public static void main(String[] args) throws InterruptedException {
        //resize();
        //concurrentModifyProblem();

        Map<Student, Integer> map = new HashMap<>();
        Student stu = new Student(1, "engure");
        map.put(stu, 1);

        System.out.println(map.get(stu));//1
        stu.age = 2;
        System.out.println(map.get(stu));//null
    }

    static class Student {
        int age;
        String name;

        public Student(int age, String name) {
            this.age = age;
            this.name = name;
        }

        /**
         * 自动生成的 hashCode() 和 equals() 方法
         */

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Student student = (Student) o;
            return age == student.age && Objects.equals(name, student.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(age, name);
        }
    }

    private static void concurrentModifyProblem() throws InterruptedException {
        //构造两个最终放在一个哈希桶的元素
        int n1 = 97, n2 = 49;
        int idx1 = hash(n1) & 15, idx2 = hash(n2) & 15;// x & 15 即 x % 16
        assert idx1 == idx2;

        //并发修改
        HashMap<Integer, Object> m = new HashMap<>();

        Thread th1 = new Thread(() -> {
            m.put(n1, new Object());
        }, "th1");

        Thread th2 = new Thread(() -> {
            m.put(n2, new Object());
        }, "th2");

        th1.start();
        th2.start();
        th1.join();
        th2.join();

        System.out.println(m);
    }

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    private static void resize() {
        //Map<Integer, Integer> m = new HashMap<>();

        List<Integer> list = new ArrayList<>();
        Random rd = new Random();
        for (int i = 0; i < 1000; i++) {
            list.add(rd.nextInt(1000000) * 2 + 1);//odd
            //list.add(rd.nextInt(1000000) * 2);//even
            //list.add(rd.nextInt(1000000));//normal
        }
        int[] cnt = new int[17];
        list.forEach(i -> {
            cnt[i % 17]++;
        });

        System.out.println(Arrays.toString(cnt));
        //分布性出现问题
        //[50, 74, 65, 58, 68, 58, 72, 54, 54, 59, 62, 55, 52, 57, 54, 50, 58]
    }
}
