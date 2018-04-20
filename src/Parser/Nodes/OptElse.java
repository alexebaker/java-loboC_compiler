package Parser.Nodes;

import Compiler.*;
import Errors.Error;
import Tokenizer.TokenReader;
import Types.Type;
import Types.TypeEnum;


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
        StringBuilder str = new StringBuilder();
        if (stmt != null) {
            str.append(super.getASTR(indentDepth, cs));
            str.append("else\n");
            str.append(stmt.getASTR(indentDepth + 1, cs));
        }
        return str.toString();
    }

    public static ASTNode parse(CompilerState cs, SymbolTable st) throws Error {
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
            return stmt.getNodeType(cs);
        }
        return getType();
    }

    public ASTNode foldConstants() {
        if (stmt != null) {
            stmt = stmt.foldConstants();
        }
        return stmt != null ? this : null;
    }

    public Object getValue() {
        return stmt != null ? stmt.getValue() : null;
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

    public String getAsm(AsmData ad) {
        return stmt != null ? stmt.getAsm(ad) : "";
    }

    public String getLoadInst() {
        if (stmt.getType() != null && stmt.getType().getTypeEnum() == TypeEnum.BOOL) {
            return "lbu";
        }
        return "lw";
    }

    public String getStoreInst() {
        if (stmt.getType() != null && stmt.getType().getTypeEnum() == TypeEnum.BOOL) {
            return "sb";
        }
        return "sw";
    }
}
