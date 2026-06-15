package im.engure.sort;

import java.util.Arrays;
import java.util.Random;

/**
 * source：https://www.cnblogs.com/liqingjiang/articles/14687491.html
 *
 * @Desc SortChecker 排序校验器
 * @Author qingjiang.li
 * @Date 2021/4/21
 */
public class SortChecker {

    /**
     * main 方法
     */
    public static void check(ArrSort ISort) {
        //测试50万次
        int totalCount = 500000;

        //是否测试通过
        boolean testOK = true;

        //开始测试
        for (int i = 0; i < totalCount; i++) {
            //随机产生20-50大小的数组，数值大小为0-1000
            int count = new Random().nextInt(30) + 20;
            int[] arr = new int[count];
            for (int j = 0; j < count; j++) {
                arr[j] = new Random().nextInt(1000);
            }

            //具体排序算法
            //Quick.quickSort(arr);
            //SelectSort.selectionSort(arr);
            //InsertSort.insertSort(arr);
            ISort.sort(arr);

            //校验排序结果
            boolean result = SortChecker.checkSortResult(arr);
            if (!result) {
                testOK = false;
                System.out.println("测试失败，失败数据如下：");
                SortChecker.printArray(arr);
                break;
            }
        }

        //打印测试通过结果
        if (testOK) {
            System.out.println("所有测试通过，总数：" + totalCount);
        }

    }

    /**
     * 检查排序结果
     *
     * @param arr 数组
     * @return boolean
     */
    private static boolean checkSortResult(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return true;
        }
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 输出数组
     *
     * @param arr 数组
     */
    private static void printArray(int[] arr) {
        System.out.println(Arrays.toString(arr));
    }


}
