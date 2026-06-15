package im.engure.string;

public class MaximumValue2496 {
    public int maximumValue(String[] sArr) {
        int max = 0;
        byte b1 = 97, b2 = 122, base = 48;
        for (String s : sArr) {
            boolean string = false;
            byte[] bytes = s.getBytes();

            // 字母
            for (byte b : bytes) {
                if (b1 <= b && b <= b2) {
                    if (s.length() > max) {
                        max = s.length();
                    }
                    string = true;
                    break;
                }
            }

            if (string) {
                continue;
            }

            int sum = 0;
            boolean hit = false;
            int factor = 1;
            for (int i = 0; i < bytes.length; i++) {
                if (!hit && bytes[i] == base) {
                    continue;
                }
                if (!hit) {
                    hit = true;
                    for (int j = i + 1; j < bytes.length; j++) {
                        factor *= 10;
                    }
                }
                sum += factor * (bytes[i] - base);
                factor /= 10;
            }
            if (max < sum) {
                max = sum;
            }
        }
        return max;
    }

}
