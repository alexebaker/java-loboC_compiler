package Parser.Operators;

import Compiler.*;
import Errors.TypeError;
import Parser.Nodes.ASTNode;
import Parser.Nodes.Number;
import Tokenizer.Tokens.NumberToken;
import Tokenizer.Tokens.Token;
import Types.PrimType;
import Types.Type;
import Types.TypeEnum;

public class ExpoOp extends Operator {
    public ExpoOp(Token token) {
        super(token);
    }

    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            Type lhs = getLhs().getNodeType(cs);
            Type rhs = getRhs().getNodeType(cs);
            if (rhs.getTypeEnum() == TypeEnum.UNSIGNED) {
                if (lhs.getTypeEnum() == TypeEnum.UNSIGNED) {
                    setType(new PrimType(TypeEnum.UNSIGNED));
                }
                else if (lhs.getTypeEnum() == TypeEnum.SIGNED) {
                    setType(new PrimType(TypeEnum.SIGNED));
                }
                else {
                    setType(new Type(TypeEnum.UNDEF));
                    String msg = "Left hand side for operator '" + getOp() + "' must be SIGNED or UNSIGNED, instead got '" + lhs + "'";
                    cs.addError(new TypeError(msg, getLocation()));
                }
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

        if (lhv != null && rhv != null) {
            try {
                return (int) Math.pow((int) lhv, (int) rhv);
            }
            catch (ClassCastException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    String applyAsmOp(AsmData ad, AsmData lhs, AsmData rhs) {
        StringBuilder asm = new StringBuilder();
        return asm.toString();
    }

    public static boolean isOp(Token token) {
        return ExpoOp.isOp(token.getValue());
    }

    public static boolean isOp(String op) {
        switch (op) {
            case "**":
                return true;
            default:
                return false;
        }
    }
}
