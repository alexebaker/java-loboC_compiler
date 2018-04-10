package Parser.Operators;

import Compiler.CompilerState;
import Errors.TypeError;
import Parser.Nodes.ASTNode;
import Tokenizer.Tokens.Token;
import Types.PointerType;
import Types.Type;
import Types.TypeEnum;

public class PostunOp extends Operator {
    public PostunOp(Token token) {
        super(token);
    }

    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            Type type = getLhs().getNodeType(cs);
            if (PointerType.isType(type) || type.getTypeEnum() == TypeEnum.UNSIGNED || type.getTypeEnum() == TypeEnum.SIGNED) {
                setType(type);
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
        setLhs(getLhs().foldConstants());
        return this;
    }

    @Override
    public Object getValue() {
        return null;
    }

    public static boolean isOp(Token token) {
        return PostunOp.isOp(token.getValue());
    }

    public static boolean isOp(String op) {
        switch (op) {
            case "--":
            case "++":
                return true;
            default:
                return false;
        }
    }
}
