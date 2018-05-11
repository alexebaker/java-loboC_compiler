package Parser.Operators;

import Errors.TypeError;
import Parser.Nodes.Number;
import Parser.Nodes.ASTNode;
import Tokenizer.Tokens.NumberToken;
import Tokenizer.Tokens.Token;
import Types.PointerType;
import Types.PrimType;
import Types.Type;
import Types.TypeEnum;
import Compiler.*;

public class RelOp extends Operator {
    public RelOp(Token token) {
        super(token);
    }

    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            Type lhs = getLhs().getNodeType(cs);
            Type rhs = getRhs().getNodeType(cs);
            if (PointerType.isType(lhs) || lhs.getTypeEnum() == TypeEnum.SIGNED || lhs.getTypeEnum() == TypeEnum.UNSIGNED) {
                if (lhs.equals(rhs)) {
                    setType(new PrimType(TypeEnum.BOOL));
                }
                else {
                    setType(new Type(TypeEnum.UNDEF));
                    String msg = "Operator types must be identical, instead got '" + lhs + "' and '" + rhs + "'";
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

        if (getOp().getValue().equals("<")) {
            if (lhv != null && rhv != null) {
                try {
                    return (int) lhv < (int) rhv;
                }
                catch (ClassCastException e) {
                    return null;
                }
            }
        }
        else if (getOp().getValue().equals("<=")) {
            if (lhv != null && rhv != null) {
                try {
                    return (int) lhv <= (int) rhv;
                }
                catch (ClassCastException e) {
                    return null;
                }
            }
        }
        else if (getOp().getValue().equals(">")) {
            if (lhv != null && rhv != null) {
                try {
                    return (int) lhv > (int) rhv;
                }
                catch (ClassCastException e) {
                    return null;
                }
            }
        }
        else if (getOp().getValue().equals(">=")) {
            if (lhv != null && rhv != null) {
                try {
                    return (int) lhv >= (int) rhv;
                }
                catch (ClassCastException e) {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    String applyAsmOp(AsmData ad, AsmData lhs, AsmData rhs) {
        StringBuilder asm = new StringBuilder();
        asm.append("\t" + getLhs().getLoadInst() + " $t0," + lhs.getAddr() + "\n");
        asm.append("\t" + getRhs().getLoadInst() + " $t1," + rhs.getAddr() + "\n");

        if (getOp().getValue().equals("<")) {
            if (getLhs().getType().getTypeEnum() == TypeEnum.UNSIGNED) {
                asm.append("\tsltu $t3,$t0,$t1\n");
            }
            else {
                asm.append("\tslt $t3,$t0,$t1\n");
            }
        }
        else if (getOp().getValue().equals("<=")) {
            if (getLhs().getType().getTypeEnum() == TypeEnum.UNSIGNED) {
                asm.append("\tsleu $t3,$t0,$t1\n");
            }
            else {
                asm.append("\tsle $t3,$t0,$t1\n");
            }
        }
        else if (getOp().getValue().equals(">")) {
            if (getLhs().getType().getTypeEnum() == TypeEnum.UNSIGNED) {
                asm.append("\tsgtu $t3,$t0,$t1\n");
            }
            else {
                asm.append("\tsgt $t3,$t0,$t1\n");
            }
        }
        else if (getOp().getValue().equals(">=")) {
            if (getLhs().getType().getTypeEnum() == TypeEnum.UNSIGNED) {
                asm.append("\tsgeu $t3,$t0,$t1\n");
            }
            else {
                asm.append("\tsge $t3,$t0,$t1\n");
            }
        }

        String newAddr = ad.getSt().getTmp(ad.getSt().addTmp(getNodeType(null))).getAddr();
        asm.append("\t" + getStoreInst() + " $t3," + newAddr + "\n");
        ad.setAddr(newAddr);
        return asm.toString();
    }

    public static boolean isOp(Token token) {
        return RelOp.isOp(token.getValue());
    }

    public static boolean isOp(String op) {
        switch (op) {
            case "<":
            case "<=":
            case ">":
            case ">=":
                return true;
            default:
                return false;
        }
    }


}
