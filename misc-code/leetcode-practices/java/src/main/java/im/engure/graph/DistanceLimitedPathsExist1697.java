package im.engure.graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * @author Administrator
 */
public class DistanceLimitedPathsExist1697 {

    public static void main(String[] args) {
        //System.out.println(Integer.MAX_VALUE);//2 * 10^9

        DistanceLimitedPathsExist1697 o = new DistanceLimitedPathsExist1697();
        boolean[] bs1 = o.distanceLimitedPathsExist(3, new int[][]{{0, 1, 2}, {1, 2, 4}, {2, 0, 8}, {1, 0, 16}}, new int[][]{{0, 1, 2}, {0, 2, 5}});
        System.out.println(Arrays.toString(bs1));

        boolean[] bs2 = o.distanceLimitedPathsExist(5, new int[][]{{0, 1, 10}, {1, 2, 5}, {2, 3, 9}, {3, 4, 13}}, new int[][]{{0, 4, 14}, {1, 4, 13}});
        System.out.println(Arrays.toString(bs2));

    }

    /**
     * 离线查询 + 并查集
     *
     * @param n
     * @param edgeList
     * @param queries
     * @return
     */
    public boolean[] distanceLimitedPathsExist(int n, int[][] edgeList, int[][] queries) {
        return null;
    }

    /**
     * 基于【dijkstra算法】计算单源通路中单边的最大距离
     * 超时、12/23 通过
     *
     * @param n
     * @param edgeList
     * @param queries
     * @return
     */
    public boolean[] distanceLimitedPathsExist2(int n, int[][] edgeList, int[][] queries) {
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(matrix[i], Integer.MAX_VALUE);
            matrix[i][i] = 0;
        }

        for (int[] edge : edgeList) {
            int p = edge[0];
            int q = edge[1];
            int w = edge[2];
            if (matrix[p][q] > w) {
                matrix[p][q] = w;
                matrix[q][p] = w;
            }
        }

        memo = new HashMap<>();
        boolean[] res = new boolean[queries.length];

        for (int i = 0; i < queries.length; i++) {
            int p = queries[i][0];
            int q = queries[i][1];
            int w = queries[i][2];
            res[i] = distanceFromPtoQ(matrix, p, q) < w;
        }

        return res;
    }

    Map<Integer, int[]> memo;

    private int distanceFromPtoQ(int[][] matrix, int p, int q) {
        if (memo.containsKey(p)) {
            return memo.get(p)[q];
        }
        if (memo.containsKey(q)) {
            return memo.get(q)[p];
        }

        int[] dist = new int[matrix.length];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[p] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((o1, o2) -> o1[1] - o2[1]);
        pq.offer(new int[]{p, 0});
        while (!pq.isEmpty()) {
            int[] arr = pq.poll();
            int x = arr[0], d = arr[1];
            if (d > dist[x]) {
                continue;
            }
            //找 x 的邻接点
            for (int y = 0; y < matrix.length; y++) {
                if (x == y || matrix[x][y] == Integer.MAX_VALUE) {
                    continue;
                }
                int newDist = Math.max(d, matrix[x][y]);
                if (newDist < dist[y]) {
                    dist[y] = newDist;
                    pq.offer(new int[]{y, newDist});
                }
            }
        }

        memo.put(p, dist);
        return dist[q];
    }

    /**
     * 基于 【弗洛伊德算法】 计算点与点之间通路中单边的最大距离
     * 时间 O(N^3), 超时， 12/23 通过
     *
     * @param n
     * @param edgeList
     * @param queries
     * @return
     */
    public boolean[] distanceLimitedPathsExist1(int n, int[][] edgeList, int[][] queries) {

        // n 个节点
        // 邻接矩阵，matrix[i][j] 表示 i 到 j 的距离
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(matrix[i], Integer.MAX_VALUE);
            matrix[i][i] = 0;
        }

        int p1, p2, w;

        for (int[] edge : edgeList) {
            p1 = edge[0];
            p2 = edge[1];
            w = edge[2];
            if (matrix[p1][p2] > w) {
                matrix[p1][p2] = w;
                matrix[p2][p1] = w;
            }
        }

        matrix = process(matrix);

        boolean[] res = new boolean[queries.length];
        for (int i = 0; i < queries.length; i++) {
            p1 = queries[i][0];
            p2 = queries[i][1];
            w = queries[i][2];
            res[i] = matrix[p1][p2] < w;
        }

        return res;
    }

    private int[][] process(int[][] matrix) {

        int n = matrix.length;
        int[][] tmp = new int[n][n];

        for (int i = 0; i < n; i++) {
            // i 作为中转节点
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < j + 1; k++) {
                    if (k == j) {
                        continue;
                    }

                    if (j == i || k == i) {
                        tmp[j][k] = matrix[j][k];
                    } else if (Math.max(matrix[j][i], matrix[i][k]) < matrix[j][k]) {
                        tmp[j][k] = Math.max(matrix[j][i], matrix[i][k]);
                    } else {
                        tmp[j][k] = matrix[j][k];
                    }

                    tmp[k][j] = tmp[j][k];
                }
            }
            matrix = tmp;
        }

        return matrix;
    }
}



