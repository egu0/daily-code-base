package translator;

import java.util.ArrayList;

import org.apache.commons.lang3.NotImplementedException;

import lexer.Token;
import lexer.TokenType;
import parser.ast.ASTNode;
import parser.ast.ASTNodeTypes;
import parser.ast.Block;
import parser.ast.Expr;
import parser.ast.FunctionDeclareStmt;
import parser.ast.IfStmt;
import parser.util.ParseException;
import translator.symbol.Symbol;
import translator.symbol.SymbolTable;

public class Translator {

    /**
     * 自顶向下翻译
     * @throws ParseException 
     */
    public TAProgram translate(ASTNode astNode) throws ParseException {
        var program = new TAProgram();
        var symbolTable = new SymbolTable();

        for (var child : astNode.getChildren()) {
            translateStmt(program, child, symbolTable);
        }

        program.setStaticSymbols(symbolTable);

        var main = new Token(TokenType.VARIABLE, "main");
        if (symbolTable.exists(main)) {
            symbolTable.createVariable(); // 返回值
            program.add(new TAInstruction(TAInstructionType.SP, null, null,
                    -symbolTable.localSize(), null));
            program.add(new TAInstruction(
                    TAInstructionType.CALL, null, null,
                    symbolTable.cloneFromSymbolTree(main, 0), null));
            program.add(new TAInstruction(TAInstructionType.SP, null, null,
                    symbolTable.localSize(), null));
        }

        return program;
    }

    public void translateStmt(TAProgram program, ASTNode node, SymbolTable symbolTable)
            throws ParseException {
        switch (node.getType()) {
            case ASSIGN_STMT:
                translateAssignStmt(program, node, symbolTable);
                return;
            case DECLARE_STMT:
                translateDeclareStmt(program, node, symbolTable);
                return;
            case BLOCK:
                translateBlock(program, (Block) node, symbolTable);
                return;
            case IF_STMT:
                translateIfStmt(program, (IfStmt) node, symbolTable);
                return;
            case FUNCTION_DECLARE_STMT:
                translateFunctionDeclareStmt(program, node, symbolTable);
                return;
            case CALL_EXPR:
                translateCallExpr(program, node, symbolTable);
                return;
            case RETURN_STMT:
                translateReturnStmt(program, node, symbolTable);
                return;
            default:
                throw new RuntimeException("type not implemented: " + node.getType());
        }
    }

    private void translateReturnStmt(TAProgram program, ASTNode node, SymbolTable symbolTable)
            throws ParseException {
        Symbol resultValue = null;
        if (node.getChild(0) != null) {
            resultValue = translateExpr(program, node.getChild(0), symbolTable);
        }
        program.add(new TAInstruction(TAInstructionType.RETURN, null, null, resultValue, null));
    }

    private void translateFunctionDeclareStmt(TAProgram program, ASTNode node, SymbolTable parent)
            throws ParseException {
        TAInstruction label = program.addLabel();
        label.setArg2(node.getLexeme().getValue());

        program.add(new TAInstruction(TAInstructionType.FUNC_BEGIN, null, null, null, null));

        var symbolTable = new SymbolTable();
        symbolTable.createVariable(); //返回地址, p0
        parent.addChild(symbolTable);
        parent.createLabel((String) label.getArg1(), node.getLexeme());

        FunctionDeclareStmt func = (FunctionDeclareStmt) node;
        for (ASTNode arg : func.getArgs().getChildren()) {
            symbolTable.createSymbolByLexeme(arg.getLexeme());
        }

        for (ASTNode stmt : func.getBlock().getChildren()) {
            translateStmt(program, stmt, symbolTable);
        }
    }

    private Symbol translateCallExpr(TAProgram program, ASTNode node, SymbolTable symbolTable)
            throws ParseException {

        var list = new ArrayList<TAInstruction>();
        for (int i = 1; i < node.getChildren().size(); i++) {
            var expr = node.getChildren().get(i);
            var addr = translateExpr(program, expr, symbolTable);
            list.add(new TAInstruction(TAInstructionType.PARAM, null, null, addr, i - 1));
        }
        for (var ins : list) {
            ins.setArg2(symbolTable.localSize() + (int) ins.getArg2() + 2);
            program.add(ins);
        }

        Symbol returnValue = symbolTable.createVariable(); //返回值
        var factor = node.getChild(0);
        var funcAddr = symbolTable.cloneFromSymbolTree(factor.getLexeme(), 0);
        if (funcAddr == null) {
            throw new ParseException("function " + factor.getLexeme().getValue() + " not found");
        }
        program.add(new TAInstruction(TAInstructionType.SP, null, null, -symbolTable.localSize(), null));
        program.add(new TAInstruction(TAInstructionType.CALL, null, null, funcAddr, null));
        program.add(new TAInstruction(TAInstructionType.SP, null, null, symbolTable.localSize(), null));
        return returnValue;
    }

