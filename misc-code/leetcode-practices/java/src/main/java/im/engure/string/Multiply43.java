package im.engure.string;

/**
 * @author engure
 */
public class Multiply43 {

    /**
     * 第二遍，速度提升10倍
     *
     * @param num1
     * @param num2
     * @return
     */
    public String multiply(String num1, String num2) {
        if ("0".equals(num1) || "0".equals(num2)) {
            return "0";
        }
        int[] arr = new int[num1.length() + num2.length()];
        int addition, idx = 0, sum;
        for (int i = num2.length() - 1; i >= 0; i--) {
            addition = 0;
            for (int j = num1.length() - 1; j >= 0; j--) {
                idx = num1.length() - 1 - j + num2.length() - 1 - i;
                sum = (num2.charAt(i) - '0') * (num1.charAt(j) - '0') + addition + arr[idx];
                arr[idx] = sum % 10;
                addition = sum / 10;
            }
            if (addition != 0) {
                arr[idx + 1] = addition;
            }
        }
        StringBuilder sb = new StringBuilder();
        boolean start = false;
        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] != 0) {
                start = true;
            }
            if (start) {
                sb.append(arr[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 第一遍。臭长。
     * 1。先计算num2每个数字与num1的乘积结果，记录在数组中
     * 2。遍历1。中得到的乘积结果，两两相加
     * 3。重复2。，得到结果
     *
     * @param num1
     * @param num2
     * @return
     */
    public String multiply1(String num1, String num2) {
        int l1 = num1.length();
        int l2 = num2.length();
        char[] chs1 = num1.toCharArray();
        char[] chs2 = num2.toCharArray();
        for (int i = 0; i < l1; i++) chs1[i] -= '0';
        for (int i = 0; i < l2; i++) chs2[i] -= '0';
        int[][] arr = new int[l2][l1 + 1];
        int mul;
        int addtion;
        int offset;
        for (int i = l2 - 1; i >= 0; i--) {
            addtion = 0;
            for (int j = l1 - 1; j >= 0; j--) {
                mul = chs2[i] * chs1[j] + addtion;
                arr[l2 - 1 - i][j + 1] = mul % 10;
                addtion = (mul >= 10) ? (mul / 10) : 0;
            }
            if (addtion != 0) {
                arr[l2 - 1 - i][0] = addtion;
            }
        }
        String res = "";
        boolean start;
        for (int i = 0; i < l2; i++) {
            StringBuilder sb = new StringBuilder();
            start = false;
            for (int j = 0; j < l1 + 1; j++) {
                if (arr[i][j] != 0) {
                    start = true;
                }
                if (start) {
                    sb.append(arr[i][j]);
                }
            }
            for (int j = 0; j < i && start; j++) {
                sb.append('0');
            }
            res = stringAdd(res, sb.toString());
        }
        return "".equals(res) ? "0" : res;
    }

    private String stringAdd(String n1, String n2) {
        if (n1.length() == 0) return n2;
        if (n2.length() == 0) return n1;

        StringBuilder sb = new StringBuilder();
        int i1 = n1.length() - 1;
        int i2 = n2.length() - 1;
        char[] chs1 = n1.toCharArray();
        char[] chs2 = n2.toCharArray();
        int sum;
        int addition = 0;
        while (i1 >= 0 && i2 >= 0) {
            sum = chs1[i1] - '0' + chs2[i2] - '0' + addition;
            sb.append(sum % 10);
            addition = sum / 10;
            i1--;
            i2--;
        }
        if (i1 < 0) {
            for (int i = i2; i >= 0; i--) {
                sum = chs2[i] - '0' + addition;
                sb.append(sum % 10);
                addition = sum / 10;
            }
        }
        if (i2 < 0) {
            for (int i = i1; i >= 0; i--) {
                sum = chs1[i] - '0' + addition;
                sb.append(sum % 10);
                addition = sum / 10;
            }
        }
        if (addition != 0) {
            sb.append(addition);
        }
        return sb.reverse().toString();
    }


//.   123
//    456
//-----------
//.   738
//.  615
//. 492
//-----------
//. 56088

    public static void main(String[] args) {
        System.out.println(new Multiply43().multiply("456", "123"));
        System.out.println(new Multiply43().multiply("1", "123"));
        System.out.println(new Multiply43().multiply("123", "1"));
        System.out.println(new Multiply43().multiply("3", "3"));
        System.out.println(new Multiply43().multiply("9", "9"));
        System.out.println(new Multiply43().multiply("0", "980"));

        //"121932631112635269"
        System.out.println(new Multiply43().multiply("123456789", "987654321"));

    }
}
