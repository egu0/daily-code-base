package im.engure.array;

import java.util.HashSet;
import java.util.Set;

public class RepeatedNTimes961 {
	/**
	 * arr_len [4, 10000]
	 * arr_ele [0, 10000]
	 * 
	 * len=10, 6个不同，5个相同.  即一半相同，一半不同
	 * ==> 1,2,2,2,3,5
	 * ==> 1,1,1,2,3,5
	 * ==> 1,2,3,5,5,5
	 * 
	 * @param nums
	 * @return
	 */
    public int repeatedNTimes(int[] nums) {
    	Set<Integer> s = new HashSet<>();
        for(int i=0; i<nums.length; i++) {
            if (s.contains(nums[i])) {
                return nums[i];
            }
            else {
                s.add(nums[i]);
            }
        }
        return -1;
    }
}
