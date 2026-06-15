package im.engure.heap;

/**
 * 使用 最大堆 构造优先级队列
 * * 堆的插入、poll 都会自动调整（上浮和下沉）
 */

public class PriorityQueueByMaxHeapDemo {

    public static void main(String[] args) {

        MaxHeapByPriority<Item> maxHeapByPriority = new MaxHeapByPriority<>(10);
        int[] priorities = {1, 8, 3, 2, 1, 10, 4, 20};
        for (int i = 0; i < priorities.length; i++) {
            maxHeapByPriority.add(new Item(priorities[i], priorities[i]));
        }
        while (maxHeapByPriority.size() != 0) {
            System.out.println(maxHeapByPriority.poll());
        }

        /*
            Item{priority=20, val=20}
            Item{priority=10, val=10}
            Item{priority=8, val=8}
            Item{priority=4, val=4}
            Item{priority=3, val=3}
            Item{priority=2, val=2}
            Item{priority=1, val=1}
            Item{priority=1, val=1}
         */

    }

}

class Item extends Element {
    public int val;

    public Item(int val, int priority) {
        super(priority);
        this.val = val;
    }

    @Override
    public String toString() {
        return "Item{" +
                "priority=" + priority +
                ", val=" + val +
                '}';
    }
}
