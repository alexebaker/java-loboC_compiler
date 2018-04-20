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
        if (getRhv() != null && getOp().getValue().equals("-")) {
            try {
                return 0 - (int) getRhv();
            }
            catch (ClassCastException e) {}
        }
        return null;
    }

    @Override
    String applyAsmOp(AsmData ad, AsmData lhs, AsmData rhs) {
        StringBuilder asm = new StringBuilder();
        asm.append("\t" + getRhs().getLoadInst() + " $t0," + rhs.getAddr() + "\n");

        if (getOp().getValue().equals("-")) {
            if (getType().getTypeEnum() == TypeEnum.UNSIGNED) {
                asm.append("\tnegu $t1,$t0\n");
            }
            else {
                asm.append("\tneg $t1,$t0\n");
            }
            String newAddr = ad.getSt().getTmp(ad.getSt().addTmp(getNodeType(null))).getAddr();
            asm.append("\tsw $t1," + newAddr + "\n");
            ad.setAddr(newAddr);
            return asm.toString();
        }
        else if (getOp().getValue().equals("--")) {
            asm.append("\tli $t1,0x01\n");
            if (getType().getTypeEnum() == TypeEnum.UNSIGNED) {
                asm.append("\tsubu $t3,$t0,$t1\n");
            }
            else {
                asm.append("\tsub $t3,$t0,$t1\n");
            }
            asm.append("\tmove $t0,$t3\n");
        }
        else if (getOp().getValue().equals("++")) {
            if (getType().getTypeEnum() == TypeEnum.UNSIGNED) {
                asm.append("\taddiu $t1,$t0,0x01\n");
            }
            else {
                asm.append("\taddi $t1,$t0,0x01\n");
            }
            asm.append("\tmove $t0,$t1\n");
        }
        else if (getOp().getValue().equals("&")) {
            asm.append("\tla $t0," + rhs.getAddr() + "\n");
            String newAddr = ad.getSt().getTmp(ad.getSt().addTmp(getNodeType(null))).getAddr();
            asm.append("\tsw $t0," + newAddr + "\n");
            ad.setAddr(newAddr);
            return asm.toString();
        }

        asm.append("\t" + getRhs().getStoreInst() + " $t0," + rhs.getAddr() + "\n");
        ad.setAddr(rhs.getAddr());
        return asm.toString();
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
