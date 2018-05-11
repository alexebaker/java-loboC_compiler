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
        String newAddr = ad.getSt().getTmp(ad.getSt().addTmp(getNodeType(null))).getAddr();
        asm.append("\t" + getLhs().getLoadInst() + " $t0," + lhs.getAddr() + "\n");
        asm.append("\t" + getRhs().getLoadInst() + " $t1," + rhs.getAddr() + "\n");
        String lbl1 = "label" + ad.getLabelCounter();
        String lbl2 = "label" + ad.getLabelCounter();
        asm.append("\tmove $t2,$0\n");
        asm.append("\tmove $t5,$t0\n");
        asm.append("\tli $t6,0x01\n");
        asm.append("\tsw $t6," + newAddr + "\n");
        asm.append(lbl1 + ":\n");
        asm.append("\tbgeu $t2,$t1," + lbl2 + "\n");
        if (getNodeType(null).getTypeEnum() == TypeEnum.UNSIGNED) {
                asm.append("\tmul $t3,$t0,$t5\n");
            }
            else {
                asm.append("\tmulo $t3,$t0,$t5\n");
            }
        asm.append("\tmove $t5,$t3\n");
        asm.append("\taddi $t4,$t2,0x01\n");
        asm.append("\tmove $t2,$t4\n");
        asm.append("\t" + getStoreInst() + " $t5," + newAddr + "\n");
        asm.append("\tj " + lbl1 + "\n");
        asm.append(lbl2 + ":\n");
        ad.setAddr(newAddr);
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
