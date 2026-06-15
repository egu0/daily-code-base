package im.engure.unclassified;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import im.engure.priorityqueue.MaxEvents1353;
import im.engure.util.MyDataLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MaxEvents1353Test {

    static MaxEvents1353 o;

    @BeforeAll
    static void setUp() {
        o = new MaxEvents1353();
    }

    @Test
    void maxEvents100000() {
        // 讀取大數組測試用例
        int[][] data = MyDataLoader.loadTwoDimensionsJSONArray("1353.json");

        // 統計運行時間
        TimeInterval timer = DateUtil.timer();
        Assertions.assertEquals(100000, o.maxEvents(data));
        System.out.println(timer.intervalMs());

//        cost about 10s, skip
//        timer.intervalRestart();
//        Assertions.assertEquals(100000, o.maxEventsV1(data));
//        System.out.println(timer.intervalMs());
    }

    @Test
    void maxEvents1() {
        int[][] arr = new int[][]{
                {1, 2},
                {2, 3},
                {3, 4},
                {1, 2}
        };
        Assertions.assertEquals(4, o.maxEvents(arr));
    }

    @Test
    void maxEvents2() {
        int[][] arr = new int[][]{
                {1, 2},
                {2, 3},
                {3, 4},
        };
        Assertions.assertEquals(3, o.maxEvents(arr));
    }

    @Test
    void maxEvents3() {
        int[][] arr = new int[][]{
                {1, 2},
        };
        Assertions.assertEquals(1, o.maxEvents(arr));
    }

    @Test
    void maxEvents4() {
        int[][] arr = new int[][]{
                {1, 2},
                {1, 2},
                {1, 2},
        };
        Assertions.assertEquals(2, o.maxEvents(arr));
    }

    @Test
    void maxEvents5() {
        int[][] arr = new int[][]{{1, 4}, {4, 4}, {2, 2}, {3, 4}, {1, 1}};
        Assertions.assertEquals(4, o.maxEvents(arr));
    }
}