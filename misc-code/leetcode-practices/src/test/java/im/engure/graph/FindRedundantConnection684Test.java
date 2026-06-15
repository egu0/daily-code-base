package im.engure.graph;

import im.engure.util.MyDataLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FindRedundantConnection684Test {

    @Test
    void t1() {
        int[][] graph = new int[][]{{1, 2}, {2, 3}, {3, 4}, {1, 4}, {1, 5}};
        int[] res = new FindRedundantConnection684().findRedundantConnection(graph);
        int[] expected = {1, 4};
        Assertions.assertEquals(expected[0], res[0]);
        Assertions.assertEquals(expected[1], res[1]);
    }

    @Test
    void t1_2() {
        int[][] graph = new int[][]{{1, 2}, {2, 3}, {3, 4}, {1, 4}, {1, 5}};
        int[] res = new im.engure.unionfind.FindRedundantConnection684().findRedundantConnection(graph);
        int[] expected = {1, 4};
        Assertions.assertEquals(expected[0], res[0]);
        Assertions.assertEquals(expected[1], res[1]);
    }

    @Test
    void t2() {
        int[][] graph = new int[][]{{1, 2}, {1, 3}, {2, 3}};
        int[] res = new FindRedundantConnection684().findRedundantConnection(graph);
        int[] expected = {2, 3};
        Assertions.assertEquals(expected[0], res[0]);
        Assertions.assertEquals(expected[1], res[1]);
    }

    @Test
    void t2_2() {
        int[][] graph = new int[][]{{1, 2}, {1, 3}, {2, 3}};
        int[] res = new im.engure.unionfind.FindRedundantConnection684().findRedundantConnection(graph);
        int[] expected = {2, 3};
        Assertions.assertEquals(expected[0], res[0]);
        Assertions.assertEquals(expected[1], res[1]);
    }

    // https://sm.ms/image/5zlbGCFje96WAx1
    @Test
    void t3() {
        int[][] graph = MyDataLoader.loadTwoDimensionsJSONArray("684.json");
        int[] res = new FindRedundantConnection684().findRedundantConnection(graph);
        int[] expected = {5, 48};
        Assertions.assertEquals(expected[0], res[0]);
        Assertions.assertEquals(expected[1], res[1]);
    }

    @Test
    void t3_3() {
        int[][] graph = MyDataLoader.loadTwoDimensionsJSONArray("684.json");
        int[] res = new im.engure.unionfind.FindRedundantConnection684().findRedundantConnection(graph);
        int[] expected = {5, 48};
        Assertions.assertEquals(expected[0], res[0]);
        Assertions.assertEquals(expected[1], res[1]);
    }
}
