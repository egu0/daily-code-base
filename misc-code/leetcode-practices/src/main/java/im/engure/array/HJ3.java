package im.engure.array;

import java.util.*;

/*
明明生成了NN个1到500之间的随机整数。请你删去其中重复的数字，即相同的数字只保留一个，把其余相同的数去掉，
   然后再把这些数从小到大排序，按照排好的顺序输出。

数据范围： 1≤n≤1000  ，输入的数字大小满足 1≤val≤500
 */
public class HJ3 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        Integer[] arr = new Integer[1001];
        while (n-- > 0) {
            arr[in.nextInt()] = 1;
        }
        for (int i = 0; i < 1001; i++) {
            if (arr[i] != null) {
                System.out.println(i);
            }
        }
    }

    /*
    原始做法
     */
    public static void main2(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int[] arr = new int[n];
        List<Integer> lst = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < n; i++) {
            arr[i] = in.nextInt();
            if (!set.contains(arr[i])) {
                lst.add(arr[i]);
                set.add(arr[i]);
            }
        }
        int[] rst = new int[lst.size()];
        for (int i = 0; i < rst.length; i++) {
            rst[i] = lst.get(i);
        }
        Arrays.sort(rst);
        for (int j : rst) {
            System.out.println(j);
        }
    }
}