package im.engure.slidewindow;

/**
 * solid window, binary search
 * 
 * @author engure
 *
 */
public class MinSubArrayLen209 {
	public static void main(String[] args) {
		int[] arr1 = new int[] { 2, 3, 1, 2, 4, 3 };
//		System.out.println(minSubArrayLen(7, arr1) + ", " + m2(7, arr1));
//		System.out.println(minSubArrayLen(6, arr1) + ", " + m2(6, arr1));
		System.out.println(minSubArrayLen(3, arr1) + ", " + m2(3, arr1));
	}

	// solid window
	public static int minSubArrayLen(int target, int[] nums) {
		int len = nums.length;
		int l = 0, r = 0;
		int sum = 0;
		int min = Integer.MAX_VALUE;
		while (r < len) {
			// 增大右边界
			sum += nums[r++];

			// 缩小左边界
			while (sum >= target) {
				if (r - l < min) {
					min = r - l;
				}

				sum -= nums[l++];
			}
		}
		return min == Integer.MAX_VALUE ? 0 : min;
	}

	// 【前缀和 + 二分法】
	// 思路较新，值得多看几遍。
	// 重点：O(nlogn)与O(n)区别，二分区间查找
	public static int m2(int target, int[] nums) {
		if (nums.length == 1) {
			return nums[0] >= target ? 1 : 0;
		}

		// prefix sum array
		// sum_nums[i] = sum_nums[0] + sum_nums[1] +...sum_nums[i - 1]
		int[] sum_nums = new int[nums.length + 1];
		for (int i = 0; i < nums.length; i++) {
			sum_nums[i + 1] = sum_nums[i] + nums[i];
		}
		// 最小区间长度
		int min_length = Integer.MAX_VALUE;
		// e.g.
		// 原数组==> 2,3,1,2,4,3
		// 前缀和==> 0,2,5,6,8,12,15
		for (int i = 0; i < nums.length; i++) {
			int min = target + sum_nums[i];
			// 子区间和 >= target 转化为计算
			// sum_nums[idx] >= target + sum_nums[i], 满足条件的最小 idx
			int idx = targetStartIndexAlgo(sum_nums, min, 0, sum_nums.length - 1);
			if (idx != -1) {
				if (idx - i < min_length) {
					min_length = idx - i;
				}
			}
		}

		return min_length == Integer.MAX_VALUE ? 0 : min_length;
	}

	// 区别于 Arrays.binarySearch()，所有元素都小于 target 时返回 -1
	public static int targetStartIndexAlgo(int[] nums, int target, int l, int r) {

		while (l <= r) {
			int mid = (r - l) / 2 + l;
			if (l == r) { // 使用 while(l < r) 时可以省略这个 if
				break;
			}
			if (nums[mid] < target) {
				l = mid + 1;
			} else {
				r = mid;
			}
		}
		return nums[l] >= target ? l : -1;
	}
}
