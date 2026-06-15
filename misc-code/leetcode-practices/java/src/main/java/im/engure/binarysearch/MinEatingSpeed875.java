package im.engure.binarysearch;

public class MinEatingSpeed875 {

	// 速度范围为 [1, max(piles[i]]
	// 速度与时长存在反比关系
	// 使用二分法逐步精确速度
	public static int minEatingSpeed(int[] piles, int h) {
		int maxSpeed = Integer.MIN_VALUE;
		for (int i = 0; i < piles.length; i++) {
			if (piles[i] > maxSpeed) {
				maxSpeed = piles[i];
			}
		}
		// 二分法计算最佳速度左边界
		int l = 1, r = maxSpeed;
		while (l <= r) {
			if (l == r) {
				break;
			}
			// “中间速度”
			int mid = (r - l) / 2 + l;
			// 计算 mid 速度下所需时长
			int t = 0;
			for (int j = 0; j < piles.length; j++) {
				t += (piles[j] - 1) / mid + 1;
			}
			if (t <= h) {
				// 速度适宜或太快
				r = mid;
			} else {
				// t > h，时长大，速度慢
				l = mid + 1;
			}
		}
		return l;
	}

	// O(mn)
	public int m2(int[] piles, int h) {
		int maxSpeed = Integer.MIN_VALUE;
		for (int i = 0; i < piles.length; i++) {
			if (piles[i] > maxSpeed) {
				maxSpeed = piles[i];
			}
		}
		// 计算所有速度下的时长，记录满足时长的最小速度
		int res = Integer.MAX_VALUE;
		for (int i = 1; i <= maxSpeed; i++) {
			int t = 0;
			for (int j = 0; j < piles.length; j++) {
				t += (piles[j] - 1) / i + 1;
			}
			if (t <= h && i < res) {
				res = i;
			}
		}
		return res;
	}

	public static void main(String[] args) {
		System.out.println(minEatingSpeed(new int[] { 3, 6, 7, 11 }, 8));
	}
}
