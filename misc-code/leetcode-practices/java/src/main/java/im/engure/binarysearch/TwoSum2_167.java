package im.engure.binarysearch;

import java.util.Arrays;

/**
 * 												time complexity				space complexity
 * 1. hash map										N								N
 * 2. binary search									NlogN							1
 * 3. two pointer									N								1
 * 4. two pointer + binary search 					～								1
 * 
 * 
 * two pointer：https://www.youtube.com/watch?v=cQ1Oz4ckceM
 * @author engure
 *
 */
public class TwoSum2_167 {

	public static void main(String[] args) {
		System.out.println(Arrays.toString(twoSum(new int[] { 2, 7, 9, 11 }, 9)));
	}

	// based on binary search
	// O(nlogn)
	public static int[] twoSum(int[] numbers, int target) {
		int len = numbers.length;
		int res;
		for (int i = 0; i < len; i++) {
			res = find(numbers, i + 1, len - 1, target - numbers[i]);
			if (res > 0) {
				return new int[] { i, res };
			}
		}
		return new int[0];
	}

	// binary search
	// return index of target, l<=index<=r
	// -1 - not found
	// 0 - l >= r
	public static int find(int[] nums, int l, int r, int target) {
		if (l >= r)
			return 0;
		int mid;
		while (l <= r) {
			mid = (r + l) / 2;
			if (nums[mid] == target)
				return mid;
			else if (nums[mid] > target)
				r = mid - 1;
			else
				l = mid + 1;
		}
		return -1;
	}
}

class Solution2 {
	public static void main(String[] args) {
		System.out.println(Arrays.toString(twoSum(new int[] { 1, 3, 4, 5, 9, 11, 13 }, 9)));
	}

	// 1,3,4,5,9,11,13
	// t = 9

	/**
	 * two pointer
	 * 
	 * @param numbers length [2, 10000]
	 * @param target
	 * @return
	 */
	public static int[] twoSum(int[] numbers, int target) {
		int i = 0, j = numbers.length - 1;

		while (i < j) {
			int sum = numbers[i] + numbers[j];
			if (sum == target) {
				return new int[] { i + 1, j + 1 };
			} else if (sum > target) {
				j--;
			} else if (sum < target) {
				i++;
			}
		}

		return new int[0];
	}
}

class Solution3 {
	// two pointer + binary search
	public int[] twoSum(int[] numbers, int target) {
		int l = 0;
		int r = numbers.length - 1;
		while (l < r) {
			int sum = numbers[l] + numbers[r];
			if (sum == target) {
				return new int[] { l + 1, r + 1 };
			}
			int mid = l + (r - l) / 2;
			if (sum > target) {
				r = (numbers[l] + numbers[mid]) >= target ? mid : r - 1;
			} else {
				l = (numbers[mid] + numbers[r]) <= target ? mid : l + 1;
			}
		}
		return new int[] { -1, -1 };
	}
}
