package Parser.Nodes;

import Compiler.*;
import Errors.Error;
import Errors.SyntaxError;
import Tokenizer.TokenReader;
import Types.Type;


public class WhileStmt extends ASTNode {
    private ASTNode expr;
    private ASTNode stmt;

    public WhileStmt() {
        expr = null;
        stmt = null;
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

    @Override
    public String getASTR(int indentDepth, CompilerState cs) {
        StringBuilder str = new StringBuilder();
        if (expr != null && stmt != null) {
            str.append("while (");
            str.append(expr.getASTR(0, cs));
            str.append(")\n");
            str.append(stmt.getASTR(indentDepth + 1, cs));
        }
        return str.toString();
    }

    public static ASTNode parse(CompilerState cs, SymbolTable st) throws Error {
        TokenReader tr = cs.getTr();
        WhileStmt whileStmt = new WhileStmt();
        if (tr.peek().getValue().equals("while")) {
            tr.read();

            if (tr.peek().getValue().equals("(")) {
                tr.read();
                whileStmt.setExpr(Expr.parse(cs, st));
            }
            else {
                throw new SyntaxError(tr.read(), "(");
            }

            if (tr.peek().getValue().equals(")")) {
                tr.read();
                whileStmt.setStmt(Statement.parse(cs, st));
            }
            else {
                throw new SyntaxError(tr.read(), ")");
            }
        }
        else {
            throw new SyntaxError(tr.read(), "while");
        }
        return whileStmt;
    }

    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            expr.getNodeType(cs);
            stmt.getNodeType(cs);
        }
        return getType();
    }

    public ASTNode foldConstants() {
        expr = expr.foldConstants();
        stmt = stmt.foldConstants();

        Object exprValue = expr.getValue();
        if (exprValue != null) {
            if (exprValue instanceof Boolean) {
                if (!((boolean) exprValue)) {
                    return null;
                }
            }
            else if (exprValue instanceof Integer) {
                if ((int) exprValue == 0) {
                    return null;
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
            expr.getLocation();
        }
        return null;
    }

    public boolean isAssignable() {
        return false;
    }

    public String getAsm(AsmData ad) {
        StringBuilder asm = new StringBuilder();
        if (expr != null && stmt != null) {
            AsmData exprAD = new AsmData(ad);
            AsmData stmtAD = new AsmData(ad);
            String lbl1 = "label" + ad.getLabelCounter();
            String lbl2 = "label" + ad.getLabelCounter();
            String newAddr = ad.getSt().getTmp(ad.getSt().addTmp(stmt.getType())).getAddr();
            asm.append(lbl1 + ":\n");
            asm.append(expr.getAsm(exprAD));
            asm.append("\t" + expr.getLoadInst() + " $t0," + exprAD.getAddr() + "\n");
            asm.append("\tbeq $0,$t0," + lbl2 + "\n");
            asm.append(stmt.getAsm(stmtAD));
            asm.append("\t" + stmt.getLoadInst() + " $t1," + stmtAD.getAddr() + "\n");
            asm.append("\t" + stmt.getStoreInst() + " $t1," + newAddr + "\n");
            asm.append("\tj " + lbl1 + "\n");
            asm.append(lbl2 + ":\n");
            ad.setAddr(newAddr);
        }
        return asm.toString();
    }
}
