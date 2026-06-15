package translator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lexer.Token;
import lexer.TokenType;
import translator.symbol.SymbolTable;

public class SymbolTests {
    @Test
    public void symbolTable() {
        var symbolTable = new SymbolTable();
        symbolTable.createLabel("L0", new Token(TokenType.VARIABLE, "foo"));
        symbolTable.createVariable();
        symbolTable.createSymbolByLexeme(new Token(TokenType.VARIABLE, "a"));
        // 2 个变量
        assertEquals(2, symbolTable.localSize());
    }

    @Test
    public void chain() {
        var symbolTable = new SymbolTable();
        symbolTable.createSymbolByLexeme(new Token(TokenType.VARIABLE, "a"));

        var childSymbolTable = new SymbolTable();
        symbolTable.addChild(childSymbolTable);

        var childchildSymbolTable = new SymbolTable();
        childSymbolTable.addChild(childchildSymbolTable);

        /*
        let a = 1
        {
            //...
            {
                //...
            }
        }
         */

        assertEquals(true, childSymbolTable.exists(new Token(TokenType.VARIABLE, "a")));
        assertEquals(true, childchildSymbolTable.exists(new Token(TokenType.VARIABLE, "a")));
    }

    @Test
    public void offset() {
        var symbolTable = new SymbolTable();
        symbolTable.createSymbolByLexeme(new Token(TokenType.INTEGER, "100"));
        var symbolA = symbolTable.createSymbolByLexeme(new Token(TokenType.VARIABLE, "a"));
        var symbolB = symbolTable.createSymbolByLexeme(new Token(TokenType.VARIABLE, "b"));

        var childSymbolTable = new SymbolTable();
        symbolTable.addChild(childSymbolTable);
        var anotherSymbolB = childSymbolTable.createSymbolByLexeme(new Token(TokenType.VARIABLE, "b"));
        var symbolC = childSymbolTable.createSymbolByLexeme(new Token(TokenType.VARIABLE, "c"));

        /*
        let a
        let b
        {
            b
            let c
        }
         */

        assertEquals(0, symbolA.getOffset());//父作用域第一个变量
        assertEquals(0, symbolA.getLayerOffset());//不来自其他作用域，所以为 0
        assertEquals(1, symbolB.getOffset());//父作用域第二个变量
        assertEquals(0, symbolB.getLayerOffset());//不来自其他作用域，所以为 0

        assertEquals(1, anotherSymbolB.getOffset());//来自父作用域，实际上为 symbolB.getOffset()
        assertEquals(1, anotherSymbolB.getLayerOffset());//来自父作用域，步长为 1

        assertEquals(0, symbolC.getOffset());//子作用域第一个实际变量
        assertEquals(0, symbolC.getLayerOffset());//不来自其他作用域，所以为 0
    }

}
