#pragma once

#include <cassert>
#include <variant>

#include "arena.hpp"
#include "tokenization.hpp"


//--------------------------------

struct NodeTermIntLit {
    Token int_lit;
};

struct NodeTermIdent {
    Token ident;
};

struct NodeExpr;

struct NodeTermParen {
    NodeExpr* expr;
};

struct NodeTerm {
    std::variant<NodeTermIntLit*, NodeTermIdent*, NodeTermParen*> var;
};

//--------------------------------

struct NodeBinExprAdd {
    NodeExpr* lhs;
    NodeExpr* rhs;
};

struct NodeBinExprMulti {
    NodeExpr* lhs;
    NodeExpr* rhs;
};

struct NodeBinExprSub {
    NodeExpr* lhs;
    NodeExpr* rhs;
};

struct NodeBinExprDiv {
    NodeExpr* lhs;
    NodeExpr* rhs;
};

struct NodeBinExpr {
    std::variant<NodeBinExprAdd*, NodeBinExprSub*, NodeBinExprMulti*, NodeBinExprDiv*> var;
};

struct NodeExpr {
    std::variant<NodeTerm*, NodeBinExpr*> var;
};

//--------------------------------

struct NodeStmtExit {
    NodeExpr* expr;
};

struct NodeStmtAssign {
    Token ident;
    NodeExpr* expr;
};

struct NodeStmtLet {
    Token ident;
    NodeExpr* expr;
};

struct NodeStmt;

struct NodeStmtScope {
    std::vector<NodeStmt*> stmts;
};

struct NodeStmtIfPred;

struct NodeStmtIfPredElif {
    NodeExpr* expr{};
    NodeStmtScope* scope{};
    std::optional<NodeStmtIfPred*> pred;
};

struct NodeStmtIfPredElse {
    NodeStmtScope* scope;
};

struct NodeStmtIfPred {
    std::variant<NodeStmtIfPredElif*, NodeStmtIfPredElse*> var;
};

struct NodeStmtIf {
    NodeExpr* expr;
    NodeStmtScope* scope;
    std::optional<NodeStmtIfPred*> pred;
};

struct NodeStmt {
    std::variant<NodeStmtExit*, NodeStmtLet*, NodeStmtScope*, NodeStmtIf*, NodeStmtAssign*> var;
};

//--------------------------------

struct NodeProg {
    std::vector<NodeStmt*> stmts;
};

class Parser {
public:
    inline explicit Parser(std::vector<Token> tokens)
        : m_tokens(std::move(tokens))
        , m_allocator(1024 * 1024 * 4) // 4 mb
    {
    }

    /**
     * 解析词语（变量标识符、数字字面量）
    */
    std::optional<NodeTerm*> parse_term()
    {
        if (auto int_lit = try_consume(TokenType::int_lit)) {
            auto term_int_lit = m_allocator.emplace<NodeTermIntLit>(int_lit.value());
            auto term = m_allocator.emplace<NodeTerm>(term_int_lit);
            return term;
        }
        else if (auto ident = try_consume(TokenType::ident)) {
            auto expr_ident = m_allocator.emplace<NodeTermIdent>(ident.value());
            auto term = m_allocator.emplace<NodeTerm>(expr_ident);
            return term;
        }
        else if (auto open_paren = try_consume(TokenType::open_paren)) {
            auto expr = parse_expr();
            if (!expr.has_value()) {
                std::cerr << "Expected expression" << std::endl;
                exit(EXIT_FAILURE);
            }
            try_consume(TokenType::close_paren, "Expected `)`");

            auto term_paren = m_allocator.emplace<NodeTermParen>(expr.value());
            auto term = m_allocator.emplace<NodeTerm>(term_paren);
            return term;
        }
        else {
            return {};
        }
    }

