package com.craftinginterpreters;

import com.craftinginterpreters.lox.Lox;
import org.junit.jupiter.api.Test;

public class FunctionTests {
  @Test
  public void nativeFunction() {
    Lox.run("""
            print clock;
            print clock();
            """);
  }

  @Test
  public void recursiveFunction() {
    // steps:
    // 1. parse `count` function      -->   obtain `Stmt.Function` instance
    // 2. parse `count(3)` calling    -->   obtain `Stmt.Expression` instance
    // 3. interpret `Stmt.Function`   -->   add `LoxFunction` instance to `globals` environment
    // 4. interpret `Stmt.Expression` -->   interpret `Expr.Call`, then interpret function body from a new env
    //            4.1. interpret `Stmt.If`
    //                      4.1.1 interpret if statement's block
    // ...
    Lox.run("""
            fun count(n) {
              if (n > 1) count(n - 1);
              print n;
            }
            
            count(3);
            """);
  }

  @Test
  public void commonFunction() {
    Lox.run("""
            fun add(a, b) {
              print a + b;
            }
            
            print add;
            add(1, 2);
            """);
  }

  @Test
  public void commonFunction2() {
    Lox.run("""
            fun sayHi(first, last) {
              print "Hi, " + first + " " + last + "!";
            }
            
            sayHi("Dear", "Reader");
            """);
  }

  @Test
  public void returnKeyword1() {
    Lox.run("""
            fun count(n) {
              while (n < 100) {
                if (n == 3) return n; // <--
                print n;
                n = n + 1;
              }
            }
            
            count(1);
            """);
  }

  @Test
  public void returnKeyword2() {
    Lox.run("""
            fun fib(n) {
              if (n <= 1) return n;
              return fib(n - 2) + fib(n - 1);
            }
            
            for (var i = 0; i < 20; i = i + 1) {
              print fib(i);
            }
            """);
  }
}
