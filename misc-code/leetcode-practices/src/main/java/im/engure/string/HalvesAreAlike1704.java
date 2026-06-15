package im.engure.string;

/**
 * @author engure
 */
public class HalvesAreAlike1704 {
    static boolean[] yy = new boolean[100];

    static {
        yy['A' - 'A'] = true;//A, 65
        yy['A' - 'A' + 0x20] = true;//a, 97
        yy['E' - 'A'] = true;
        yy['E' - 'A' + 0x20] = true;
        yy['I' - 'A'] = true;
        yy['I' - 'A' + 0x20] = true;
        yy['O' - 'A'] = true;
        yy['O' - 'A' + 0x20] = true;
        yy['U' - 'A'] = true;
        yy['U' - 'A' + 0x20] = true;
    }//97,65, 32

    public boolean halvesAreAlike(String s) {
        int n1 = 0, n2 = 0, len = s.length();
        for (int i = 0; i < len / 2; i++) {
            if (yy[s.charAt(i) - 65]) n1++;
        }
        for (int i = len / 2; i < len; i++) {
            if (yy[s.charAt(i) - 65]) n2++;
            if (n2 > n1) {
                return false;
            }
        }
        return n1 == n2;
    }

    public static void main(String[] args) {
        System.out.println(new HalvesAreAlike1704().halvesAreAlike("bk"));
    }
}
