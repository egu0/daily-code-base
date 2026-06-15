package im.engure.greedy;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * @author engure
 */
public class EatenApples1705 {

    public static void main(String[] args) {
        System.out.println(new EatenApples1705().eatenApples2(new int[]{1, 2, 3, 5, 2}, new int[]{3, 2, 1, 4, 2}));
        System.out.println(new EatenApples1705().eatenApples2(new int[]{3, 0, 0, 0, 0, 2}, new int[]{3, 0, 0, 0, 0, 2}));
        System.out.println(new EatenApples1705().eatenApples2(new int[]{3}, new int[]{3}));
        System.out.println(new EatenApples1705().eatenApples2(new int[]{1, 1, 1}, new int[]{1, 1, 1}));
//        System.out.println(new EatenApples1705().eatenApples(new int[]{5, 5, 5, 5, 5}, new int[]{1, 2, 3, 4, 5}));
        System.out.println(new EatenApples1705().eatenApples2(new int[]{0, 4, 2, 0, 1, 0, 5, 6, 6, 6, 1, 5, 0, 1, 3, 2}, new int[]{0, 3, 3, 0, 2, 0, 4, 4, 1, 11, 2, 8, 0, 1, 2, 4}));
        System.out.println(new EatenApples1705().eatenApples2(new int[]{0, 19, 19, 19, 11, 14, 33, 0, 28, 7, 0, 28, 7, 0, 21, 16, 0, 22, 0, 13, 8, 0, 19, 0, 0, 2, 26, 2, 22, 0, 8, 0, 0, 27, 19, 16, 24, 0, 20, 26, 20, 7, 0, 0, 29, 0, 0, 16, 19, 0, 0, 0, 29, 30, 17, 0, 23, 0, 0, 26, 24, 13, 3, 0, 21, 0, 18, 0}, new int[]{0, 5, 1, 16, 7, 10, 54, 0, 40, 2, 0, 23, 4, 0, 20, 18, 0, 40, 0, 22, 8, 0, 35, 0, 0, 3, 24, 1, 8, 0, 10, 0, 0, 2, 38, 8, 4, 0, 36, 33, 14, 9, 0, 0, 56, 0, 0, 21, 27, 0, 0, 0, 14, 20, 18, 0, 42, 0, 0, 44, 3, 8, 3, 0, 10, 0, 27, 0}));
//        System.out.println(new EatenApples1705().eatenApples(new int[]{0, 19, 19, 19, 11, 14, 33, 0, 28, 7, 0, 28, 7, 0, 21, 16, 0, 22, 0, 13, 8, 0, 19, 0, 0, 2, 26, 2, 22, 0, 8, 0, 0, 27, 19, 16, 24, 0, 20, 26, 20, 7, 0, 0, 29, 0, 0, 16, 19, 0, 0, 0, 29, 30, 17, 0, 23, 0, 0, 26, 24, 13, 3, 0, 21, 0, 18, 0}, new int[]{0, 5, 1, 16, 7, 10, 54, 0, 40, 2, 0, 23, 4, 0, 20, 18, 0, 40, 0, 22, 8, 0, 35, 0, 0, 3, 24, 1, 8, 0, 10, 0, 0, 2, 38, 8, 4, 0, 36, 33, 14, 9, 0, 0, 56, 0, 0, 21, 27, 0, 0, 0, 14, 20, 18, 0, 42, 0, 0, 44, 3, 8, 3, 0, 10, 0, 27, 0}));
//        System.out.println(new EatenApples1705().eatenApples1(new int[]{0, 19, 19, 19, 11, 14, 33, 0, 28, 7, 0, 28, 7, 0, 21, 16, 0, 22, 0, 13, 8, 0, 19, 0, 0, 2, 26, 2, 22, 0, 8, 0, 0, 27, 19, 16, 24, 0, 20, 26, 20, 7, 0, 0, 29, 0, 0, 16, 19, 0, 0, 0, 29, 30, 17, 0, 23, 0, 0, 26, 24, 13, 3, 0, 21, 0, 18, 0}, new int[]{0, 5, 1, 16, 7, 10, 54, 0, 40, 2, 0, 23, 4, 0, 20, 18, 0, 40, 0, 22, 8, 0, 35, 0, 0, 3, 24, 1, 8, 0, 10, 0, 0, 2, 38, 8, 4, 0, 36, 33, 14, 9, 0, 0, 56, 0, 0, 21, 27, 0, 0, 0, 14, 20, 18, 0, 42, 0, 0, 44, 3, 8, 3, 0, 10, 0, 27, 0}));
        System.out.println(new EatenApples1705().eatenApples2(new int[]{3}, new int[]{1}));
    }

