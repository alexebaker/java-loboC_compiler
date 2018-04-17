package Parser.Operators;

import Compiler.*;
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

    @Override
    String applyAsmOp(AsmData ad, AsmData lhs, AsmData rhs) {
        StringBuilder asm = new StringBuilder();
        asm.append("\tlw $t0," + lhs.getAddr() + "\n");

        if (getOp().getValue().equals("--")) {
            asm.append("\tli $t1,0x01\n");
            if (getType().getTypeEnum() == TypeEnum.UNSIGNED) {
                asm.append("\tsubu $t2,$t0,$t1\n");
            }
            else {
                asm.append("\tsub $t2,$t0,$t1\n");
            }
        }
        else if (getOp().getValue().equals("++")) {
            asm.append("\tli $t1,0x01\n");
            if (getType().getTypeEnum() == TypeEnum.UNSIGNED) {
                asm.append("\taddiu $t2,$t0,0x01\n");
            }
            else {
                asm.append("\taddi $t2,$t0,0x01\n");
            }
        }

        String newAddr = ad.getSt().getTmp(ad.getSt().addTmp(getNodeType(null))).getAddr();
        asm.append("\tsw $t0," + newAddr + "\n");
        asm.append("\tsw $t2," + lhs.getAddr() + "\n");
        ad.setAddr(newAddr);
        return asm.toString();
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
