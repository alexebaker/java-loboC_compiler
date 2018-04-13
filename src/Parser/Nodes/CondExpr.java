package Parser.Nodes;

import Errors.Error;
import Errors.SyntaxError;
import Tokenizer.TokenReader;
import Compiler.*;
import Types.Type;
import Types.TypeEnum;

public class CondExpr extends ASTNode {
    private ASTNode logOrExpr;
    private ASTNode expr;
    private ASTNode condExpr;

    public CondExpr() {
        logOrExpr = null;
        expr = null;
        condExpr = null;
    }

    public void setLogOrExpr(ASTNode logOrExpr) {
        this.logOrExpr = logOrExpr;
    }

    public void setCondExpr(ASTNode condExpr) {
        this.condExpr = condExpr;
    }

    public void setExpr(ASTNode expr) {
        this.expr = expr;
    }

    @Override
    public String getASTR(int indentDepth, CompilerState cs) {
        StringBuilder str = new StringBuilder("");
        if (logOrExpr != null) {
            if (expr != null && condExpr != null) {
                str.append(getTypePrefix(cs));
                str.append("(");
                str.append(logOrExpr.getASTR(0, cs));
                str.append("?");
                str.append(expr.getASTR(0, cs));
                str.append(":");
                str.append(condExpr.getASTR(0, cs));
                str.append(")");
            }
            else {
                str.append(logOrExpr.getASTR(0, cs));
            }
        }
        return str.toString();
    }

    public static ASTNode parse(CompilerState cs, SymbolTable st) throws Error {
        TokenReader tr = cs.getTr();
        CondExpr condExpr = new CondExpr();
        condExpr.setLogOrExpr(LogOrExpr.parse(cs, st));
        if (tr.peek().getValue().equals("?")) {
            tr.read();
            condExpr.setExpr(Expr.parse(cs, st));
            if (tr.peek().getValue().equals(":")) {
                tr.read();
                condExpr.setCondExpr(CondExpr.parse(cs, st));
            }
            else {
                throw new SyntaxError(tr.read(), ":");
            }
        }
        return condExpr;
    }


    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            if (expr != null && condExpr != null) {
                if (logOrExpr.getNodeType(cs).getTypeEnum() == TypeEnum.BOOL) {
                    if (expr.getNodeType(cs).equals(condExpr.getNodeType(cs))) {
                        setType(expr.getNodeType(cs));
                    }
                    else {
                        //excpetion
                    }
                }
                else {
                    //exception
                }
            }
            else {
                setType(logOrExpr.getNodeType(cs));
            }
        }
        return getType();
    }

    public ASTNode foldConstants() {
        logOrExpr = logOrExpr.foldConstants();
        if (expr != null && condExpr != null) {
            expr = expr.foldConstants();
            condExpr.foldConstants();

            Object logOrValue = logOrExpr.getValue();
            if (logOrValue != null) {
                if (logOrValue instanceof Boolean) {
                    if ((boolean) logOrValue) {
                        return expr;
                    }
                    else {
                        return condExpr;
                    }
                }
                else if (logOrValue instanceof Integer) {
                    if ((int) logOrValue != 0) {
                        return expr;
                    }
                    else {
                        return condExpr;
                    }
                }
            }
        }
        return this;
    }

    public Object getValue() {
        if (logOrExpr != null) {
            if (expr != null && condExpr != null) {
                Object boolValue = logOrExpr.getValue();
                if (boolValue != null) {
                    if (boolValue instanceof Integer) {
                        if ((int) boolValue != 0) {
                            return expr.getValue();
                        }
                        else {
                            return condExpr.getValue();
                        }
                    }
                    else if (boolValue instanceof Boolean) {
                        if ((boolean) boolValue) {
                            return expr.getValue();
                        }
                        else {
                            return condExpr.getValue();
                        }
                    }
                }

            }
            return logOrExpr.getValue();
        }
        return null;
    }

    public Location getLocation() {
        if (logOrExpr != null) {
            return logOrExpr.getLocation();
        }
        return  null;
    }

    public boolean isAssignable() {
        if (logOrExpr != null) {
            if (expr != null && condExpr != null) {
                return false;
            }
            return logOrExpr.isAssignable();
        }
        return false;
    }
}
