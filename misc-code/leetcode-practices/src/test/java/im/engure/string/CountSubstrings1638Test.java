package im.engure.string;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CountSubstrings1638Test {

    @Test
    void t1() {
        CountSubstrings1638 o = new CountSubstrings1638();
        Assertions.assertEquals(6, o.countSubstrings("aba", "baba"));
    }

    @Test
    void t2() {
        CountSubstrings1638 o = new CountSubstrings1638();
        Assertions.assertEquals(3, o.countSubstrings("ab", "bb"));
    }

    @Test
    void t3() {
        CountSubstrings1638 o = new CountSubstrings1638();
        Assertions.assertEquals(10, o.countSubstrings("abe", "bbc"));
    }

    @Test
    void t4() {
        CountSubstrings1638 o = new CountSubstrings1638();
        Assertions.assertEquals(0, o.countSubstrings("a", "a"));
    }
}