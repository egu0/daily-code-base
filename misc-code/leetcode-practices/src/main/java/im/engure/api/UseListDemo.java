package im.engure.api;

import java.util.ArrayList;
import java.util.LinkedList;

public class UseListDemo {
    public static void main(String[] args) {

        ArrayList<Integer> l1 = new ArrayList<>();
        l1.add(1);
        l1.get(0);
        l1.remove(0);//需要制定index
        l1.size();
        l1.contains(1);
        l1.add(0, 1);

        //双链表。实现了 Set、Deque（继承了Queue） 接口
        LinkedList<Integer> l2 = new LinkedList<>();
        l2.add(1);
        l2.get(0);
        l2.remove(0);
        l2.size();
        l2.contains(1);
        l2.getFirst();      //头部
        l2.getLast();       //尾部
        l2.remove();        //头部
        l2.removeFirst();   //头部
        l2.removeLast();    //尾部
        l2.addFirst(9);  //头部
        l2.addLast(8);   //尾部

    }
}