    /**
     * 解析表达式（核心方法）
    */
    std::optional<NodeExpr*> parse_expr(int min_prec = 0)
    {
        // 获取左表达式，类型是 NodeExpr<NodeTerm>
        std::optional<NodeTerm*> term_lhs = parse_term();
        if (!term_lhs.has_value()) {
            return {};
        }
        auto expr_lhs = m_allocator.emplace<NodeExpr>(term_lhs.value());

        while (true) {
            std::optional<Token> cur_operator = peek();
            std::optional<int> prec;
            if (cur_operator.has_value()) {
                prec = bin_prec(cur_operator->type);
                if (!prec.has_value() || prec < min_prec) {
                    break;
                }
            }
            else {
                break;
            }
            // op 是操作符，并且 op 的优先级 >= min_prec
            Token op = consume();

            // 解析右侧表达式
            int next_min_prec = prec.value() + 1;
            auto expr_rhs = parse_expr(next_min_prec);
            if (!expr_rhs.has_value()) {
                std::cerr << "Unable to parse expression" << std::endl;
                exit(EXIT_FAILURE);
            }

            // 根据 op 类型组织新的表达式实例
            auto expr = m_allocator.emplace<NodeBinExpr>();
            // 用于保存旧左侧表达式
            auto expr_lhs_old = m_allocator.emplace<NodeExpr>();
            // 冗余代码
            if (op.type == TokenType::plus) {
                expr_lhs_old->var = expr_lhs->var;
                auto add = m_allocator.emplace<NodeBinExprAdd>(expr_lhs_old, expr_rhs.value());
                expr->var = add;
            }
            else if (op.type == TokenType::star) {
                expr_lhs_old->var = expr_lhs->var;
                auto multi = m_allocator.emplace<NodeBinExprMulti>(expr_lhs_old, expr_rhs.value());
                expr->var = multi;
            }
            else if (op.type == TokenType::minus) {
                expr_lhs_old->var = expr_lhs->var;
                auto sub = m_allocator.emplace<NodeBinExprSub>(expr_lhs_old, expr_rhs.value());
                expr->var = sub;
            }
            else if (op.type == TokenType::fslash) {
                expr_lhs_old->var = expr_lhs->var;
                auto div = m_allocator.emplace<NodeBinExprDiv>(expr_lhs_old, expr_rhs.value());
                expr->var = div;
            }
            else {
                assert(false); // Unreachable;
            }

            expr_lhs->var = expr;
        }
        return expr_lhs;
    }

    /**
     * 解析作用域
    */
    std::optional<NodeStmtScope*> parse_scope()
    {
        if (!try_consume(TokenType::open_curly).has_value()) {
            return {};
        }
        auto scope = m_allocator.emplace<NodeStmtScope>();
        while (auto stmt = parse_stmt()) {
            scope->stmts.push_back(stmt.value());
        }
        try_consume(TokenType::close_curly, "Expected `}`");
        return scope;
    }

    /**
     * 解析 elif 或 else 语句
    */
    std::optional<NodeStmtIfPred*> parse_if_pred()
    {
        if (try_consume(TokenType::elif)) {
            try_consume(TokenType::open_paren, "Expected `(`");
            const auto elif = m_allocator.alloc<NodeStmtIfPredElif>();
            if (const auto expr = parse_expr()) {
                elif->expr = expr.value();
            }
            else {
                std::cerr << "Expected expression" << std::endl;
                exit(EXIT_FAILURE);
            }
            try_consume(TokenType::close_paren, "Expected `)`");
            if (const auto scope = parse_scope()) {
                elif->scope = scope.value();
            }
            else {
                std::cerr << "Expected scope" << std::endl;
                exit(EXIT_FAILURE);
            }

            elif->pred = parse_if_pred();
            auto pred = m_allocator.emplace<NodeStmtIfPred>(elif);
            return pred;
        }

        if (try_consume(TokenType::else_)) {
            auto else_ = m_allocator.alloc<NodeStmtIfPredElse>();
            if (const auto scope = parse_scope()) {
                else_->scope = scope.value();
            }
            else {
                std::cerr << "Expected scope" << std::endl;
                exit(EXIT_FAILURE);
            }
            auto pred = m_allocator.emplace<NodeStmtIfPred>(else_);
            return pred;
        }
        return {};
    }

