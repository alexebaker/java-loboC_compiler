package Parser.Operators;

import Compiler.*;
import Errors.TypeError;
import Parser.Nodes.ASTNode;
import Tokenizer.Tokens.Token;
import Types.PointerType;
import Types.PrimType;
import Types.Type;
import Types.TypeEnum;

public class PostunOp extends Operator {
    public PostunOp(Token token) {
        super(token);
    }

    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            Type type = getLhs().getNodeType(cs);
            if (getOp().getValue().equals("!")) {
                if (type.getTypeEnum() == TypeEnum.UNSIGNED || type.getTypeEnum() == TypeEnum.SIGNED) {
                    setType(new PrimType(TypeEnum.UNSIGNED));
                }
                else {
                    setType(new Type(TypeEnum.UNDEF));
                    String msg = "Cannot apply operator '" + getOp() + "' to type '" + type + "'";
                    cs.addError(new TypeError(msg, getLocation()));
                }
            }
            else {
                if (PointerType.isType(type) || type.getTypeEnum() == TypeEnum.UNSIGNED || type.getTypeEnum() == TypeEnum.SIGNED) {
                    setType(type);
                } else {
                    setType(new Type(TypeEnum.UNDEF));
                    String msg = "Cannot apply operator '" + getOp() + "' to type '" + type + "'";
                    cs.addError(new TypeError(msg, getLocation()));
                }
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
        Object lhv = getLhv();

        if (getOp().getValue().equals("!") && lhv != null) {
            try {
                int factor = 1;
                int x = (int) lhv;

                for (int i = 1; i <= x; i++) {
                    factor *= i;
                }
                return factor;
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
        String newAddr = ad.getSt().getTmp(ad.getSt().addTmp(getNodeType(null))).getAddr();
        asm.append("\t" + getLhs().getLoadInst() + " $t0," + lhs.getAddr() + "\n");

        if (getOp().getValue().equals("--")) {
            asm.append("\tli $t1,0x01\n");
            if (getType().getTypeEnum() == TypeEnum.UNSIGNED) {
                asm.append("\tsubu $t2,$t0,$t1\n");
            }
            else {
                asm.append("\tsub $t2,$t0,$t1\n");
            }
            asm.append("\t" + getStoreInst() + " $t0," + newAddr + "\n");
            asm.append("\t" + getLhs().getStoreInst() + " $t2," + lhs.getAddr() + "\n");
        }
        else if (getOp().getValue().equals("++")) {
            asm.append("\tli $t1,0x01\n");
            if (getNodeType(null).getTypeEnum() == TypeEnum.UNSIGNED) {
                asm.append("\taddiu $t2,$t0,0x01\n");
            }
            else {
                asm.append("\taddi $t2,$t0,0x01\n");
            }
            asm.append("\t" + getStoreInst() + " $t0," + newAddr + "\n");
            asm.append("\t" + getLhs().getStoreInst() + " $t2," + lhs.getAddr() + "\n");
        }
        else if (getOp().getValue().equals("!")) {
            String lbl1 = "label" + ad.getLabelCounter();
            String lbl2 = "label" + ad.getLabelCounter();
            asm.append("\tmove $t1,$t0\n");
            asm.append("\tli $t2,0x01\n");
            asm.append("\tsw $t2," + newAddr + "\n");
            asm.append(lbl1 + ":\n");
            if (getLhs().getNodeType(null).getTypeEnum() == TypeEnum.UNSIGNED) {
                asm.append("\tbleu $t1,$0," + lbl2 + "\n");
            }
            else {
                asm.append("\tble $t1,$0," + lbl2 + "\n");
            }
            asm.append("\tmul $t3,$t1,$t2\n");
            asm.append("\tmove $t2,$t3\n");
            asm.append("\taddi $t4,$t1,-0x01\n");
            asm.append("\tmove $t1,$t4\n");
            asm.append("\t" + getStoreInst() + " $t2," + newAddr + "\n");
            asm.append("\tj " + lbl1 + "\n");
            asm.append(lbl2 + ":\n");
        }

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
            case "!":
                return true;
            default:
                return false;
        }
    }
}
