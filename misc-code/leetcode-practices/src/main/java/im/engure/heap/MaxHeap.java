package im.engure.heap;

import java.util.Arrays;

/*
可视化演示：https://visualgo.net/zh/heap
学习来源：https://www.bilibili.com/video/BV1ti4y1879c
 */

/**
 * @author Administrator
 */
public class MaxHeap {

    /**
     * 堆排序：将所有元素插入一个堆中，不断取出堆顶元素
     */
    public static void main(String[] args) {
        int[] arr = {-1, 8, 2, 100, 30, 13, 66};
        MaxHeap maxHeap = new MaxHeap(arr.length);
        for (int e : arr) {
            maxHeap.add(e);
        }
        while (maxHeap.size() != 0) {
            System.out.println(maxHeap.poll() + " ");
        }

    }

    int size = 0;
    int len;
    int[] arr;

    public MaxHeap(int len) {
        if (len < 1) {
            throw new RuntimeException("illegal length");
        }
        this.len = len;
        arr = new int[len];
    }

    public int size() {
        return size;
    }

    /**
     * 取堆顶并删除，O(logN)
     */
    public int poll() {
        if (size == 0) {
            throw new RuntimeException("empty heap");
        }
        int ele = arr[0];
        arr[0] = arr[size - 1];
        arr[--size] = 0;
        heapifyDown();
        return ele;
    }

    /**
     * 下沉 O(logN)
     */
    private void heapifyDown() {
        //从新堆顶开始
        int index = 0;
        int largerChild, newIndex, tmp;

        //如果有左孩子
        while (getLeftChildIndex(index) < size) {
            newIndex = getLeftChildIndex(index);
            largerChild = getLeftChild(index);
            //如果有右孩子
            if (getRightChildIndex(index) < size && getRightChild(index) > largerChild) {
                largerChild = getRightChild(index);
                newIndex = getRightChildIndex(index);
            }

            //有大孩子
            if (largerChild > arr[index]) {
                tmp = arr[index];
                arr[index] = largerChild;
                arr[newIndex] = tmp;
                index = newIndex;
            } else {
                break;//没有大孩子则停止
            }

        }
    }

    /**
     * 堆顶，O(1)
     */
    public int peek() {
        if (size == 0) {
            throw new RuntimeException("empty heap");
        }
        return arr[0];
    }

    /**
     * 入堆，O(logN)
     */
    public void add(int val) {
        if (size == len) {
            arr = Arrays.copyOf(arr, len * 2);
            len *= 2;
        }
        arr[size++] = val;
        heapifyUp(size - 1);
    }

    /**
     * 上浮, O(logN)
     */
    private void heapifyUp(int index) {
        int tmp;
        while (index != 0 && getParent(index) < arr[index]) {
            tmp = arr[index];
            arr[index] = getParent(index);
            arr[getParentIndex(index)] = tmp;
            index = getParentIndex(index);
        }
    }

    private int getLeftChildIndex(int index) {
        return index * 2 + 1;
    }

    private int getRightChildIndex(int index) {
        return index * 2 + 2;
    }

    private int getParentIndex(int index) {
        return (index - 1) / 2;
    }

    private int getLeftChild(int index) {
        return arr[getLeftChildIndex(index)];
    }

    private int getRightChild(int index) {
        return arr[getRightChildIndex(index)];
    }

    private int getParent(int index) {
        return arr[getParentIndex(index)];
    }

}
