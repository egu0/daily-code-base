package im.engure.string;

public class GetLucky1945 {

    public static void main(String[] args) {
        GetLucky1945 o = new GetLucky1945();
        System.out.println(o.getLucky("iiii", 1));
        System.out.println(o.getLucky("leetcode", 2));
    }

    public int getLucky(String s, int k) {
        int sum = 0;
        for (int i = 0; i < s.length(); i++) {
            int ch = s.charAt(i) - 96;
            sum += ch % 10 + ch / 10;
        }
        //k>=1
        while (--k > 0) {
            int tmp = 0;
            while (sum != 0) {
                tmp += sum % 10;
                sum /= 10;
            }
            sum = tmp;
        }
        return sum;
    }

    public int getLucky1(String s, int k) {
        StringBuilder tmp = new StringBuilder(s);
        int num = 0;
        while (k-- > 0) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < tmp.length(); i++) {
                char ch = tmp.charAt(i);
                if ('0' <= ch && ch <= '9') {
                    str.append(ch - '0');
                } else {
                    str.append(ch - 'a' + 1);
                }
            }
            num = 0;
            for (int i = 0; i < str.length(); i++) {
                num += str.charAt(i) - '0';
            }
            tmp = new StringBuilder(String.valueOf(num));
        }
        return num;
    }
}
