package im.engure.busipro;

import java.util.*;

/**
 * 题目：频率栈
 * 类型：业务题
 * 关键词：优先级队列、hashmap
 *
 * @author Administrator
 */
public class FreqStack895 {
    public static void main(String[] args) {
        FreqStack freqStack = new FreqStack3();
        freqStack.push(7);
        freqStack.push(6);
        freqStack.push(2);
        freqStack.push(6);
        freqStack.push(3);
        freqStack.push(3);
        System.out.println(freqStack.pop());
        freqStack.push(2);
        System.out.println(freqStack.pop());
        freqStack.push(2);
        System.out.println(freqStack.pop());
        freqStack.push(5);
        System.out.println(freqStack.pop());
        freqStack.push(6);
        System.out.println(freqStack.pop());
        System.out.println(freqStack.pop());
        System.out.println(freqStack.pop());
        System.out.println(freqStack.pop());
        System.out.println(freqStack.pop());
        System.out.println(freqStack.pop());
    }
}

class FreqStack3 implements FreqStack {

    PriorityQueue<int[]> priorityQueue;
    Map<Integer, Integer> freq;
    int index = 0;

    public FreqStack3() {
        // o[0]=value, o[1]=freq, o[2]=index, sorted(freq DESC, index ASC)
        priorityQueue = new PriorityQueue<>((o1, o2) -> o1[1] == o2[1] ? (o2[2] - o1[2]) : o2[1] - o1[1]);
        freq = new HashMap<>();
    }

    //O(logN)
    @Override
    public void push(int val) {
        int cnt = freq.getOrDefault(val, 0) + 1;
        freq.put(val, cnt);
        priorityQueue.offer(new int[]{val, cnt, index++});
    }

    //O(1)
    @Override
    public int pop() {
        int[] ele = priorityQueue.poll();
        freq.put(ele[0], ele[1] - 1);
        return ele[0];
    }
}

/**
 * <a href="https://leetcode.cn/problems/maximum-frequency-stack/solution/zui-da-pin-lu-zhan-by-leetcode-solution-moay/">...</a>
 * - 记录每个频率下的元素序列和当前最大元素频率maxFreq
 * - push: val的频率加1为cnt，将val放在group.get(cnt)的队列中
 * - pop: 返回 group.get(maxFreq).removeFirst()
 */
class FreqStack2 implements FreqStack {
    //元素频率
    Map<Integer, Integer> freq = new HashMap<>();

    //根据元素出现的频率进行存储
    Map<Integer, Deque<Integer>> group = new HashMap<>();

    //当前出现最多的元素出现的次数
    int maxFreq = 0;

    public FreqStack2() {
    }

    @Override
    public void push(int val) {
        //当前出现的次数
        int cnt = 1 + freq.getOrDefault(val, 0);

        //将val放入group[cnt]
        ArrayDeque<Integer> emptyDeque = new ArrayDeque<>();
        Deque<Integer> deque = group.getOrDefault(cnt, emptyDeque);
        deque.addFirst(val);
        if (deque.size() == 1) {
            group.put(cnt, deque);
        }

        freq.put(val, cnt);
        maxFreq = Math.max(maxFreq, cnt);
    }

    @Override
    public int pop() {
        Integer val = group.get(maxFreq).removeFirst();
        if (group.get(maxFreq).size() == 0) {
            maxFreq--;
        }
        freq.put(val, freq.get(val) - 1);
        return val;
    }
}

/**
 * hashmap + priorityQueue
 * 总结：
 * - priorityQueue的remove方法通过 == 判断相等以进行删除的。如果要删除则需要用迭代器找出手动后再remove
 */
class FreqStack1 implements FreqStack {

    int[] stack = new int[20001];
    int ptr = 0;
    Map<Integer, Integer> map = new HashMap<>();
    PriorityQueue<Pair> priorityQueue = new PriorityQueue<>((o1, o2) -> o2.cnt - o1.cnt);

    public FreqStack1() {
    }

    /**
     * O(logN)
     *
     * @param val
     */
    public void push(int val) {
        // add to stack
        stack[ptr++] = val;

        // update times of element
        Integer cnt = map.getOrDefault(val, 0);
        map.put(val, cnt + 1);

        // update occurrence of element
        Pair pair = new Pair(val, cnt + 1);
        if (cnt != 0) {
            removeFromPriorityQueue(val);
        }

        priorityQueue.add(pair);
    }

    void removeFromPriorityQueue(int val) {
        Iterator<Pair> iterator = priorityQueue.iterator();
        Pair tmp = null;
        while (iterator.hasNext()) {
            tmp = iterator.next();
            if (val == tmp.val) {
                break;
            }
        }
        priorityQueue.remove(tmp);
    }

    /**
     * O(N)
     *
     * @return
     */
    public int pop() {
        // get max cnt of all elements
        int maxCnt = priorityQueue.peek().cnt, val = -1;

        // find element whose occurrence time is most
        for (int i = ptr - 1; i >= 0; i--) {
            val = stack[i];
            if (val >= 0 && map.get(val) == maxCnt) {
                removeFromPriorityQueue(val);
                map.put(val, maxCnt - 1);
                if (maxCnt > 1) {
                    priorityQueue.add(new Pair(val, maxCnt - 1));
                }
                stack[i] = -1;
                return val;
            }
        }

        return val;
    }
}

class Pair {
    int val;
    int cnt;

    public Pair(int val, int cnt) {
        this.val = val;
        this.cnt = cnt;
    }

    /**
     * used by contains()/etc..
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair) {
            Pair o = (Pair) obj;
            return o.val == this.val;
        } else {
            return false;
        }
    }
}

interface FreqStack {
    public void push(int val);

    public int pop();
}