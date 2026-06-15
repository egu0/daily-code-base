package com.craftinginterpreters;

import com.craftinginterpreters.lox.Lox;
import org.junit.jupiter.api.Test;

public class ClosureTests {
  @Test
  public void testClosure1() {
    Lox.run("""
            fun makeCounter() {
              var i = 0;
              fun count() {
                i = i + 1;
                print i;
              }
              return count;
            }
            
            var counter = makeCounter();
            counter(); // "1".
            counter(); // "2".
            """);
  }
}
