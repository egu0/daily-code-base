package im.engure.string;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
#业务题
 */
public class FindDifferentBinaryString1980 {
    public static void main(String[] args) {
        addOneToBinaryStr("111");
        addOneToBinaryStr("011");
        addOneToBinaryStr("001");
    }

    private static void addOneToBinaryStr(String s) {
        byte[] bytes = s.getBytes();
        boolean adder = true;
        for (int i = bytes.length - 1; i >= 0; i--) {
            if (!adder) {
                break;
            }
            if (bytes[i] == '0') {
                bytes[i] = '1';
                adder = false;
            } else {
                bytes[i] = '0';
            }
        }
        String newStr = new String(bytes);
    }

    public String findDifferentBinaryString(String[] nums) {
        Set<String> set = new HashSet<>(List.of(nums));
        String s = nums[0];
        while (true) {
            boolean adder = true;
            byte[] bytes = s.getBytes();
            for (int i = bytes.length - 1; i >= 0; i--) {
                if (!adder) {
                    break;
                }
                if (bytes[i] == '0') {
                    bytes[i] = '1';
                    adder = false;
                } else {
                    bytes[i] = '0';
                }
            }
            String newStr = new String(bytes);
            if (!set.contains(newStr)) {
                return newStr;
            } else {
                s = newStr;
            }
        }
    }
}
