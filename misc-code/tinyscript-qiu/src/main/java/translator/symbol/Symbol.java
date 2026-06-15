package translator.symbol;

import lexer.Token;

public class Symbol {
    SymbolType type;
    // 将三种类型可能用到所有字段列出来
    SymbolTable parent;
    Token lexeme;
    String label;
    int offset;
    int layerOffset = 0;

    public Symbol(SymbolType type) {
        this.type = type;
    }

    public static Symbol createAddressSymbol(Token lexeme, int offset) {
        var symbol = new Symbol(SymbolType.ADDRESS_SYMBOL);
        symbol.lexeme = lexeme;
        symbol.offset = offset;
        return symbol;
    }

    public static Symbol createImmediateSymbol(Token lexeme) {
        var symbol = new Symbol(SymbolType.IMMEDIATE_SYMBOL);
        symbol.lexeme = lexeme;
        return symbol;
    }

    public static Symbol createLabelSymbol(String label, Token lexeme) {
        var symbol = new Symbol(SymbolType.LABEL_SYMBOL);
        symbol.label = label;
        symbol.lexeme = lexeme;
        return symbol;
    }

    public Symbol copy() {
        var symbol = new Symbol(this.type);
        symbol.lexeme = this.lexeme;
        symbol.label = label;
        symbol.offset = offset;
        symbol.layerOffset = this.layerOffset;
        return symbol;
    }

    @Override
    public String toString() {
        if (this.type == SymbolType.LABEL_SYMBOL) {
            return this.label;
        }
        return lexeme.getValue();
    }

    public void setParent(SymbolTable parent) {
        this.parent = parent;
    }

    public SymbolTable getParent() {
        return this.parent;
    }

    public void setLexeme(Token lexeme) {
        this.lexeme = lexeme;
    }

    public Token getLexeme() {
        return this.lexeme;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return this.offset;
    }

    public void setLayerOffset(int layerOffset) {
        this.layerOffset = layerOffset;
    }

    public int getLayerOffset() {
        return this.layerOffset;
    }

    public SymbolType getType() {
        return this.type;
    }
}
