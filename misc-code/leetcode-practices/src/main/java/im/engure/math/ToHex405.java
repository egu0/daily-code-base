package im.engure.math;

/*
#bitmanipulation
*/

public class ToHex405 {

    static final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    // below `Integer.toHexString(num)`
    public String toHex(int val) {
        int shift = 4;
        int mag = Integer.SIZE - Integer.numberOfLeadingZeros(val);
        int chars = Math.max(((mag + (shift - 1)) / shift), 1);
        byte[] buf = new byte[chars];
        int charPos = chars;
        int radix = 1 << shift;
        int mask = radix - 1;
        do {
            buf[--charPos] = (byte) digits[val & mask];
            val >>>= shift;
        } while (charPos > 0);
        return new String(buf);
    }
}
