package im.engure.graph;

import java.util.*;

public class FindRedundantConnection684 {

    /*
并查集

视频教程：https://www.youtube.com/watch?v=ayW5B2W9hfo

--------------------

【详细解释】

初始化：
- 定义一个数组 parent，下标范围从 1 到 n（注意：题目中的节点通常从 1 开始编号）。
- parent[i] = i 表示每个节点最开始时都是独立的，每个节点的“代表”就是它自己。

遍历每条边：
- 对于每条边 [u, v]：
    a. 判断 u 和 v 是否已经在同一集合中。
- 调用 find(parent, u) 和 find(parent, v) 获取它们各自的代表。
- 如果代表相同，则说明 u 与 v 已经连接过，再连这次就会出现环，直接返回这条边。
    b. 如果 u 和 v 不在同一集合中，那么就把它们合并。调用 union(parent, u, v) 进行合并操作。

find 方法：
- 这个方法的作用是查找节点 x 所在集合的根部代表。
- 递归实现：如果 parent[x] 不是 x，说明 x 不是根节点，就继续查找它的父节点。
- 同时使用了“路径压缩”，也就是在递归的过程中直接把 x 的父节点更新为根节点，这样后续查找更快。

union 方法：
- 这个方法的作用是将两个节点合并到同一个集合。
- 操作步骤：先分别找出 x 和 y 的根节点，然后把 y 所属的集合“挂到” x 的集合上。
- 这里简单地将 find(parent, y) 的结果设置为 find(parent, x)，即把 y 的集合的根变为 x 的根。
     */
    public int[] findRedundantConnection(int[][] edges) {
        int n = edges.length;
        // 定义一个数组 parent 用来记录每个节点的父节点
        int[] parent = new int[n + 1];
        // 初始化，每个节点的父节点初始时都是自己
        for (int i = 1; i <= n; i++) {
            parent[i] = i;
        }

        // 遍历所有边
        for (int[] edge : edges) {
            int u = edge[0], v = edge[1];
            // 通过 find 方法找到 u 和 v 各自的代表（根节点）
            // 如果 u 和 v 的根相同，说明它们已经在同一个连通块中，
            // 再连接这两个点就会形成环，直接返回这条边作为答案
            if (find(parent, u) == find(parent, v)) {
                return edge;
            }
            // 如果不在同一个集合，则把它们合并（union 操作）
            union(parent, u, v);
        }
        return new int[0];
    }

    // find 方法：找到节点 x 所属集合的代表元素（根节点）
    // 这里使用递归，并且做了路径压缩优化，让以后查询更快
    private int find(int[] parent, int x) {
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]); // 路径压缩
        }
        return parent[x];
    }

    // union 方法：将两个节点合并到同一个集合中
    private void union(int[] parent, int x, int y) {
        // 把 y 的集合挂到 x 的集合上（具体做法可以有多种，这里选择简单的方式）
        parent[find(parent, y)] = find(parent, x);
    }


    public int[] findRedundantConnectionV1(int[][] edges) {
        int n = edges.length;
        // 收集「临界点」信息
        Map<Integer, List<Integer>> neighbors = new HashMap<>();
        for (int[] edge : edges) {
            int a = edge[0], b = edge[1];
            if (!neighbors.containsKey(a)) {
                neighbors.put(a, new ArrayList<>());
            }
            if (!neighbors.containsKey(b)) {
                neighbors.put(b, new ArrayList<>());
            }

            neighbors.get(b).add(a);
            neighbors.get(a).add(b);
        }

        // 因为是连接图，所以从任何顶点开始均可以
        int start = edges[0][0];

        Map<Integer, Set<Integer>> visits = new HashMap<>();
        for (Integer p : neighbors.keySet()) {
            visits.put(p, new HashSet<>());
        }

        Set<Integer> tracked = new HashSet<>();
        Stack<Integer> track = new Stack<>();

        dfs(start, visits, track, tracked, neighbors);

        for (int i = n - 1; i >= 0; i--) {
            if (membersOfCircle.contains(edges[i][0]) && membersOfCircle.contains(edges[i][1])) {
                return edges[i];
            }
        }

        return null;
    }

    boolean meet = false;
    Set<Integer> membersOfCircle = new HashSet<>();

    private void dfs(int cur, Map<Integer, Set<Integer>> visits, Stack<Integer> track,
                     Set<Integer> tracked, Map<Integer, List<Integer>> neighbors) {

        if (tracked.contains(cur)) {
            // System.out.println("hit circle, current tracked: " + tracked);
            // System.out.println("current stack: " + track);
            while (track.peek() != cur) {
                membersOfCircle.add(track.pop());
            }
            membersOfCircle.add(cur);
            meet = true;
            return;
        }

        //System.out.println("visit " + cur);
        tracked.add(cur);
        track.push(cur);
        Set<Integer> visitedOnesOfCur = visits.get(cur);

        for (Integer near : neighbors.get(cur)) {
            if (meet) {
                return;
            }
            if (!visitedOnesOfCur.contains(near)) {
                visitedOnesOfCur.add(near);
                visits.get(near).add(cur);
                dfs(near, visits, track, tracked, neighbors);
            }
        }

        tracked.remove(cur);
        if (!track.isEmpty()) {
            track.pop();
        }
    }
}
