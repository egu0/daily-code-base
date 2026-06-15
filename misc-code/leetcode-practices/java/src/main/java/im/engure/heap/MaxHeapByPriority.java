package im.engure.heap;

import java.util.Arrays;

/*
基于优先级的最大堆
仿写 MaxHeap，添加优先级属性 Element.priority
 */

class Element {
    public int priority;

    public Element(int priority) {
        this.priority = priority;
    }

}

public class MaxHeapByPriority<T extends Element> {
    int size = 0;
    int len;
    Element[] arr;

    public MaxHeapByPriority(int len) {
        if (len < 1) throw new RuntimeException("illegal length");
        this.len = len;
        arr = new Element[len];//父类型，Item -> Element
    }

    public int size() {
        return size;
    }

    /*
    取堆顶并删除，O(logN)
     */
    public Element poll() {
        if (size == 0) throw new RuntimeException("empty heap");
        Element ele = arr[0];
        arr[0] = arr[size - 1];
        arr[--size] = null;
        heapifyDown();
        return ele;
    }

    /*
    下沉
     */
    private void heapifyDown() {
        int index = 0;//从新堆顶开始
        Element largerChild, tmp;
        int newIndex;
        while (getLeftChildIndex(index) < size) {//如果有左孩子
            newIndex = getLeftChildIndex(index);
            largerChild = getLeftChild(index);
            //如果有右孩子
            if (getRightChildIndex(index) < size && getRightChild(index).priority > largerChild.priority) {
                largerChild = getRightChild(index);
                newIndex = getRightChildIndex(index);
            }

            //有大孩子
            if (largerChild.priority > arr[index].priority) {
                tmp = arr[index];
                arr[index] = largerChild;
                arr[newIndex] = tmp;
                index = newIndex;
            } else break;//没有大孩子则停止

        }
    }


    /*
    堆顶，O(1)
    */
    public Element peek() {
        if (size == 0) throw new RuntimeException("empty heap");
        return arr[0];
    }

    /*
    入堆，O(logN)
     */
    public void add(T item) {
        if (size == len) {
            arr = Arrays.copyOf(arr, len * 2);
            len *= 2;
        }
        arr[size++] = item;
        heapifyUp(size - 1);
    }

    /*
    上浮
     */
    private void heapifyUp(int index) {
        Element tmp;
        while (index != 0 && getParent(index).priority < arr[index].priority) {
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

    private Element getLeftChild(int index) {
        return arr[getLeftChildIndex(index)];//exp
    }

    private Element getRightChild(int index) {
        return arr[getRightChildIndex(index)];//exp
    }

    private Element getParent(int index) {
        return arr[getParentIndex(index)];
    }

}


