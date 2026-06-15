package im.engure.array;

public class NumOfSubarrays1343 {

    // int res = new NumOfSubarrays1343().numOfSubarrays(new int[]{2, 2, 2, 2, 5, 5, 5, 8}, 3, 4);
    // Assertions.assertEquals(3, res);
    public int numOfSubarrays(int[] arr, int k, int threshold) {
        int res = 0, sum = 0, i = 0, n = arr.length, threeThreshold = threshold * k;
        while (i < k) {
            sum += arr[i++];
        }
        if (sum >= threeThreshold) {
            res++;
        }
        while (i < n) {
            sum -= arr[i - k];
            sum += arr[i++];
            if (sum >= threeThreshold) {
                res++;
            }
        }
        return res;
    }
}

// sum(a[i], a[i+1],, a[i+k-1]) / k >= threshold
