package im.engure.testcases;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author engure
 */
public class FailSafeAndFailFast {
    public static void main(String[] args) {

//        failfast();
//        failsafe();

        //failfast2();
        List<Integer> list = new LinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        for (Integer i : list) {
            System.out.println(i);
        }

    }

    private static void failfast2() {
        List<Integer> list = new Vector<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        for (Integer i : list) {
            System.out.println(i);
        }
    }

    private static void failsafe() {
        List<Integer> list = new CopyOnWriteArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        for (Integer i : list) {
            System.out.println(i);
        }
    }

    private static void failfast() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        for (Integer i : list) {
            System.out.println(i);
        }
    }
}
