package im.engure.sort;

public class BubbleSort implements ArrSort {

    public static void main(String[] args) {
        SortChecker.check(new BubbleSort());
    }

    @Override
    public void sort(int[] nums) {
        if (nums == null) return;

        for (int i = 0; i < nums.length; i++) {

            for (int j = 1; j < nums.length - i; j++) {
                if (nums[j - 1] > nums[j]) {
                    swap(nums, j, j - 1);
                }
            }

        }

    }

    private void swap(int[] nums, int j, int i) {
        int tmp = nums[j];
        nums[j] = nums[i];
        nums[i] = tmp;
    }
}
