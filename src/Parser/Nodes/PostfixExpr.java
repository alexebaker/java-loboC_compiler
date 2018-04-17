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
                    type = type.deRef();
                    if (type != null && arraySpec.getValue() != null) {
                        try {
                            if (((int) arraySpec.getValue()) >= ((ArrayType) type).getArraySize()) {
                                setType(new Type(TypeEnum.UNDEF));
                                String msg = "Array index out of bounds!";
                                cs.addError(new TypeError(msg, primaryExpr.getLocation()));
                                return getType();
                            }
                        } catch (ClassCastException e) { }
                    }
                }

                if (postfixExpr != null) {
                    Type tmp = postfixExpr.getNodeType(cs);
                    tmp.setOfType(type);
                    type = tmp;
                }

                setType(type);
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
            if (postfixExpr != null) {
                AsmData postfixAD = new AsmData(ad);
                asm.append(postfixExpr.getAsm(postfixAD));
                ad.setAddr(postfixAD.getAddr());
            }
            else if (arraySpec != null) {
                AsmData arrayAD = new AsmData(ad);
                asm.append(arraySpec.getAsm(arrayAD));
                ad.setAddr(arrayAD.getAddr());
            }
            else {
                ad.setAddr(primaryAD.getAddr());
            }
        }
        return asm.toString();
    }
}
