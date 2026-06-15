# 编译器表达式解析

## 一、普拉特解析器

代码目录：`src/main/java/me/`

可以使用的运算符号：`+ - * / ^ ( )`

了解普拉特解析器：[博客](https://zhuanlan.zhihu.com/p/471075848)

## 二、递归下降解析方法

代码目录：`src/main/java/ci240913`

语法规则：

```text
expression     → equality ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           → factor ( ( "-" | "+" ) factor )* ;
factor         → unary ( ( "/" | "*" ) unary )* ;
unary          → ( "!" | "-" ) unary
               | primary ;
primary        → NUMBER | STRING | "true" | "false" | "nil"
               | "(" expression ")" ;
```

[出处](https://craftinginterpreters.com/parsing-expressions.html#recursive-descent-parsing)
