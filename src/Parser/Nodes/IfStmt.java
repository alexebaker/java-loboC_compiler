package Parser.Nodes;

import Compiler.*;
import Errors.SyntaxError;
import Tokenizer.TokenReader;
import Types.Type;


public class IfStmt extends ASTNode {
    private ASTNode expr;
    private ASTNode stmt;
    private ASTNode optElse;

    public IfStmt() {
        expr = null;
        stmt = null;
        optElse = null;
    }

    public void setExpr(ASTNode expr) {
        this.expr = expr;
    }

    public ASTNode getExpr() {
        return expr;
    }

    public void setStmt(ASTNode stmt) {
        this.stmt = stmt;
    }

    public ASTNode getStmt() {
        return stmt;
    }

    public void setOptElse(ASTNode optElse) {
        this.optElse = optElse;
    }

    public ASTNode getOptElse() {
        return optElse;
    }

    @Override
    public String getASTR(int indentDepth, CompilerState cs) {
        StringBuilder str = new StringBuilder("");
        if (expr != null && stmt != null) {
            str.append("if (");
            str.append(expr.getASTR(0, cs));
            str.append(")\n");
            str.append(stmt.getASTR(indentDepth + 1, cs));
            if (optElse != null) {
                str.append(optElse.getASTR(indentDepth, cs));
            }
        }
        return str.toString();
    }

    public static ASTNode parse(CompilerState cs, SymbolTable st) throws SyntaxError {
        TokenReader tr = cs.getTr();
        IfStmt ifStmt = new IfStmt();
        if (tr.peek().getValue().equals("if")) {
            tr.read();

            if (tr.peek().getValue().equals("(")) {
                tr.read();
                ifStmt.setExpr(Expr.parse(cs, st));
            }
            else {
                throw new SyntaxError(tr.read(), "(");
            }

            if (tr.peek().getValue().equals(")")) {
                tr.read();
                ifStmt.setStmt(Statement.parse(cs, st));
                ifStmt.setOptElse(OptElse.parse(cs, st));
            }
            else {
                throw new SyntaxError(tr.read(), ")");
            }
        }
        else {
            throw new SyntaxError(tr.read(), "if");
        }
        return ifStmt;
    }

    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            expr.getNodeType(cs);
            stmt.getNodeType(cs);
            if (optElse != null) optElse.getNodeType(cs);
        }
        return getType();
    }

    public ASTNode foldConstants() {
        expr = expr.foldConstants();
        stmt = stmt.foldConstants();
        if (optElse != null) {
            optElse = optElse.foldConstants();
        }

        Object exprValue = expr.getValue();
        if (exprValue != null) {
            if (exprValue instanceof Boolean) {
                if ((boolean) exprValue) {
                    return stmt;
                }
                else {
                    return ((OptElse) optElse).getStmt();
                }
            }
            else if (exprValue instanceof Integer) {
                if ((int) exprValue != 0) {
                    return stmt;
                }
                else {
                    if (optElse != null) {
                        return ((OptElse) optElse).getStmt();
                    }
                    else {
                        return null;
                    }
                }
            }
        }
        return this;
    }

    public Object getValue() {
        return null;
    }

    public Location getLocation() {
        if (expr != null) {
            return expr.getLocation();
        }
        return null;
    }

    public boolean isAssignable() {
        return false;
    }
}