    public void translateIfStmt(TAProgram program, IfStmt node, SymbolTable symbolTable)
            throws ParseException {
        ASTNode expr = node.getExpr();
        Symbol exprAddr = translateExpr(program, expr, symbolTable);
        TAInstruction ifTAInstruction = new TAInstruction(TAInstructionType.IF, null, null, exprAddr, null);
        program.add(ifTAInstruction);

        translateBlock(program, (Block) node.getBlock(), symbolTable);

        TAInstruction gotoInstruction = null;
        if (node.getChild(2) != null) {
            gotoInstruction = new TAInstruction(TAInstructionType.GOTO, null, null, null, null);
            program.add(gotoInstruction);
            var labelEndIf = program.addLabel();
            ifTAInstruction.setArg2(labelEndIf.getArg1());
        }

        // 解析 block 或 if stmt
        if (node.getElseBlock() != null) {
            translateBlock(program, (Block) node.getElseBlock(), symbolTable);
        } else if (node.getElseIfStmt() != null) {
            translateIfStmt(program, (IfStmt) node.getElseIfStmt(), symbolTable);
        }

        var labelEnd = program.addLabel();
        if (node.getChild(2) == null) {
            ifTAInstruction.setArg2(labelEnd.getArg1());
        } else {
            gotoInstruction.setArg1(labelEnd.getArg1());
        }
    }

    private void translateBlock(TAProgram program, ASTNode node, SymbolTable parent)
            throws ParseException {
        var symbolTable = new SymbolTable();
        parent.addChild(symbolTable);

        var parentOffset = symbolTable.createVariable();
        parentOffset.setLexeme(new Token(TokenType.INTEGER, symbolTable.localSize() + ""));

        for (var stmt : node.getChildren()) {
            translateStmt(program, stmt, symbolTable);
        }
    }

    public void translateDeclareStmt(TAProgram program, ASTNode node, SymbolTable symbolTable)
            throws ParseException {
        var lexeme = node.getChild(0).getLexeme();
        if (symbolTable.exists(lexeme)) {
            throw new ParseException("Syntax Error, Identifier already exists: " + lexeme.getValue());
        }

        Symbol assigned = symbolTable.createSymbolByLexeme(node.getChild(0).getLexeme());
        ASTNode expr = node.getChild(1);
        Symbol addr = translateExpr(program, expr, symbolTable);
        program.add(new TAInstruction(TAInstructionType.ASSIGN, assigned, "=", addr, null));
    }

    public void translateAssignStmt(TAProgram program, ASTNode node, SymbolTable symbolTable)
            throws ParseException {
        Symbol assigned = symbolTable.createSymbolByLexeme(node.getChild(0).getLexeme());
        ASTNode expr = node.getChild(1);
        Symbol addr = translateExpr(program, expr, symbolTable);
        program.add(new TAInstruction(TAInstructionType.ASSIGN, assigned, "=", addr, null));
    }

    /*
    SDD:
        E -> E1 op E2
        E -> F
     */
    public Symbol translateExpr(TAProgram program, ASTNode node, SymbolTable symbolTable)
            throws ParseException {
        if (node.isValueType()) {
            Symbol addr = symbolTable.createSymbolByLexeme(node.getLexeme());
            node.setProp("addr", addr);
            return addr;
        } else if (node.getType() == ASTNodeTypes.CALL_EXPR) {
            var addr = translateCallExpr(program, node, symbolTable);
            node.setProp("addr", addr);
            return addr;
        } else if (node instanceof Expr) {
            for (ASTNode child : node.getChildren()) {
                translateExpr(program, child, symbolTable);
            }

            if (node.getProp("addr") == null) {
                node.setProp("addr", symbolTable.createVariable());
            }

            var instruction = new TAInstruction(
                    TAInstructionType.ASSIGN,
                    (Symbol) (node.getProp("addr")),
                    node.getLexeme().getValue(),
                    (Symbol) (node.getChild(0).getProp("addr")),
                    (Symbol) (node.getChild(1).getProp("addr")));
            program.add(instruction);
            return instruction.getResult();
        }

        throw new NotImplementedException(node.getType().toString());
    }
}
