package im.engure.array;

import java.util.Arrays;

/**
 * <a href="https://en.wikipedia.org/wiki/Median_of_medians">...</a>
 *
 * @author engure
 */
public class BfprtUtil {
    public static void main(String[] args) {
        int[] arr = {5, 4, 3, 2, 1, 6};
        System.out.println(BfprtUtil.bfprt(arr, 0, arr.length, 1));
        System.out.println(BfprtUtil.bfprt(arr, 0, arr.length, 2));
        System.out.println(BfprtUtil.bfprt(arr, 0, arr.length, 3));
        System.out.println(BfprtUtil.bfprt(arr, 0, arr.length, 4));
        System.out.println(BfprtUtil.bfprt(arr, 0, arr.length, 5));
        System.out.println(BfprtUtil.bfprt(arr, 0, arr.length, 6));
        //System.out.println(BfprtUtil.bfprt(arr, 0, arr.length - 1, 7));
    }

    static int bfprt(int[] a, int l, int r, int k) {
        return kthSmallest(a, l, r, k);
    }

    static int kthSmallest(int[] nums, int start, int end, int elm) {
        if (end - start <= 5) {
            Arrays.sort(nums, start, end);
            return nums[start + elm - 1];
        }

        // find the pivot element that will split the
        // set as evenly as possible - this is where all the magic happens
        int pivot = getPivot(nums, start, end);

        // perform a partition based on the pivot found.
        int loc = arrangeByPivot(nums, start, end, pivot);

        // check where is the element we are seeking compared to the partition index
        // call recursively depending on the partitioned location or return, if we have found the element
        if (loc - start == elm - 1) {
            return nums[loc];
        } else if (loc - start > elm - 1) {
            return kthSmallest(nums, start, loc, elm);
        }

        return kthSmallest(nums, loc + 1, end, (elm - (loc + 1 - start)));
    }

    static int getPivot(int[] nums, int start, int end) {
        int[] medians = new int[(end - start) / 5];
        int i = start;
        int k = 0;
        for (int j = start + 5; j < end; i = j, j += 5) {
            Arrays.sort(nums, i, j);
            medians[k++] = nums[i + 2];
        }

        // The magic!!! we are calling our own primary method recursively to find the median of medians.
        return kthSmallest(medians, 0, medians.length, medians.length / 2 + 1);
    }

    static int arrangeByPivot(int[] nums, int start, int end, int pivot) {
        int b = start;
        int e = end - 1;
        for (int i = start; i <= e; ) {
            if (nums[i] < pivot) {
                int temp = nums[i];
                nums[i] = nums[b];
                nums[b] = temp;
                b++;
                i++;
            } else if (nums[i] == pivot) {
                i++;
            } else {
                int temp = nums[i];
                nums[i] = nums[e];
                nums[e] = temp;
                e--;
            }
        }
        return b;
    }

}

/*
python3
---------------------------------------
def median_of_medians(arr, i):
    # divide A into lists of len 5
    lists = [arr[j:j + 5] for j in range(0, len(arr), 5)]
    medians = [sorted(sublist)[len(sublist) // 2] for sublist in lists]
    if len(medians) <= 5:
        pivot = sorted(medians)[len(medians) // 2]
    else:
        # the pivot is the median of the medians
        pivot = median_of_medians(medians, len(medians) / 2)

    # partitioning step
    low = [j for j in arr if j < pivot]
    high = [j for j in arr if j > pivot]

    k = len(low)
    if i < k:
        return median_of_medians(low, i)
    elif i > k:
        return median_of_medians(high, i - k - 1)
    else:  # pivot = k
        return pivot


if __name__ == "__main__":
    # arg 2 start from 0
    print(median_of_medians([1, 2, 3, 4, 5, 6], 0))
 */