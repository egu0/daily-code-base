package im.engure.string;

import java.util.ArrayList;
import java.util.List;

public class Convert6 {
}

/*
80%
 */

class Solution6 {
    public String convert(String s, int numRows) {
        if (numRows == 1) return s;
        List<StringBuilder> l0 = new ArrayList<>();
        for (int i = 0; i < numRows; i++) l0.add(new StringBuilder());

        int i = 0;
        int d = 1;//控制走向，1下楼floor++，-1上楼floor--
        int floor = 0;//0层

        while (true) {
            if (i >= s.length()) break;

            //下到最后一层
            if (d == 1 && floor == numRows - 1) {
                l0.get(floor).append(s.charAt(i));
                d = -1;
            }
            //上到第一层
            else if (d == -1 && floor == 0) {
                l0.get(floor).append(s.charAt(i));
                d = 1;
            } else {
                l0.get(floor).append(s.charAt(i));

            }

            floor += d;
            i++;
        }

        StringBuilder res = new StringBuilder(s.length());
        for (i = 0; i < numRows; i++)
            res.append(l0.get(i).toString());
        return res.toString();
    }
}
