package Parser.Nodes;

import Compiler.*;
import Errors.Error;
import Errors.SyntaxError;
import Parser.Operators.PreunOp;
import Tokenizer.TokenReader;
import Tokenizer.Tokens.Token;
import Types.Type;


public class Statement extends ASTNode {
    private ASTNode expr;
    private ASTNode block;
    private ASTNode ifStmt;
    private ASTNode whileStmt;

    public Statement() {
        expr = null;
        block = null;
        ifStmt = null;
        whileStmt = null;
    }

    public void setExpr(ASTNode expr) {
        this.expr = expr;
    }

    public ASTNode getExpr() {
        return expr;
    }

    public void setBlock(ASTNode block) {
        this.block = block;
    }

    public ASTNode getBlock() {
        return block;
    }

    public void setIfStmt(ASTNode ifStmt) {
        this.ifStmt = ifStmt;
    }

    public ASTNode getIfStmt() {
        return ifStmt;
    }

    public void setWhileStmt(ASTNode whileStmt) {
        this.whileStmt = whileStmt;
    }

    public ASTNode getWhileStmt() {
        return whileStmt;
    }

    @Override
    public String getASTR(int indentDepth, CompilerState cs) {
        StringBuilder str = new StringBuilder();
        String indentStr = super.getASTR(indentDepth, cs);
        if (expr != null) {
            str.append(indentStr);
            str.append(expr.getASTR(indentDepth, cs));
            str.append(";\n");
        }
        else if (ifStmt != null) {
            str.append(indentStr);
            str.append(ifStmt.getASTR(indentDepth, cs));
            str.append("\n");
        }
        else if (whileStmt != null) {
            str.append(indentStr);
            str.append(whileStmt.getASTR(indentDepth, cs));
            str.append("\n");
        }
        else if (block != null) {
            str.append(indentStr);
            str.append("{\n");
            if (block != null) {
                str.append(block.getVSR(indentDepth+1, cs));
                str.append(block.getASTR(indentDepth+1, cs));
            }
            str.append(indentStr);
            str.append("}\n");
        }
        return str.toString();
    }

    public static ASTNode parse(CompilerState cs, SymbolTable st) throws Error {
        TokenReader tr = cs.getTr();
        Statement stmt = new Statement();
        if (tr.peek().getValue().equals("{")) {
            tr.read();

            stmt.setBlock(Block.parse(cs, st));

            if (tr.peek().getValue().equals("}")) {
                tr.read();
            }
            else {
                throw new SyntaxError(tr.read(), "}");
            }
        }
        else if (tr.peek().getValue().equals("if")) {
            stmt.setIfStmt(IfStmt.parse(cs, st));
        }
        else if (tr.peek().getValue().equals("while")) {
            stmt.setWhileStmt(WhileStmt.parse(cs, st));
        }
        else {
            try {
                stmt.setExpr(Expr.parse(cs, st));
                Token nextToken = tr.peek();
                if (nextToken.getValue().equals(";")) {
                    tr.read();
                }
                else {
                    if (!nextToken.getValue().equals("}")) {
                        nextToken = tr.read();
                    }
                    throw new SyntaxError(nextToken, ";");
                }
            }
            catch (Error ex) {
                cs.addError(ex);
                Token recoveredToken = tr.recoverFromError();
                if (!recoveredToken.getValue().equals(";")) {
                    throw new SyntaxError(recoveredToken, ";");
                }
            }
        }
        return stmt;
    }

    public static boolean beginsStmt(Token token) {
        return Statement.beginsStmt(token.getValue());
    }

    public static boolean beginsStmt(String str) {
        return PrimaryExpr.beginsPrimaryExpr(str) || str.equals("{") || PreunOp.isOp(str) || str.equals("if") || str.equals("while");
    }

    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            if (expr != null) {
                setType(expr.getNodeType(cs));
            }
            else if (block != null) {
                setType(block.getNodeType(cs));
            }
            else if (ifStmt != null) {
                setType(ifStmt.getNodeType(cs));
            }
            else if (whileStmt != null) {
                setType(whileStmt.getNodeType(cs));
            }
        }
        return getType();
    }

    public ASTNode foldConstants() {
        if (expr != null) {
            expr = expr.foldConstants();
        }
        else if (ifStmt != null) {
            ifStmt = ifStmt.foldConstants();
        }
        else if (whileStmt != null) {
            whileStmt = whileStmt.foldConstants();
        }
        else if (block != null) {
            block = block.foldConstants();
        }
        return this;
    }

    public Object getValue() {
        if (expr != null) {
            return expr.getValue();
        }
        else if (ifStmt != null) {
            return ifStmt.getValue();
        }
        else if (whileStmt != null) {
            return whileStmt.getValue();
        }
        else if (block != null) {
            return block.getValue();
        }
        return null;
    }

    public Location getLocation() {
        if (expr != null) {
            return expr.getLocation();
        }
        else if (ifStmt != null) {
            return ifStmt.getLocation();
        }
        else if (whileStmt != null) {
            return whileStmt.getLocation();
        }
        else if (block != null) {
            return block.getLocation();
        }
        return null;
    }

    public boolean isAssignable() {
        return false;
    }

    public String getAsm(AsmData ad) {
        StringBuilder asm = new StringBuilder();
        if (expr != null) {
            asm.append(expr.getAsm(ad));
        }
        else if (ifStmt != null) {
            asm.append(ifStmt.getAsm(ad));
        }
        else if (whileStmt != null) {
            asm.append(whileStmt.getAsm(ad));
        }
        else if (block != null) {
            asm.append(block.getAsm(ad));
        }
        return asm.toString();
    }
}
