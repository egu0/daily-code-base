package im.engure.sort;

public class QuickSort implements ArrSort {

    public static void main(String[] args) {
        SortChecker.check(new QuickSort());
    }

    @Override
    public void sort(int[] nums) {
        quickSort(nums, 0, nums.length - 1);
    }

    /**
     * 新思路：⭐⭐
     * 叶胖：https://www.bilibili.com/video/BV11K4y1b7bC
     * lomuto 方法。将 left...right 区间分为 pivot + 小堆 + 大堆 + 乱堆
     */
    static void quickSort(int[] nums, int left, int right) {

        // base case
        if (left >= right) return;

        // partition · lamuto
        int pivot = nums[left]; // 轴心值
        int lBound = left;  // 指向小堆最后一个元素，等于 left 说明小堆为空
        int rBound = left + 1;

        // rBound 作为探子探索“乱堆”
        while (rBound <= right) {
            if (nums[rBound] >= pivot) {
                // 找到大元素，放入大堆
                rBound++;
            } else {
                // 找到小元素，放入小堆（将该元素与大堆第一个元素进行交换）
                swap(nums, ++lBound, rBound++);
            }
        }
        swap(nums, left, lBound); // 将小堆最后一个元素与 pivot 交换

        // System.out.println("[" + left + ", " + right + "] " + Arrays.toString(nums));

        // recursion
        quickSort(nums, left, lBound);
        quickSort(nums, lBound + 1, right);
    }


    // 图灵学院 https://www.bilibili.com/video/BV1QE41177ST
    // 选取右边界
    static void process(int[] nums, int left, int right) {
        if (left >= right) return;
        int pivot = nums[right]; // 右边界上的值作为“轴心”
        int i = left, j = right - 1;
        while (true) {
            while (i < right && nums[i] <= pivot) i++;
            while (left <= j && nums[j] > pivot) j--;
            if (i > j) break;
            swap(nums, i, j);
        }
        swap(nums, i, right);
        process(nums, left, i - 1);
        process(nums, i, right);
    }

    static void swap(int[] nums, int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }

}
