package Parser.Operators;

import Errors.TypeError;
import Parser.Nodes.ASTNode;
import Parser.Nodes.Number;
import Tokenizer.Tokens.NumberToken;
import Tokenizer.Tokens.Token;
import Types.PrimType;
import Types.Type;
import Types.TypeEnum;
import Compiler.CompilerState;

public class TermOp extends Operator {
    public TermOp(Token token) {
        super(token);
    }

    @Override
    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            Type lhs = getLhs().getNodeType(cs);
            Type rhs = getRhs().getNodeType(cs);
            if (lhs.getTypeEnum() == TypeEnum.UNSIGNED && (rhs.getTypeEnum() == TypeEnum.UNSIGNED || rhs.getTypeEnum() == TypeEnum.SIGNED)) {
                setType(new PrimType(TypeEnum.UNSIGNED));
            }
            else if (lhs.getTypeEnum() == TypeEnum.SIGNED) {
                if (rhs.getTypeEnum() == TypeEnum.SIGNED) {
                    setType(new PrimType(TypeEnum.SIGNED));
                }
                else if (rhs.getTypeEnum() == TypeEnum.UNSIGNED) {
                    setType(new PrimType(TypeEnum.UNSIGNED));
                }
                else {
                    setType(new Type(TypeEnum.UNDEF));
                    String msg = "Cannot apply operator '" + getOp() + "' to types '" + lhs + "' and '" + rhs + "'";
                    cs.addError(new TypeError(msg, getLocation()));
                }
            }
            else {
                setType(new Type(TypeEnum.UNDEF));
                String msg = "Cannot apply operator '" + getOp() + "' to types '" + lhs + "' and '" + rhs + "'";
                cs.addError(new TypeError(msg, getLocation()));
            }
        }
        return getType();
    }

    @Override
    public ASTNode foldConstants() {
        setLhs(getLhs().foldConstants());
        setRhs(getRhs().foldConstants());

        if (getValue() != null) {
            try {
                return new Number(new NumberToken(Integer.toString((int) getValue()), getOp().getLoc()));
            }
            catch (ClassCastException e) {}
        }
        return this;
    }

    @Override
    public Object getValue() {
        Object lhv = getLhv();
        Object rhv = getRhv();

        if (getOp().getValue().equals("+")) {
            if (lhv != null && rhv != null) {
                try {
                    return (int) lhv + (int) rhv;
                }
                catch (ClassCastException e) {
                    return null;
                }
            }
        }
        else if (getOp().getValue().equals("-")) {
            if (lhv != null && rhv != null) {
                try {
                    return (int) lhv - (int) rhv;
                }
                catch (ClassCastException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public static boolean isOp(Token token) {
        return TermOp.isOp(token.getValue());
    }

    public static boolean isOp(String op) {
        switch (op) {
            case "+":
            case "-":
                return true;
            default:
                return false;
        }
    }
}
