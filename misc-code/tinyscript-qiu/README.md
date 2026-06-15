# 《程序员三大浪漫：编译原理、操作系统、图形学》

[code](https://github.com/ramroll/romantics)

实现一个简单的编译器，内容：

1. 第四章，词法分析。`lexer`
2. 第五章，语法分析。根据生成式获取抽象语法树 AST。`parser`
3. 第六章，语法制导翻译。根据语法制导定义（SDD，Syntax Directed Definition）将 AST 翻译为三地址指令程序。`traslator`
4. 第七章第一部分，使用代码生成器将三地址指令翻译为机器代码。`gen`
5. 第七章第二部分，实现虚拟机，模拟操作系统执行 4 中得到的程序。`vm`
