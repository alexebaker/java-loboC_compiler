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

public class EqOp extends Operator {
    public EqOp(Token token) {
        super(token);
    }

    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            Type lhs = getLhs().getNodeType(cs);
            Type rhs = getRhs().getNodeType(cs);
            if (lhs.equals(rhs) || (lhs.getTypeEnum() == TypeEnum.SIGNED && rhs.getTypeEnum() == TypeEnum.UNSIGNED) || (lhs.getTypeEnum() == TypeEnum.UNSIGNED && rhs.getTypeEnum() == TypeEnum.SIGNED)) {
                setType(new PrimType(TypeEnum.BOOL));
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
                String val = "0";
                if ((boolean) getValue()) {
                    val = "1";
                }
                ASTNode number = new Number(new NumberToken(val, getOp().getLoc()));
                number.setType(new PrimType(TypeEnum.BOOL));
                return number;
            }
            catch (ClassCastException e) {}
        }
        return this;
    }

    @Override
    public Object getValue() {
        Object lhv = getLhv();
        Object rhv = getRhv();

        if (getOp().getValue().equals("==")) {
            if (lhv != null && rhv != null) {
                return lhv.equals(rhv);
            }
        }
        else if (getOp().getValue().equals("!=")) {
            if (lhv != null && rhv != null) {
                return !lhv.equals(rhv);
            }
        }
        return null;
    }

    public static boolean isOp(Token token) {
        return EqOp.isOp(token.getValue());
    }

    public static boolean isOp(String op) {
        switch (op) {
            case "==":
            case "!=":
                return true;
            default:
                return false;
        }
    }
}
