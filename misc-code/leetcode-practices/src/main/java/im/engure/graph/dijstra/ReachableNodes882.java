package im.engure.graph.dijstra;

import java.util.*;

/**
 * 882.细分图中的可到达节点
 *
 * @author Administrator
 * <p>
 * 其他Dijstra题目：
 * - 743. 网络延迟时间
 * - 1976. 到达目的地的方案数
 * - 2203. 得到要求路径的最小带权子图
 */
public class ReachableNodes882 {
    public static void main(String[] args) {
        System.out.println(new ReachableNodes882().reachableNodes(
                new int[][]{{0, 1, 10}, {0, 2, 1}, {1, 2, 2}}, 6, 3
        ));
        System.out.println(new ReachableNodes882().reachableNodes(
                new int[][]{{0, 1, 4}, {1, 2, 6}, {0, 2, 8}, {1, 3, 1}}, 10, 4
        ));
        System.out.println(new ReachableNodes882().reachableNodes(
                new int[][]{{1, 2, 4}, {1, 4, 5}, {1, 3, 1}, {2, 3, 4}, {3, 4, 5}}, 17, 5
        ));

    }

    /**
     * cnti（边可细分的新节点数）可作为边的权值
     * 此题可转化成计算单源最短路径（dijkstra）
     * solution article：<a href="https://leetcode.cn/problems/reachable-nodes-in-subdivided-graph/solution/tu-jie-zhuan-huan-cheng-dan-yuan-zui-dua-6l8o/">...</a>
     */
    @SuppressWarnings("all")
    public int reachableNodes(int[][] edges, int maxMoves, int n) {
        List<int[]>[] g = new ArrayList[n];
        Arrays.setAll(g, e -> new ArrayList<int[]>());
        for (int[] e : edges) {
            int u = e[0], v = e[1], cnt = e[2];
            g[u].add(new int[]{v, cnt + 1});
            g[v].add(new int[]{u, cnt + 1});
        }

        // 从 0 出发的最短路
        int[] dist = dijkstra(g, 0);

        int ans = 0;
        for (int d : dist) {
            // 这个点可以在 maxMoves 步内到达
            if (d <= maxMoves) {
                ++ans;
            }
        }
        for (int[] e : edges) {
            int u = e[0], v = e[1], cnt = e[2];
            // a: 从 u 出发还可以走的步数
            // b: 从 v 出发还可以走的步数
            int a = Math.max(maxMoves - dist[u], 0);
            int b = Math.max(maxMoves - dist[v], 0);
            // 这条边上可以到达的节点数
            if (a + b >= cnt) {
                ans += cnt;
            } else {
                ans += a + b;
            }
        }
        return ans;
    }

    /**
     * Dijkstra 算法模板
     */
    @SuppressWarnings("all")
    private int[] dijkstra(List<int[]>[] g, int start) {
        int[] dist = new int[g.length];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<int[]>((a, b) -> a[1] - b[1]);
        pq.offer(new int[]{start, 0});
        while (!pq.isEmpty()) {
            //取出
            int[] p = pq.poll();
            int x = p[0], d = p[1];
            //如果存在更短的单源距离，则么跳过
            if (d > dist[x]) {
                continue;
            }
            //找 x 的邻接点
            for (int[] e : g[x]) {
                int y = e[0];
                // len[0->x] + len[x->y] ? len[0->y]
                int newDist = d + e[1];
                if (newDist < dist[y]) {
                    dist[y] = newDist;
                    pq.offer(new int[]{y, newDist});
                }
            }
        }
        return dist;
    }

    // region Solution-BruteForceDFS

    /**
     * dfs, 记录每个点和边的覆盖状态。【【超时】】
     */

    Map<Integer, Map<Integer, int[]>> data;
    boolean[] visited;
    boolean[] passed;
    int totalNum;

    @SuppressWarnings("all")
    public int reachableNodes2(int[][] edges, int maxMoves, int n) {
        //ready
        data = new HashMap<>();
        visited = new boolean[n];
        passed = new boolean[n];
        totalNum = n;
        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            int l = edge[2];
            Map<Integer, int[]> uVal = data.getOrDefault(u, new HashMap<>());
            // [0]:edge length, [1]:edge max occupy, [2]:if the edge passed already
            uVal.put(v, new int[]{l, 0, 0});
            data.put(u, uVal);
            Map<Integer, int[]> vVal = data.getOrDefault(v, new HashMap<>());
            vVal.put(u, new int[]{l, 0, 0});
            data.put(v, vVal);
        }
        //dfs
        startFromN(0, maxMoves + 1);
        //summary
        int ans = 0;
        for (boolean b : passed) {
            if (b) {
                ans++;
            }
        }
        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            int l = edge[2];
            int p1 = data.get(u).get(v)[1];
            int p2 = data.get(v).get(u)[1];
            ans += Math.min(p1 + p2, l);
        }
        return ans;
    }

    public void startFromN(int n, int maxMoves) {
        if (n >= totalNum || visited[n]) {
            return;
        }
        if (maxMoves < 1) {
            return;
        }
        passed[n] = true;
        if (maxMoves == 1) {
            return;
        }

        // maxMove > 1 && n < totalNum
        Map<Integer, int[]> nextPointSet = data.get(n);
        if (nextPointSet == null) {
            return;
        }
        for (Integer p2 : nextPointSet.keySet()) {
            if (visited[p2] && data.get(p2).get(n)[2] == 1) {
                continue;
            }
            int[] nums = nextPointSet.get(p2);
            int l = nums[0];
            if (l + 1 >= maxMoves) {
                int move = maxMoves - 1;
                if (move > nums[1]) {
                    nums[1] = move;
                }
            } else {
                nums[1] = l;
                visited[n] = true;
                nums[2] = 1;
                startFromN(p2, maxMoves - l - 1);
                nums[2] = 0;
                visited[n] = false;
            }
        }
    }

    // endregion

}
