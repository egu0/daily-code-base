package com.craftinginterpreters;

import com.craftinginterpreters.lox.Lox;
import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;

public class ControlFlowTests {
  @Test
  public void commonForLoop() {
    Lox.run("""
            var a = 0;
            var temp;
            
            for (var b = 1; a < 10000; b = temp + b) {
              print a;
              temp = a;
              a = b;
            }
            """);
  }
}
