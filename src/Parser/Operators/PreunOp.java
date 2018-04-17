package Parser.Operators;

import Errors.TypeError;
import Parser.Nodes.ASTNode;
import Tokenizer.Tokens.Token;
import Types.PointerType;
import Types.PrimType;
import Types.Type;
import Types.TypeEnum;
import Compiler.*;

public class PreunOp extends Operator {
    public PreunOp(Token token) {
        super(token);
    }

    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            Type type = getRhs().getNodeType(cs);
            if (getOp().getValue().equals("--") || getOp().getValue().equals("++")) {
                if (PointerType.isType(type) || type.getTypeEnum() == TypeEnum.UNSIGNED || type.getTypeEnum() == TypeEnum.SIGNED) {
                    setType(type);
                }
            }
            else if (getOp().getValue().equals("-")) {
                if (type.getTypeEnum() == TypeEnum.UNSIGNED || type.getTypeEnum() == TypeEnum.SIGNED) {
                    setType(new PrimType(type.getTypeEnum()));
                }
            }
            else if (getOp().getValue().equals("&")) {
                PointerType newType = new PointerType();
                newType.setOfType(type);
                setType(newType);
            }
            else {
                setType(new Type(TypeEnum.UNDEF));
                String msg = "Cannot apply operator '" + getOp() + "' to type '" + type + "'";
                cs.addError(new TypeError(msg, getLocation()));
            }
        }
        return getType();
    }

    @Override
    public ASTNode foldConstants() {
        setRhs(getRhs().foldConstants());
        return this;
    }

    @Override
    public Object getValue() {
        if (getOp().getValue().equals("-")) {
            try {
                return 0 - (int) getRhv();
            }
            catch (ClassCastException e) {}
        }
        return null;
    }

    @Override
    String applyAsmOp(AsmData ad, AsmData lhs, AsmData rhs) {
        return "";
    }

    public static boolean isOp(Token token) {
        return PreunOp.isOp(token.getValue());
    }

    public static boolean isOp(String op) {
        switch (op) {
            case "-":
            case "--":
            case "++":
            case "&":
                return true;
            default:
                return false;
        }
    }
}
