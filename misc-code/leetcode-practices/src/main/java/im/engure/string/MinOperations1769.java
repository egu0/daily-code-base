package im.engure.string;

import java.util.Arrays;

/**
 * @author engure
 */
public class MinOperations1769 {

    public static void main(String[] args) {
        System.out.println(Arrays.toString(new MinOperations1769().minOperations("001011")));
    }

    public int[] minOperations(String boxes) {
        int len = boxes.length();
        char[] chs = boxes.toCharArray();
        int right = 0, left = 0;
        int cnt = 0;
        for (int i = 0; i < len; i++) {
            if (chs[i] == '1') {
                right++;
                cnt += (i + 1);
            }
        }
        int[] res = new int[len];
        for (int i = 0; i < len; i++) {
            res[i] = cnt - right + left;
            if (chs[i] == '1') {
                right--;
                left++;
            }
            cnt = res[i];
        }
        return res;
    }
}
