package im.engure.array;

/**
 * 计算两个升序数组合并后的升序数组的中位数
 */

public class FindMedianSortedArrays4 {
}

class Solution4 {
    /**
     * O(m+n)的复杂度
     *
     * @param nums1
     * @param nums2
     * @return
     */
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int l1 = nums1.length;
        int l2 = nums2.length;
        int sum = l1 + l2;
        if (sum == 1) return l1 == 0 ? nums2[0] : nums1[0];
        int[] arr = new int[sum];
        //双指针，共走 m+n 长度，合并数组
        int i = 0, j = 0, index = 0;
        while (true) {
            if (i >= l1 || j >= l2) break;
            if (i == l1) {
                while (j < l2) arr[index++] = nums2[j++];
                break;
            }
            if (j == l2) {
                while (i < l1) arr[index++] = nums1[i++];
            }

            if (nums1[i] > nums2[j]) arr[index++] = nums2[j++];
            else arr[index++] = nums1[i++];
        }
        //找出arr的中位数
        int mid = sum / 2;
        if (sum % 2 == 0) return (arr[mid] + arr[mid - 1] + 0.0) / 2.0;
        else return arr[mid];
    }

}

class Solution4_2 {

    /**
     * O(log(m+n))
     *
     * @param nums1
     * @param nums2
     * @return
     */

    public double findMedianSortedArrays(int[] nums1, int[] nums2) {

        return 0.0;
    }

}

/**
 * 二分查找：
 * * 第一次检索区间长度 N/1
 * * 第二次检索区间长度 N/2
 * * 第三次检索区间长度 N/4
 * * ...
 * * 第K次检索区间长度 N / (2 ^ (k - 1))，最坏情况下剩余 1 才找到
 * <p>
 * N = 2 ^ (k -1)
 * logN = (k-1)log2
 * logN = k
 */
