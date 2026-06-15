package im.engure.multithread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 三个线程按序打印 123
 * 我的题解：<a href="https://leetcode.cn/problems/print-in-order/solution/sync-by-engure-gr9t/">...</a>
 * 1. sync - await + notify/notifyAll
 * 2. Lock + Condition
 */
public class Prob1114 {
    public static void main(String[] args) throws InterruptedException {
        Foo foo = new Foo1();
        one(foo);
        two(foo);
        three(foo);
    }

    private static void two(Foo foo) {
        new Thread(() -> {
            try {
                foo.second(() -> System.out.println("2"));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private static void three(Foo foo) {
        new Thread(() -> {
            try {
                foo.third(() -> System.out.println("3"));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private static void one(Foo foo) {
        new Thread(() -> {
            try {
                foo.first(() -> System.out.println("1"));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}

interface Foo {

    public void first(Runnable printFirst) throws InterruptedException;

    public void second(Runnable printSecond) throws InterruptedException;

    public void third(Runnable printThird) throws InterruptedException;
}

class Foo1 implements Foo {
    Lock lock = new ReentrantLock();
    Condition c1, c2;//定向通知

    volatile int state = 1;

    public Foo1() {
        c1 = lock.newCondition();
        c2 = lock.newCondition();
    }

    public void first(Runnable printFirst) throws InterruptedException {
        lock.lock();
        try {
            printFirst.run();
            state = 2;
            c1.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void second(Runnable printSecond) throws InterruptedException {
        lock.lock();
        try {
            if (state != 2) {
                c1.await();
            }
            printSecond.run();
            state = 3;
            c2.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void third(Runnable printThird) throws InterruptedException {
        lock.lock();
        try {
            if (state != 3) {
                c2.await();
            }
            printThird.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

class Foo2 implements Foo {

    CountDownLatch l1 = new CountDownLatch(1);
    CountDownLatch l2 = new CountDownLatch(1);

    public Foo2() {
    }

    @Override
    public void first(Runnable printFirst) throws InterruptedException {
        printFirst.run();
        l1.countDown();
    }

    @Override
    public void second(Runnable printSecond) throws InterruptedException {
        l1.await();
        printSecond.run();
        l2.countDown();
    }

    @Override
    public void third(Runnable printThird) throws InterruptedException {
        l2.await();
        printThird.run();
    }
}
