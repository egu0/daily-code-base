package translator.symbol;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Hashtable;

public class StaticSymbolTable {

    private Hashtable<String, Symbol> offsetMap;
    private int offsetCounter = 0;
    private ArrayList<Symbol> symbols;

    public StaticSymbolTable() {
        symbols = new ArrayList<>();
        offsetMap = new Hashtable<>();
    }

    /**
     * 添加常量符号
     */
    public void add(Symbol symbol) {
        var lexval = symbol.getLexeme().getValue();
        if (!offsetMap.containsKey(lexval)) {
            // 常量值不存在
            offsetMap.put(lexval, symbol);
            symbol.setOffset(offsetCounter++);
            symbols.add(symbol);
        } else {
            // 常量值已存在
            var sameSymbol = offsetMap.get(lexval);
            symbol.setOffset(sameSymbol.offset);
        }
    }

    public int size() {
        return this.symbols.size();
    }

    @Override
    public String toString() {
        var list = new ArrayList<String>();
        for (int i = 0; i < this.symbols.size(); i++) {
            list.add(i + ":" + this.symbols.get(i).toString());
        }
        return StringUtils.join(list, "\n");
    }

    public ArrayList<Symbol> getSymbols() {
        return this.symbols;
    }
}
