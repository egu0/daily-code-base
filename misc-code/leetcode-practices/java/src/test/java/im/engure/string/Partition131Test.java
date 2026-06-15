package im.engure.string;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class Partition131Test {

    @Test
    void partition1() {
        Partition131 o = new Partition131();
        List<List<String>> expected = List.of(List.of("a", "a"), List.of("aa"));
        List<List<String>> res = o.partition("aa");
        Assertions.assertEquals(expected, res);
    }

    @Test
    void partition2() {
        Partition131 o = new Partition131();
        List<List<String>> expected = List.of(List.of("a", "a", "b"), List.of("aa", "b"));
        List<List<String>> res = o.partition("aab");
        Assertions.assertEquals(expected, res);
    }

    @Test
    void partition3() {
        Partition131 o = new Partition131();
        List<List<String>> expected = List.of(List.of("a"));
        List<List<String>> res = o.partition("a");
        Assertions.assertEquals(expected, res);
    }

    @Test
    void partition4() {
        Partition131 o = new Partition131();
        List<List<String>> expected = List.of(
                List.of("a", "b", "b", "a", "b"),
                List.of("a", "b", "bab"),
                List.of("a", "bb", "a", "b"),
                List.of("abba", "b")
        );
        List<List<String>> res = o.partition("abbab");
        Assertions.assertEquals(expected, res);
    }

    @Test
    void partition5() {
        Partition131 o = new Partition131();
        List<List<String>> expected = List.of(
                List.of("a", "b", "b", "a", "b", "a"),
                List.of("a", "b", "b", "aba"),
                List.of("a", "b", "bab", "a"),
                List.of("a", "bb", "a", "b", "a"),
                List.of("a", "bb", "aba"),
                List.of("abba", "b", "a")
        );
        List<List<String>> res = o.partition("abbaba");
        Assertions.assertEquals(expected, res);
    }
}