    /**
     * 解析语句
    */
    std::optional<NodeStmt*> parse_stmt()
    {
        if (peek().value().type == TokenType::exit
            && peek(1).has_value() && peek(1).value().type == TokenType::open_paren) {
            // ['exit', '(']
            consume();
            consume();

            auto stmt_exit = m_allocator.emplace<NodeStmtExit>();
            if (auto node_expr = parse_expr()) {
                stmt_exit->expr = node_expr.value();
            }
            else {
                std::cerr << "Invalid expression" << std::endl;
                exit(EXIT_FAILURE);
            }
            try_consume(TokenType::close_paren, "Expected `)`");
            try_consume(TokenType::semi, "Expected `;`");

            auto stmt = m_allocator.emplace<NodeStmt>();
            stmt->var = stmt_exit;
            return stmt;
        }
        else if (
            peek().has_value() && peek().value().type == TokenType::let
            && peek(1).has_value() && peek(1).value().type == TokenType::ident
            && peek(2).has_value() && peek(2).value().type == TokenType::eq) {
            // ['let', 'x', '=']
            consume();

            auto stmt_let = m_allocator.emplace<NodeStmtLet>();
            stmt_let->ident = consume();
            consume();
            if (auto expr = parse_expr()) {
                stmt_let->expr = expr.value();
            }
            else {
                std::cerr << "Invalid expression" << std::endl;
                exit(EXIT_FAILURE);
            }
            try_consume(TokenType::semi, "Expected `;`");

            auto stmt = m_allocator.emplace<NodeStmt>();
            stmt->var = stmt_let;
            return stmt;
        }
        else if (peek().has_value() && peek().value().type == TokenType::open_curly) {
            if (auto scope = parse_scope()) {
                auto stmt = m_allocator.emplace<NodeStmt>(scope.value());
                return stmt;
            }
            else {
                std::cerr << "Invalid scope" << std::endl;
                exit(EXIT_FAILURE);
            }
        }
        else if (auto if_ = try_consume(TokenType::if_)) {
            // 解析 if 条件表达式
            try_consume(TokenType::open_paren, "Expected `(`");
            auto stmt_if = m_allocator.emplace<NodeStmtIf>();
            if (auto expr = parse_expr()) {
                stmt_if->expr = expr.value();
            }
            else {
                std::cerr << "Invalid expression" << std::endl;
                exit(EXIT_FAILURE);
            }
            try_consume(TokenType::close_paren, "Expected `)`");

            // 解析 if 语句的块作用域
            if (auto scope = parse_scope()) {
                stmt_if->scope = scope.value();
            }
            else {
                std::cerr << "Invalid scope" << std::endl;
                exit(EXIT_FAILURE);
            }

            stmt_if->pred = parse_if_pred();
            auto stmt = m_allocator.emplace<NodeStmt>(stmt_if);
            return stmt;
        }
        else if (peek().has_value() && peek().value().type == TokenType::ident
            && peek(1).has_value() && peek(1).value().type == TokenType::eq) {
            // 赋值语句
            const auto assign = m_allocator.alloc<NodeStmtAssign>();
            assign->ident = consume(); // 1
            consume();// 2

            if (const auto expr = parse_expr()) {
                assign->expr = expr.value(); // 3
            }
            else {
                std::cerr << "Expected expression" << std::endl;
                exit(EXIT_FAILURE);
            }
            try_consume(TokenType::semi, "Expected `;`");//4

            auto stmt = m_allocator.emplace<NodeStmt>(assign);
            return stmt;
        }
        else {
            return {};
        }
    }

    /**
     * 解析程序
    */
    std::optional<NodeProg> parse_prog()
    {
        NodeProg prog;
        while (peek().has_value()) {
            if (auto stmt = parse_stmt()) {
                prog.stmts.push_back(stmt.value());
            }
            else {
                std::cerr << "Invalid statement" << std::endl;
                exit(EXIT_FAILURE);
            }
        }
        return prog;
    }

private:
    /**
     * 读取当前指针指向的 token
    */
    [[nodiscard]] inline std::optional<Token> peek(std::size_t offset = 0) const
    {
        if (m_index + offset >= m_tokens.size()) {
            return {};
        }
        else {
            return m_tokens.at(m_index + offset);
        }
    }

    /**
     * 指针前移
    */
    inline Token consume()
    {
        return m_tokens.at(m_index++);
    }

    /**
     * try to conusme the peek element if the type matches, otherwise exit
    */
    inline Token try_consume(TokenType type, const std::string& err_msg)
    {
        if (peek().has_value() && peek().value().type == type) {
            return consume();
        }
        else {
            std::cerr << err_msg << std::endl;
            exit(EXIT_FAILURE);
        }
    }

    /**
     * try to conusme the peek element if the type matches
    */
    inline std::optional<Token> try_consume(TokenType type)
    {
        if (peek().has_value() && peek().value().type == type) {
            return consume();
        }
        else {
            return {};
        }
    }

    const std::vector<Token> m_tokens;
    std::size_t m_index = 0;
    ArenaAllocator m_allocator;
};