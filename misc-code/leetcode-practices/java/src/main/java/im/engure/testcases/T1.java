package im.engure.testcases;

import java.io.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

class A implements Serializable, Cloneable {
    public A() {
        System.out.println("con");
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

interface Action {
    public void fly();//缺省修饰符
}

class Hero implements Action {
    @Override
    public void fly() {

    }
}

public class T1 {
    public static void main(String[] args) {
        //countDownLatchTest();
        //cyclicBarrierTest();
        //joinMethodTest();

        //oisReadObjectTest();
        //cloneObjTest();

    }

    private static void cloneObjTest() {
        A a = new A();
        try {
            Object clone = a.clone();//需重写clone方法、实现Clonable接口
            System.out.println(a.hashCode());
            System.out.println(clone.hashCode());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void oisReadObjectTest() {
        ObjectInputStream is = null;
        try {
            //obj 2 byte[]
            A a = new A();//需实现Serializable
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(a);

            //byte[] 2 obj
            is = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            Object o = is.readObject();//未调用构造函数
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void joinMethodTest() throws InterruptedException {
        Thread subT = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println("binggo~");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        subT.start();
        subT.join();//wait for the call thread to die.
        System.out.println("main~");
    }

    private static void cyclicBarrierTest() {
        int n = 5;
        int m = 5;

        CyclicBarrier cyclicBarrier = new CyclicBarrier(n);

        for (int i = 0; i < m; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 3; j++) {
                        try {
                            String tname = Thread.currentThread().getName();
                            System.out.println(tname + " awaiting..." + "- " + (j + 1));
                            cyclicBarrier.await();
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } catch (BrokenBarrierException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }, String.valueOf(i + 1)).start();
        }
    }

    private static void countDownLatchTest() {
        int n = 5;
        int m = 5;

        // m 和 n 不同会发生什么？

        CountDownLatch latch = new CountDownLatch(m);

        for (int i = 0; i < n; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String tname = Thread.currentThread().getName();
                        System.out.println(tname + " countdown");
                        latch.countDown();

                        Thread.sleep(200);

                        latch.await();
                        System.out.println(tname + " await");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            }, String.valueOf(i + 1)).start();
        }
    }

}