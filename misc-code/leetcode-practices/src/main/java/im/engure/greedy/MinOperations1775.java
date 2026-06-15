package im.engure.greedy;

import im.engure.util.MyAssertions;

/**
 * @author Administrator
 */
public class MinOperations1775 {

    public static void main(String[] args) {
        MinOperations1775 o = new MinOperations1775();
        MyAssertions.assertEqual(o.minOperations(new int[]{1, 2, 3, 4, 5, 6}, new int[]{1, 1, 2, 2, 2, 2}), 3);
        MyAssertions.assertEqual(o.minOperations(new int[]{1, 1, 1, 1, 1, 1, 1}, new int[]{6}), -1);
        MyAssertions.assertEqual(o.minOperations(new int[]{6, 6}, new int[]{1}), 3);
        MyAssertions.assertEqual(o.minOperations(new int[]{5, 6, 4, 3, 1, 2}, new int[]{6, 3, 3, 1, 4, 5, 3, 4, 1, 3, 4}), 4);
    }

    /**
     * 贪心算法。4ms
     */
    public int minOperations(int[] nums1, int[] nums2) {
        int sum1 = 0, sum2 = 0;
        int[] cnt1 = new int[7], cnt2 = new int[7];
        for (int j : nums1) {
            cnt1[j]++;
            sum1 += j;
        }
        for (int i : nums2) {
            cnt2[i]++;
            sum2 += i;
        }

        if (nums1.length > nums2.length * 6 || nums2.length > nums1.length * 6) {
            return -1;
        }

        int res = 0;

        while (sum1 != sum2) {
            if (sum1 < sum2) {
                int tmp = sum1;
                sum1 = sum2;
                sum2 = tmp;
                int[] tempArr = cnt1;
                cnt1 = cnt2;
                cnt2 = tempArr;
            }

            // now sum1 > sum2
            int relativeVal = sum1 - sum2;
            // 此时减 sum1 或 加 sum2

            if (sum1 == cnt1[1]) {
                // sum1 不能再减了，要加 sum2
                int i = 1;
                for (; i < 6; i++) {
                    if (cnt2[i] != 0) {
                        int maxAdd = 6 - i;
                        if (maxAdd >= relativeVal) {
                            return res + 1;
                        } else {
                            res++;
                            cnt2[i]--;
                            cnt2[6]++;
                            sum2 += maxAdd;
                        }
                        break;
                    }
                }
            } else if (sum2 == cnt2[6] * 6) {
                // sum2 不能再加了，要减 sum1
                int j = 6;
                for (; j > 1; j--) {
                    if (cnt1[j] != 0) {
                        int maxSub = j - 1;
                        if (maxSub >= relativeVal) {
                            return res + 1;
                        } else {
                            res++;
                            cnt1[j]--;
                            cnt1[1]++;
                            sum1 -= maxSub;
                        }
                        break;
                    }
                }
            } else {
                // sum2 加
                int i = 1;
                for (; i < 6; i++) {
                    if (cnt2[i] != 0) {
                        break;
                    }
                }
                // 此时 i < 6
                int maxAdd = 6 - i;

                // sum1 减
                int j = 6;
                for (; j > 1; j--) {
                    if (cnt1[j] != 0) {
                        break;
                    }
                }
                // 此时 j > 1
                int maxSub = j - 1;

                if (maxAdd >= relativeVal || maxSub >= relativeVal) {
                    return res + 1;
                } else {
                    res++;
                    // 比较二者那个更划算
                    if (maxAdd >= maxSub) {
                        cnt2[i]--;
                        cnt2[6]++;
                        sum2 += maxAdd;
                    } else {
                        cnt1[j]--;
                        cnt1[1]++;
                        sum1 -= maxSub;
                    }
                }
            }
        }

        return res;
    }

}