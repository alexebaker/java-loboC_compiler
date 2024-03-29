package Parser.Operators;

import Compiler.*;
import Parser.Nodes.ASTNode;
import Parser.Nodes.Number;
import Tokenizer.Tokens.NumberToken;
import Tokenizer.Tokens.Token;
import Types.PrimType;
import Types.Type;
import Types.TypeEnum;

public class Operator extends ASTNode {
    private Token op;
    private ASTNode lhs;
    private ASTNode rhs;

    public Operator(Token op) {
        this.op = op;
        this.lhs = null;
        this.rhs = null;
    }

    public Token getOp() {
        return op;
    }

    public ASTNode getLhs() {
        return lhs;
    }

    public ASTNode getRhs() {
        return rhs;
    }

    public void setLhs(ASTNode lhs) {
        this.lhs = lhs;
    }

    public void setRhs(ASTNode rhs) {
        this.rhs = rhs;
    }

    @Override
    public String getASTR(int indentDepth, CompilerState cs) {
        StringBuilder str = new StringBuilder();

        str.append(getTypePrefix(cs));
        str.append("(");
        if (lhs != null) str.append(lhs.getASTR(indentDepth, cs));
        str.append(op.getValue());
        if (rhs != null) str.append(rhs.getASTR(indentDepth, cs));
        str.append(")");
        return str.toString();
    }

    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            String op = getOp().getValue();
            if (op.equals(",")) {
                if (rhs != null) {
                    setType(rhs.getNodeType(cs));
                }
            }
            else if (op.equals("||") || op.equals("&&")) {
                if (lhs != null && (lhs.getNodeType(cs).getTypeEnum() == TypeEnum.BOOL) && rhs != null && (rhs.getNodeType(cs).getTypeEnum() == TypeEnum.BOOL)) {
                    setType(new PrimType(TypeEnum.BOOL));
                }
            }
        }
        return getType();
    }

    public ASTNode foldConstants() {
        String op = getOp().getValue();
        if (lhs != null) lhs = lhs.foldConstants();
        if (rhs != null) rhs = rhs.foldConstants();
        if (op.equals("&&") || op.equals("||")) {
           if (getValue() != null)  {
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
        }
        return this;
    }

    @Override
    public Object getValue() {
        String op = getOp().getValue();
        if (op.equals(",")) {
            if (rhs != null) {
                return rhs.getValue();
            }
        }
        else if (op.equals("||") || op.equals("&&")) {
            Object lhv = getLhv();
            Object rhv = getRhv();

            try {
                if (lhv instanceof Boolean) {
                    if (rhv instanceof Boolean) {
                        if (op.equals("||")) {
                            return (boolean) lhv || (boolean) rhv;
                        } else {
                            return (boolean) lhv && (boolean) rhv;
                        }
                    } else {
                        if (op.equals("||")) {
                            return (boolean) lhv || ((int) rhv != 0);
                        } else {
                            return (boolean) lhv && ((int) rhv != 0);
                        }
                    }
                } else {
                    if (rhv instanceof Boolean) {
                        if (op.equals("||")) {
                            return ((int) lhv != 0) || (boolean) rhv;
                        } else {
                            return ((int) lhv != 0) && (boolean) rhv;
                        }
                    } else {
                        if (op.equals("||")) {
                            return ((int) lhv != 0) || ((int) rhv != 0);
                        } else {
                            return ((int) lhv != 0) && ((int) rhv != 0);
                        }
                    }
                }
            }
            catch (ClassCastException|NullPointerException e) {
                return null;
            }
        }
        return null;
    }

    public Object getLhv() {
        return lhs != null ? lhs.getValue() : null;
    }

    public Object getRhv() {
        return rhs != null ? rhs.getValue() : null;
    }

    public Location getLocation() {
        return getOp().getLoc();
    }

    public boolean isAssignable() {
        if (getOp().getValue().equals(",")) {
            return getRhs().isAssignable();
        }
        return false;
    }

    public String getAsm(AsmData ad) {
        StringBuilder asm = new StringBuilder();
        if (getOp().getValue().equals("&&") || getOp().getValue().equals("||")) {
            asm.append(applyLogicalOp(ad));
        }
        else {
            AsmData lhsAD = new AsmData(ad);
            AsmData rhsAD = new AsmData(ad);
            if (getLhs() != null) asm.append(getLhs().getAsm(lhsAD));
            if (getRhs() != null) asm.append(getRhs().getAsm(rhsAD));
            asm.append(applyAsmOp(ad, lhsAD, rhsAD));
        }
        return asm.toString();
    }

    String applyAsmOp(AsmData ad, AsmData lhs, AsmData rhs) {
        StringBuilder asm = new StringBuilder();
        if (getOp().getValue().equals(",")) {
            ad.setAddr(rhs.getAddr());
        }
        return asm.toString();
    }

    String applyLogicalOp(AsmData ad) {
        StringBuilder asm = new StringBuilder();
        String shortCutLbl = "label" + ad.getLabelCounter();
        String newAddr = ad.getSt().getTmp(ad.getSt().addTmp(getType())).getAddr();
        String op;
        asm.append(getLhs().getAsm(ad));
        asm.append("\t" + getLhs().getLoadInst() + " $t0," + ad.getAddr() + "\n");
        asm.append("\t" + getStoreInst() + " $t0," + newAddr + "\n");
        if (getOp().getValue().equals("&&")) {
            asm.append("\tbeq $0,$t0," + shortCutLbl + "\n");
            op = "and";
        }
        else {
            asm.append("\tbne $0,$t0," + shortCutLbl + "\n");
            op = "or";
        }
        asm.append(getRhs().getAsm(ad));
        asm.append("\t" + getLoadInst() + " $t0," + newAddr + "\n");
        asm.append("\t" + getRhs().getLoadInst() + " $t1," + ad.getAddr() + "\n");
        asm.append("\t" + op + " $t2,$t0,$t1\n");
        asm.append("\t" + getStoreInst() + " $t2," + newAddr + "\n");
        asm.append(shortCutLbl + ":\n");
        ad.setAddr(newAddr);
        return asm.toString();
    }
}
