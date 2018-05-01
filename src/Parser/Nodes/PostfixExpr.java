package Parser.Nodes;

import Errors.Error;
import Errors.TypeError;
import Parser.Operators.Operator;
import Parser.Operators.PostunOp;
import Tokenizer.TokenReader;
import Compiler.*;
import Types.ArrayType;
import Types.Type;
import Types.TypeEnum;

public class PostfixExpr extends ASTNode {
    private ASTNode primaryExpr;
    private ASTNode postfixExpr;
    private ASTNode arraySpec;

    public PostfixExpr() {
        this.primaryExpr = null;
        this.postfixExpr = null;
        this.arraySpec = null;
    }

    public void setPostfixExpr(ASTNode postfixExpr) {
        this.postfixExpr = postfixExpr;
    }

    public void setArraySpec(ASTNode arraySpec) {
        this.arraySpec = arraySpec;
    }

    public void setPrimaryExpr(ASTNode primaryExpr) {
        this.primaryExpr = primaryExpr;
    }

    @Override
    public String getASTR(int indentDepth, CompilerState cs) {
        StringBuilder str = new StringBuilder();
        if (primaryExpr != null) {
            str.append(getTypePrefix(cs));
            str.append("(");
            str.append(primaryExpr.getASTR(indentDepth, cs));

            if (arraySpec != null) {
                str.append(arraySpec.getASTR(indentDepth, cs));
            }

            if (postfixExpr != null) {
                str.append(postfixExpr.getASTR(indentDepth, cs));
            }
            str.append(")");
        }
        return str.toString();
    }

    public static ASTNode parse(CompilerState cs, SymbolTable st) throws Error {
        TokenReader tr = cs.getTr();
        ASTNode node = null;
        if (PrimaryExpr.beginsPrimaryExpr(tr.peek())) {
            node = PrimaryExpr.parse(cs, st);
            while (PostunOp.isOp(tr.peek()) || tr.peek().getValue().equals("[")) {
                if (PostunOp.isOp(tr.peek())) {
                    Operator temp = new PostunOp(tr.read());
                    temp.setLhs(node);
                    temp.setRhs(PostfixExpr.parse(cs, st));
                    node = temp;
                } else {
                    PostfixExpr temp = new PostfixExpr();
                    temp.setPrimaryExpr(node);
                    temp.setArraySpec(ArraySpec.parse(cs, st));
                    temp.setPostfixExpr(PostfixExpr.parse(cs, st));
                    node = temp;
                }
            }
        }
        return node;
    }

    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            if (primaryExpr != null) {
                Type type = primaryExpr.getNodeType(cs);
                if (arraySpec != null && type != null) {
                    if (type.deRef() != null && arraySpec.getValue() != null) {
                        try {
                            if (((int) arraySpec.getValue()) >= ((ArrayType) type.deRef()).getArraySize()) {
                                setType(new Type(TypeEnum.UNDEF));
                                String msg = "Array index out of bounds!";
                                cs.addError(new TypeError(msg, primaryExpr.getLocation()));
                                return getType();
                            }
                        } catch (ClassCastException e) { }
                    }
                    setType(type.deRef());
                }
            }
        }
        return getType();
    }

    public ASTNode foldConstants() {
        if (primaryExpr != null) {
            primaryExpr = primaryExpr.foldConstants();
            arraySpec = arraySpec != null ? arraySpec.foldConstants() : null;
            postfixExpr = postfixExpr != null ?  postfixExpr.foldConstants() : null;
        }
        return this;
    }

    public Object getValue() {
        return primaryExpr != null ? primaryExpr.getValue() : null;
    }

    public Location getLocation() {
        return primaryExpr != null ? primaryExpr.getLocation() : null;
    }

    public boolean isAssignable() {
        if (primaryExpr != null) {
            if (postfixExpr != null) {
                return primaryExpr.isAssignable() && postfixExpr.isAssignable();
            }
            else if (arraySpec != null) {
                return primaryExpr.isAssignable() && arraySpec.isAssignable();
            }
            return primaryExpr.isAssignable();
        }
        return false;
    }

    public String getAsm(AsmData ad) {
        StringBuilder asm = new StringBuilder();
        if (primaryExpr != null) {
            AsmData primaryAD = new AsmData(ad);
            asm.append(primaryExpr.getAsm(primaryAD));
            ad.setAddr(primaryAD.getAddr());

            if (arraySpec != null) {
                if (primaryExpr.getType().getTypeEnum() == TypeEnum.ARRAY) {
                    Type deRefType = primaryExpr.getType().deRef();
                    String load = deRefType.getTypeEnum() == TypeEnum.BOOL ? "lbu" : "lw";
                    String store = deRefType.getTypeEnum() == TypeEnum.BOOL ? "sb" : "sw";
                    String stepSize = "0x0" + Integer.toHexString(deRefType.getSize());
                    AsmData arrayAD = new AsmData(ad);
                    asm.append(arraySpec.getAsm(arrayAD));
                    asm.append("\tla $t0," + primaryAD.getAddr() + "\n");
                    asm.append("\t" + arraySpec.getLoadInst() + " $t1," + arrayAD.getAddr() + "\n");
                    asm.append("\tli $t2," + stepSize + "\n");
                    asm.append("\tmul $t3,$t1,$t2\n");
                    asm.append("\tadd $t4,$t0,$t3\n");
                    asm.append("\t" + load + " $t5,0($t4)\n");
                    String newAddr = ad.getSt().getTmp(ad.getSt().addTmp(deRefType)).getAddr();
                    asm.append("\t" + store + " $t5," + newAddr + "\n");
                    ad.setAddr(newAddr);
                }
                else if (primaryExpr.getType().getTypeEnum() == TypeEnum.POINTER) {
                    Type pointerType = primaryExpr.getType();
                    asm.append("\tlw $t9," + primaryAD.getAddr() + "\n");
                    ad.setAddr("0($t9)");
                }
            }

            if (postfixExpr != null) {
                AsmData postfixAD = new AsmData(ad);
                asm.append(postfixExpr.getAsm(postfixAD));
                ad.setAddr(postfixAD.getAddr());
            }
        }
        return asm.toString();
    }
}
