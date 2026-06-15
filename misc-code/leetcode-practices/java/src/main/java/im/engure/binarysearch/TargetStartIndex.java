package im.engure.binarysearch;

/**
 * 非降序数组，返回区间 [l,r] 内 >= target 元素的最小位置
 * 
 * @author engure
 *
 */
public class TargetStartIndex {
	// 二分法 o(log(n))
	public static int targetStartIndexAlgo(int[] nums, int target, int l, int r) {

		while (l <= r) {
			int mid = (r - l) / 2 + l;
			if (l == r) {
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

	public static void main(String[] args) {
		// test cases
		// 5
		System.out.println(targetStartIndexAlgo(new int[] { 1, 3, 4, 5, 6, 7, 8, 11, 19 }, 7, 0, 8));
		// 7
		System.out.println(targetStartIndexAlgo(new int[] { 1, 3, 4, 5, 6, 7, 8, 11, 19 }, 9, 0, 8));
		// 1
		System.out.println(targetStartIndexAlgo(new int[] { 1, 3, 4, 5, 6, 7, 8, 11, 19 }, 2, 0, 8));
		// 0
		System.out.println(targetStartIndexAlgo(new int[] { 1, 3, 4, 5, 6, 7, 8, 11, 19 }, -4, 0, 8));
		// -1
		System.out.println(targetStartIndexAlgo(new int[] { 1, 3, 4, 5, 6, 7, 8, 11, 19 }, 20, 0, 8));
		// 注，以上结果与 Arrays.binarySearch() 结果由所出入
	}
}
