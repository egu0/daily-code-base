package im.engure.priorityqueue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class MaxEvents1353 {

    // 時間：O(NLogN)
    // 參考：https://leetcode.cn/problems/maximum-number-of-events-that-can-be-attended/solutions/98224/chun-cui-de-tan-xin-mei-yong-you-xian-dui-lie-dai-
    // 結果：通過
    public int maxEvents(int[][] events) {
        Arrays.sort(events, Comparator.comparingInt(o -> o[0]));
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        int i = 0, res = 0, day = 1, n = events.length;
        while (i < n || !pq.isEmpty()) {
            // 添加當天開始的會議的結束日期到小堆
            while (i < n && events[i][0] == day) {
                pq.offer(events[i++][1]);
            }
            // 去掉已經結束的會議
            while (!pq.isEmpty() && pq.peek() < day) {
                pq.poll();
            }
            // 堆頂是我們要的數據
            if (!pq.isEmpty()) {
                pq.poll();
                res++;
            }
            day++;
        }
        return res;
    }

    static class Pair {
        int l;
        int r;

        Pair(int l, int r) {
            this.l = l;
            this.r = r;
        }

        @Override
        public String toString() {
            return "[" + l + ", " + r + "]";
        }
    }

    // 時間：O(N * M)，N = events.length()，M = max(events[i][1]) - min(events[i][0])
    // 結果：超時
    public int maxEventsV1(int[][] events) {
        int min = 100001;
        int max = 0;
        for (int[] e : events) {
            if (e[0] < min) {
                min = e[0];
            }
            if (e[1] > max) {
                max = e[1];
            }
        }
        //System.out.printf("%d to %d\n", min, max);

        // 第二遍
        int res = 0;
        boolean[] visited = new boolean[events.length];
        for (int i = min; i <= max; i++) {
            //System.out.println("---");
            //System.out.printf("day %d\n", i);
            int tmpRight = max + 1;
            int tempJ = -1;
            for (int j = 0; j < events.length; j++) {
                if (!visited[j] && events[j][0] <= i && i <= events[j][1]) {
                    //System.out.printf("hit event[%d] = %s\n", j, Arrays.toString(events[j]));
                    if (events[j][1] < tmpRight) {
                        tmpRight = events[j][1];
                        tempJ = j;
                    }
                }
            }
            if (tmpRight != max + 1) {
                visited[tempJ] = true;
                //System.out.printf("day %d, choose event [%d] = %s\n", i, tempJ, Arrays.toString(events[tempJ]));
                res++;
            }
        }
        return res;
    }
}
