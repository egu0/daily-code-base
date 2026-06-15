package im.engure.testcases;

public class T3 {
    static Object obj = new Object();

    public static void m1() {
        synchronized (obj) {
            System.out.println("m1");
            m2();
        }
    }

    public static void m2() {
        synchronized (obj) {
            //blablabla...
            System.out.println("m2");
        }
    }


    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(T3::m1, "1");
        t1.start();
        t1.join();//wait thread 1 to die
        new Thread(T3::m2, "2").start();
    }
}
