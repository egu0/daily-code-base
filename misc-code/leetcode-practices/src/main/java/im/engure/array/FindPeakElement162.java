package im.engure.array;

public class FindPeakElement162 {
	public static void main(String[] args) {
		System.out.println(findPeakElement(new int[]{1}));
		System.out.println(findPeakElement(new int[]{1, 2}));
		System.out.println(findPeakElement(new int[]{3, 1, 2}));
		System.out.println(findPeakElement(new int[]{1, 2, 3, 1}));
		System.out.println(findPeakElement(new int[]{1, 2, 1, 3, 5, 6, 4}));
	}
	
	/**
	 * 经审题可知：
	 * 1. 任何位置的元素都与相邻元素不同
	 * 1. 如果 nums[i] > nums[i+1]，则小于 i 的区间是递减的，区间内一定存在峰值
	 * 2. 如果 nums[i] < nums[i+1]，在大于 i 的区间是递增的，区间内一定存在峰值
	 * 
	 * @param nums
	 * @return
	 */
	public static int findPeakElement(int[] nums) {
		if (nums.length == 1) {
			return 0;
		}
        int l = 0, r = nums.length - 1;
        while (l <= r) {
        	if (l + 1 == r) break;
            int mid = (r - l) / 2 + l;
            if (nums[mid - 1] < nums[mid]) {
                l = mid;
            } else {
                r = mid;
            }
        }
        return nums[l] > nums[r] ? l : r;
    }
}
