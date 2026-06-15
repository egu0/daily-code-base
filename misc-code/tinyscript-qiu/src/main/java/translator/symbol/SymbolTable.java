package translator.symbol;

import java.util.ArrayList;

import lexer.Token;
import lexer.TokenType;

public class SymbolTable {
    private SymbolTable parent;
    private ArrayList<SymbolTable> children;
    private ArrayList<Symbol> symbols;

    //临时变量名后缀
    private int tempIndex = 0;

    // offsetIndex 作用：用于统计【函数参数、定义的变量、临时变量】三者
    private int offsetIndex = 0;

    private int level = 0;

    public SymbolTable() {
        this.children = new ArrayList<>();
        this.symbols = new ArrayList<>();
    }

    public void addSymbol(Symbol symbol) {
        this.symbols.add(symbol);
        symbol.parent = this;
    }

    // 判断一个符号是否在当前【符号表树】中存在
    public boolean exists(Token lexeme) {
        var symbol = this.symbols.stream()
                .filter(x -> x.lexeme.getValue().equals(lexeme.getValue()))
                .findFirst();
        return symbol.isPresent() || (this.parent != null && this.parent.exists(lexeme));
    }

    /*
    从当前作用域向外递归查找目标符号
    ---------------------------------
    let a = 1
    {
        {
            {
                // 需要拷贝 a 这个符号，此时 layoutOffset 为 3。
                // 如果 a 定义在当前作用域，那么 layoutOffset 为 0
                let b = a
            }
        }
    }
     */
    public Symbol cloneFromSymbolTree(Token lexeme, int layoutOffset) {
        var _symbol = this.symbols.stream()
                .filter(x -> x.lexeme.getValue().equals(lexeme.getValue()))
                .findFirst();
        if (_symbol.isPresent()) {
            var symbol = _symbol.get().copy();
            symbol.setLayerOffset(layoutOffset);
            return symbol;
        }

        if (this.parent != null) {
            return this.parent.cloneFromSymbolTree(lexeme, layoutOffset + 1);
        }

        return null;
    }

    public Symbol createSymbolByLexeme(Token lexeme) {
        Symbol symbol = null;
        if (lexeme.isScalar()) {
            symbol = Symbol.createImmediateSymbol(lexeme);
        } else {
            symbol = cloneFromSymbolTree(lexeme, 0);
            if (symbol == null) {
                symbol = Symbol.createAddressSymbol(lexeme, this.offsetIndex++);
            }
        }
        this.symbols.add(symbol);
        return symbol;
    }

    /**
     * 创建临时变量，会同时修改 this.offsetIndex 和 this.tempIndex
     * @return
     */
    public Symbol createVariable() {
        var lexeme = new Token(TokenType.VARIABLE, "p" + this.tempIndex++);
        var symbol = Symbol.createAddressSymbol(lexeme, this.offsetIndex++);//临时变量也是作用域内的变量
        this.symbols.add(symbol);
        return symbol;
    }

    public void addChild(SymbolTable child) {
        child.parent = this;
        child.level = this.level + 1;
        this.children.add(child);
    }

    public int localSize() {
        return this.offsetIndex;
    }

    public ArrayList<Symbol> getSymbols() {
        return this.symbols;
    }

    public ArrayList<SymbolTable> getChildren() {
        return this.children;
    }

    public void createLabel(String label, Token lexeme) {
        var labelSymbol = Symbol.createLabelSymbol(label, lexeme);
        this.addSymbol(labelSymbol);
    }

}
