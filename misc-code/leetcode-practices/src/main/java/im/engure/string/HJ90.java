package im.engure.string;

import java.util.*;

/*
合法IP
 */
public class HJ90 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (in.hasNext()) {
            String s = in.next();
            boolean ok = false;
            if (s != null && s.length() > 0) {
                String[] ss = s.split("\\.");
                if (ss.length == 4) {
                    try {
                        boolean failed = false;
                        for (int i = 0; i < 4 && !failed; i++) {
                            //纯数字，无符号
                            if (ss[i].indexOf('+') != -1 || ss[i].indexOf('-') != -1) {
                                failed = true;
                            }
                            //无前导零
                            if (ss[i].length() > 1 && ss[i].charAt(0) == '0') {
                                failed = true;
                            }
                            //解析，考虑异常
                            int n1 = Integer.parseInt(ss[i]);
                            if (n1 > 255 || n1 < 0) {
                                failed = true;
                            }
                        }
                        ok = !failed;
                    } catch (Exception ignored) {

                    }
                }
            }
            System.out.println(ok ? "YES" : "NO");
        }
    }
}
