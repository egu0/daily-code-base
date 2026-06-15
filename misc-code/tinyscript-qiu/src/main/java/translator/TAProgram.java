package translator;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import translator.symbol.StaticSymbolTable;
import translator.symbol.SymbolTable;
import translator.symbol.SymbolType;

public class TAProgram {
    private ArrayList<TAInstruction> instructions = new ArrayList<>();
    private int labelCounter = 0;
    private StaticSymbolTable staticSymbolTable = new StaticSymbolTable();

    public void add(TAInstruction instruction) {
        this.instructions.add(instruction);
    }

    public TAInstruction addLabel() {
        var label = "L" + labelCounter++;
        var ins = new TAInstruction(TAInstructionType.LABEL, null, null, null, null);
        ins.setArg1(label);
        instructions.add(ins);
        return ins;
    }

    public ArrayList<TAInstruction> getInstructions() {
        return instructions;
    }

    public StaticSymbolTable getStaticSymbolTable() {
        return staticSymbolTable;
    }

    public int getLabelCounter() {
        return labelCounter;
    }

    public void setLabelCounter(int labelCounter) {
        this.labelCounter = labelCounter;
    }

    public void setStaticSymbolTable(StaticSymbolTable staticSymbolTable) {
        this.staticSymbolTable = staticSymbolTable;
    }

    @Override
    public String toString() {
        var lines = new ArrayList<String>();
        for (var i : instructions) {
            lines.add(i.toString());
        }
        return StringUtils.join(lines, "\n");
    }

    /**
     * 以递归的方式从树状符号表中提取常量到静态符号表
     */
    public void setStaticSymbols(SymbolTable symbolTable) {
        for (var symbol : symbolTable.getSymbols()) {
            if (symbol.getType() == SymbolType.IMMEDIATE_SYMBOL) {
                staticSymbolTable.add(symbol);
            }
        }

        for (var child : symbolTable.getChildren()) {
            setStaticSymbols(child);
        }
    }
}