    public int eatenApples2(int[] apples, int[] days) {

        int ans = 0;
        //这里的权重变量days表示：腐烂的具体日期
        PriorityQueue<Pair> pq = new PriorityQueue<>(Comparator.comparingInt(o -> o.days));

        int i;
        for (i = 0; ; i++) {
            if (i >= apples.length && pq.isEmpty()) {
                break;
            }

            //放苹果
            if (i < apples.length && apples[i] != 0 && days[i] != 0) {
                pq.add(new Pair(days[i] + i, apples[i]));
            }

            //吃苹果
            if (!pq.isEmpty()) {
                Pair pair = pq.peek();
                if (pair.number == 1) {
                    pq.poll();
                } else {
                    pair.number--;
                }
                ans++;
            }

            //循环变量 i 可视为天数，因此可以去除坏苹果
            while (!pq.isEmpty()) {
                int minDays = pq.peek().days;
                if (i + 1 < minDays) {
                    break;
                } else {
                    pq.poll();
                }
            }
        }

        return ans;
    }

    /**
     * 贪心 + 优先队列
     *
     * @param apples
     * @param days
     * @return
     */
    public int eatenApples(int[] apples, int[] days) {

        int ans = 0;
        PriorityQueue<Pair> pq = new PriorityQueue<>(Comparator.comparingInt(o -> o.days));

        for (int i = 0; ; i++) {

            //放苹果
            if (i < apples.length && apples[i] != 0) {
                pq.add(new Pair(days[i], apples[i]));
            }

            //吃苹果
            if (!pq.isEmpty()) {
                Pair pair = pq.peek();
                if (pair.number == 1) {
                    pq.poll();
                } else {
                    pair.number--;
                }
                ans++;
            } else if (i >= apples.length) {
                break;
            }

            //腐烂。这块逻辑复杂度太大
            if (!pq.isEmpty()) {
                PriorityQueue<Pair> tmp = new PriorityQueue<>(Comparator.comparingInt(o -> o.days));
                while (!pq.isEmpty()) {
                    Pair p = pq.poll();
                    if (p.days > 1) {
                        tmp.add(new Pair(p.days - 1, p.number));
                    }
                }
                pq = tmp;
            }
        }

        return ans;
    }

    static class Pair {
        int days;
        int number;

        public Pair(int d, int n) {
            this.days = d;
            this.number = n;
        }
    }

    /**
     * 贪心
     *
     * @param apples
     * @param days
     * @return
     */
    public int eatenApples1(int[] apples, int[] days) {
        int len = 2 * 10000 + 1;
        int res = 0;
        //表示有nums[i]个苹果在i天后腐烂
        int start = Integer.MAX_VALUE, end = Integer.MIN_VALUE;
        short[] nums = new short[len];
        for (int i = 0; ; i++) {
            //放苹果
            if (i < apples.length && apples[i] != 0) {
                nums[days[i]] += apples[i];
                if (days[i] <= start) start = days[i];
                if (days[i] >= end) end = days[i];
            }

            //吃苹果
            boolean ate = false;
            for (int j = start; j <= end; j++) {
                if (nums[j] > 0) {
                    nums[j]--;
                    res++;
                    ate = true;
                    break;
                }
            }
            if (!ate) {
                if (i >= apples.length) break;
                else continue;
            }

            //所有苹果保质期减少一天
            for (int j = start; j <= end; j++) {
                if (j >= 1) {
                    nums[j - 1] = nums[j];
                }
            }
            nums[end] = 0;
            start = (start == 1) ? 1 : (start - 1);
            end = (end == 1) ? 1 : (end - 1);
        }

        return res;
    }
}
