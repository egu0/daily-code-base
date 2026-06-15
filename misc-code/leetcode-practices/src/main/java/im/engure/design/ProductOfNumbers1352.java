package im.engure.design;

import java.util.*;

/**
 * #业务题
 */
public class ProductOfNumbers1352 {

    /**
     * 【前缀积】法
     */
    public static class ProductOfNumbers {
        // 元素
        List<Integer> list = new ArrayList<>(10000);
        // 元素的前缀积
        List<Integer> prefixProductList = new ArrayList<>(10000);
        // 记录 0 所在的索引
        List<Integer> zeroIndexes = new ArrayList<>();
        // 前缀积
        int prefixProduct = 1;

        public ProductOfNumbers() {
        }

        public void add(int num) {
            if (num == 0) {
                zeroIndexes.add(list.size());
                prefixProduct = 1;
            } else {
                prefixProduct *= num;
            }
            prefixProductList.add(prefixProduct);
            list.add(num);
        }

        public int getProduct(int k) {
            int n = list.size();
            int idx = n - k;
            Optional<Integer> first = zeroIndexes.stream().filter(zeroIdx -> zeroIdx >= idx).findFirst();
            if (first.isPresent()) {
                return 0;
            }
            return prefixProductList.get(n - 1) / (idx < 1 ? 1 : prefixProductList.get(idx - 1));
        }
    }

    /*
        缓存法。时间 O(N)，超时
     */
    static class ProductOfNumbersV1 {
        ArrayList<Integer> lst;
        Map<Integer, Integer> cachedKV;
        boolean dirty = false;

        public ProductOfNumbersV1() {
            lst = new ArrayList<>();
            cachedKV = new HashMap<>();
        }

        public void add(int num) {
            lst.add(num);
            dirty = true;
        }

        public int getProduct(int k) {
            if (dirty) {
                cachedKV.clear();
            }
            dirty = false;

            int res = 1, n = lst.size();
            if (cachedKV.containsKey(n - k)) {
                return cachedKV.get(n - k);
            }

            for (int i = n - k; i < n; i++) {
                if (cachedKV.containsKey(i)) {
                    res *= cachedKV.get(i);
                    cachedKV.put(n - k, res);
                    return res;
                } else {
                    res *= lst.get(i);
                }
            }

            return res;
        }
    }
}


