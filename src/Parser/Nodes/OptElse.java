package Parser.Nodes;

import Compiler.*;
import Errors.SyntaxError;
import Tokenizer.TokenReader;
import Types.Type;


public class OptElse extends ASTNode {
    private ASTNode stmt;

    public OptElse() {
        stmt = null;
    }

    public void setStmt(ASTNode stmt) {
        this.stmt = stmt;
    }

    public ASTNode getStmt() {
        return stmt;
    }

    @Override
    public String getASTR(int indentDepth, CompilerState cs) {
        StringBuilder str = new StringBuilder("");
        if (stmt != null) {
            str.append(super.getASTR(indentDepth, cs));
            str.append("else\n");
            str.append(stmt.getASTR(indentDepth + 1, cs));
        }
        return str.toString();
    }

    public static ASTNode parse(CompilerState cs, SymbolTable st) throws SyntaxError {
        TokenReader tr = cs.getTr();
        OptElse optElse = new OptElse();
        if (tr.peek().getValue().equals("else")) {
            tr.read();

            optElse.setStmt(Statement.parse(cs, st));
            return optElse;
        }
        return null;
    }

    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            stmt.getNodeType(cs);
        }
        return getType();
    }

    public ASTNode foldConstants() {
        if (stmt != null) {
            stmt = stmt.foldConstants();
        }
        return this;
    }

    public Object getValue() {
        return null;
    }

    public Location getLocation() {
        if (stmt != null) {
            return stmt.getLocation();
        }
        return null;
    }

    public boolean isAssignable() {
        return false;
    }
}
