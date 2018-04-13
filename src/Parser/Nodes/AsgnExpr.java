package Parser.Nodes;

import Errors.Error;
import Errors.TypeError;
import Tokenizer.TokenReader;
import Compiler.*;
import Types.PointerType;
import Types.Type;
import Types.TypeEnum;

public class AsgnExpr extends ASTNode {
    private ASTNode asgnExpr;
    private ASTNode condExpr;

    public AsgnExpr() {
        this.asgnExpr = null;
        this.condExpr = null;
    }

    public void setAsgnExpr(ASTNode asgnExpr) {
        this.asgnExpr = asgnExpr;
    }

    public void setCondExpr(ASTNode condExpr) {
        this.condExpr = condExpr;
    }

    public ASTNode getCondExpr() {
        return condExpr;
    }

    @Override
    public String getASTR(int indentDepth, CompilerState cs) {
        StringBuilder str = new StringBuilder("");
        if (condExpr != null) {
            if (asgnExpr != null) {
                str.append(getTypePrefix(cs));
                str.append("(");
                str.append(condExpr.getASTR(indentDepth, cs));
                str.append("=");
                str.append(asgnExpr.getASTR(indentDepth, cs));
                str.append(")");
            }
            else {
                str.append(condExpr.getASTR(indentDepth, cs));
            }
        }
        return str.toString();
    }

    public static ASTNode parse(CompilerState cs, SymbolTable st) throws Error {
        TokenReader tr = cs.getTr();
        AsgnExpr asgnExpr = new AsgnExpr();
        asgnExpr.setCondExpr(CondExpr.parse(cs, st));
        if (tr.peek().getValue().equals("=")) {
            if (asgnExpr.getCondExpr().isAssignable()) {
                tr.read();
                asgnExpr.setAsgnExpr(AsgnExpr.parse(cs, st));
            }
            else {
                String msg = "'" + asgnExpr.toString() + "' is not assignable";
                throw new TypeError(msg, tr.read().getLoc());
            }
        }
        return asgnExpr;
    }

    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            if (asgnExpr == null) {
                setType(condExpr.getNodeType(cs));
            }
            else {
                Type lhs = condExpr.getNodeType(cs);
                Type rhs = asgnExpr.getNodeType(cs);
                if (PointerType.isType(lhs) && PointerType.isType(rhs)) {
                    if (lhs.equals(rhs)) {
                        setType(lhs);
                    }
                    else {
                        setType(new Type(TypeEnum.UNDEF));
                        String msg = "Pointers must be of the same type, instead got '" + lhs + "' and '" + rhs + "'";
                        cs.addError(new TypeError(msg, condExpr.getLocation()));
                    }
                }
                else if (Types.PrimType.isType(lhs) && Types.PrimType.isType(rhs)) {
                    setType(lhs);
                }
                else {
                    setType(new Type(TypeEnum.UNDEF));
                    String msg = "Type '" + rhs + "' cannot be assigned to type '" + lhs + "'";
                    cs.addError(new TypeError(msg, condExpr.getLocation()));
                }
            }
        }
        return getType();
    }

    public ASTNode foldConstants() {
        if (condExpr != null) {
            condExpr = condExpr.foldConstants();
            if (asgnExpr != null) {
                asgnExpr = asgnExpr.foldConstants();
            }
        }
        return this;
    }

    public Object getValue() {
        if (condExpr != null) {
            if (asgnExpr != null) {
                return asgnExpr.getValue();
            }
            return condExpr.getValue();
        }
        return null;
    }

    public Location getLocation() {
        if (condExpr != null) {
            return condExpr.getLocation();
        }
        return null;
    }

    public boolean isAssignable() {
        condExpr = condExpr != null ?  condExpr.foldConstants() : null;
        asgnExpr = asgnExpr != null ? asgnExpr.foldConstants() : null;
        return condExpr != null && asgnExpr != null && condExpr.isAssignable();
    }
}
