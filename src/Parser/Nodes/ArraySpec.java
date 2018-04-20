package Parser.Nodes;

import Errors.Error;
import Errors.SyntaxError;
import Errors.TypeError;
import Tokenizer.TokenReader;
import Compiler.*;
import Types.ArrayType;
import Types.PointerType;
import Types.Type;
import Types.TypeEnum;

public class ArraySpec extends ASTNode {
    private ASTNode expr;

    public ArraySpec() {
        this.expr = null;
    }

    public void setExpr(ASTNode expr) {
        this.expr = expr;
    }

    @Override
    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            Type type = null;
            if (expr != null) {
                if (Types.PrimType.isType(expr.getNodeType(cs))) {
                    try {
                        type = new ArrayType((int) expr.getValue());
                    }
                    catch (ClassCastException e) {
                        setType(new Type(TypeEnum.UNDEF));
                        String msg = "Type '" + expr.getNodeType(cs) + "' cannot be used for array lookups.";
                        cs.addError(new TypeError(msg, expr.getLocation()));
                    }
                }
                else {
                    setType(new Type(TypeEnum.UNDEF));
                    String msg = "Array lookup cannot be applied to type '" + expr.getNodeType(cs) + "'";
                    cs.addError(new TypeError(msg, expr.getLocation()));
                }
            }
            else {
                type = new PointerType();
            }
            setType(type);
        }
        return getType();
    }

    @Override
    public String getASTR(int indentDepth, CompilerState cs) {
        StringBuilder str = new StringBuilder();
        str.append("[");
        if (expr != null) {
            str.append(expr.getASTR(indentDepth, cs));
        }
        str.append("]");
        return str.toString();
    }

    public static ASTNode parse(CompilerState cs, SymbolTable st) throws Error {
        TokenReader tr = cs.getTr();
        if (tr.peek().getValue().equals("[")) {
            tr.read();
            ArraySpec arraySpec = new ArraySpec();
            if (tr.peek().getValue().equals("]")) {
                tr.read();
                return arraySpec;
            }
            else {
                arraySpec.setExpr(Expr.parse(cs, st));
                if (tr.peek().getValue().equals("]")) {
                    tr.read();
                    return arraySpec;
                }
                else {
                    throw new SyntaxError(tr.read(), "]");
                }
            }
        }
        else {
            throw new SyntaxError(tr.read(), "[");
        }
    }

    public ASTNode foldConstants() {
        if (expr != null) {
            expr = expr.foldConstants();
        }
        return this;
    }

    public Object getValue() {
        if (expr != null) {
            return expr.getValue();
        }
        return null;
    }

    public Location getLocation() {
        if (expr != null) {
            expr.getLocation();
        }
        return null;
    }

    public boolean isAssignable() {
        return true;
    }

    public String getAsm(AsmData ad) {
        StringBuilder asm = new StringBuilder();
        if (expr != null) asm.append(expr.getAsm(ad));
        return asm.toString();
    }
}
