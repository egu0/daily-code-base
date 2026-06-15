package parser.ast;

import parser.util.ParseException;
import parser.util.PeekTokenIterator;

/**
 * 语句
 */
public abstract class Stmt extends ASTNode {
    public Stmt(ASTNodeTypes _type, String _label) {
        super(_type, _label);
    }

    /**
     * 解析语句
     * @param it
     * @return
     * @throws ParseException
     */
    public static ASTNode parseStmt(PeekTokenIterator it) throws ParseException {
        if (!it.hasNext()) {
            return null;
        }
        var token = it.next();
        var lookahead = it.peek();
        it.putBack();

        if (token.isVariable() && lookahead != null && lookahead.getValue().equals("=")) {
            //赋值语句
            return AssignStmt.parse(it);
        } else if (token.getValue().equals("var")) {
            //声明语句
            return DeclareStmt.parse(it);
        } else if (token.getValue().equals("func")) {
            // 函数定义语句
            return FunctionDeclareStmt.parse(it);
        } else if (token.getValue().equals("return")) {
            // return 语句
            return ReturnStmt.parse(it);
        } else if (token.getValue().equals("if")) {
            // if 语句
            return IfStmt.parse(it);
        } else if (token.getValue().equals("{")) {
            // 语句块
            return Block.parse(it);
        } else {
            // 表达式
            return Expr.parse(it);
        }

    }

}
