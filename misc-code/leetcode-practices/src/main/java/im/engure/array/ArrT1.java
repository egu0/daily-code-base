package im.engure.array;

/*

请构造一个长度为N的数组，其中的 i,k,j位置满足 i<k<j, arr[i] + arr[j] != 2 * arr[k]
https://www.bilibili.com/video/BV1t5411N7NH?p=2

 */

public class ArrT1 {

    public static int[] solve(int len) {
        if (len == 1) return new int[]{1};

        int half = (len + 1) / 2;
        int[] ret = solve(half);
        int[] res = new int[len];

        int index = 0;
        for (int i = 0; i < half; i++) {
            res[i] = ret[index++] * 2 - 1;
        }
        index = 0;
        for (int i = half; i < len; i++) {
            res[i] = ret[index++] * 2;
        }

        //System.out.println(len + " ==> " + Arrays.toString(res));
        return res;
    }

    public static void main(String[] args) {
        solve(7);
    }

    ///////////////////////////////////////////////////////////

    // i<k<j, arr[i] + arr[j] != 2 * arr[k]
    public static int[] process(int len) {

        if (len == 1) return null;

        // 3,5,7,11,13,17,19,23,29,31,37,41       质数序列不行

        return null;
    }


}
