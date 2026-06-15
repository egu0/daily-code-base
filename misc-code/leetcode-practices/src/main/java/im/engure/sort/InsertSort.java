package im.engure.sort;

public class InsertSort implements ArrSort {

    public static void main(String[] args) {
        SortChecker.check(new InsertSort());
    }

    public void sort(int[] nums) {
        if (nums == null) return;

        // 依次对 1...n-1 位置排序
        for (int i = 1; i < nums.length; i++) {

            for (int j = i; j > 0; j--) {
                if (nums[j - 1] > nums[j]) {
                    swap(nums, j, j - 1);
                } else {
                    break;
                }
            }

        }

    }

    private void swap(int[] nums, int j, int i) {
        int t = nums[j];
        nums[j] = nums[i];
        nums[i] = t;
    }

}

// 5 2 8 3

// 2 5 8 3
