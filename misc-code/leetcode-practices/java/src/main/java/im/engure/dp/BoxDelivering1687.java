package im.engure.dp;

import java.util.HashMap;
import java.util.Map;

public class BoxDelivering1687 {

    public static void main(String[] args) {
        BoxDelivering1687 o = new BoxDelivering1687();
        assert o.boxDelivering(new int[][]{{1, 1}, {2, 1}, {1, 1}}, 2, 3, 3) == 4;
        assert o.boxDelivering(new int[][]{{1, 2}, {3, 3}, {3, 1}, {3, 1}, {2, 4}}, 3, 3, 6) == 6;
        assert o.boxDelivering(new int[][]{{1, 4}, {1, 2}, {2, 1}, {2, 1}, {3, 2}, {3, 4}}, 3, 6, 7) == 6;
        assert o.boxDelivering(new int[][]{{2, 4}, {2, 5}, {3, 1}, {3, 2}, {3, 7}, {3, 1}, {4, 4}, {1, 3}, {5, 2}}, 3, 6, 7) == 14;
    }

    int[][] boxes;
    int portsCnt, maxBoxes, maxWeight;

    public int boxDelivering(int[][] boxes, int portsCount, int maxBoxes, int maxWeight) {
        this.boxes = boxes;
        this.portsCnt = portsCount;
        this.maxBoxes = maxBoxes;
        this.maxWeight = maxWeight;
        return minCostFrom(0);
    }

    Map<Integer, Integer> note = new HashMap<>();

    /**
     * 超时、35/39 通过
     *
     * @param idx
     * @return
     */
    private int minCostFrom(int idx) {
        if (idx == boxes.length) return 0;

        if (note.containsKey(idx)) {
            return note.get(idx);
        }

        int totalWeight = 0;
        int totalCnt = 0;
        int lastPortNum = 0;
        int totTimes = 1;
        int minDeliveringDist = Integer.MAX_VALUE;
        for (int i = idx; i < boxes.length; i++) {
            totalCnt++;
            totalWeight += boxes[i][1];

            //装不下时（第一次一定不走这个语句，所以 minDeliveringDist 最终一定不为 MAX_VALUE）
            if (totalCnt > this.maxBoxes || totalWeight > this.maxWeight) {
                break;
            }

            //累计行程
            if (boxes[i][0] != lastPortNum) {
                totTimes++;
                lastPortNum = boxes[i][0];
            }

            //计算装 [i+1,,,) 货物时最小行程（已知卡车至少能装一个箱子，所以 nexVal 至少为 2）
            int nextVal = minCostFrom(i + 1);

            minDeliveringDist = Math.min(totTimes + nextVal, minDeliveringDist);
        }

        note.put(idx, minDeliveringDist);
        return minDeliveringDist;
    }
}
