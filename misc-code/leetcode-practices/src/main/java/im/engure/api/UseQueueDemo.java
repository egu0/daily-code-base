package im.engure.api;

import java.util.LinkedList;

public class UseQueueDemo {
    public static void main(String[] args) {

        LinkedList<Integer> q = new LinkedList<>();
        q.add(1);           //添加到队列尾部，同 addLast()
        q.addFirst(2);   //添加到队列头部
        q.peek();           //查看队头，不报错
        q.poll();           //移除队头，不报错
        q.remove();         //移除队头，会抛异常，同 removeFirst()
        q.removeLast();     //移除队尾
        q.size();

        /*
        常用：
         - add(e)    入队
         - remove()  队头出队
         - size()    大小
         - peek()    查看队头
         */

    }
}
