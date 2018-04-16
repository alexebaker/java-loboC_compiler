package Parser.Nodes;

import Errors.Error;
import Errors.SyntaxError;
import Errors.TypeError;
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
        StringBuilder str = new StringBuilder();
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
                        String msg = "Second and Third arguments to the '?' operator must be the same type. Instead got '" + expr.getNodeType(cs) + "' and '" + condExpr.getNodeType(cs) + "'";
                        cs.addError(new TypeError(msg, logOrExpr.getLocation()));
                        setType(new Type(TypeEnum.UNDEF));
                    }
                }
                else {
                    String msg = "The '?' operator must start with a 'Bool', instead found '" + logOrExpr.getNodeType(cs) + "'";
                    cs.addError(new TypeError(msg, logOrExpr.getLocation()));
                    setType(new Type(TypeEnum.UNDEF));
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
            condExpr = condExpr.foldConstants();

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
        return  logOrExpr != null ? logOrExpr.getLocation() : null;
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

    public String getAsm(AsmLabel ifTrue, AsmLabel ifFalse, FallThrough ft) {
        StringBuilder asm = new StringBuilder();
        if (logOrExpr != null) {
            asm.append(logOrExpr.getAsm(ifTrue, ifFalse, ft));
            if (expr != null && condExpr != null) {
                asm.append(expr.getAsm(ifTrue, ifFalse, ft));
                asm.append(condExpr.getAsm(ifTrue, ifFalse, ft));
            }
        }
        return asm.toString();
    }
}
