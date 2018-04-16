package Parser.Nodes;

import Errors.Error;
import Parser.Operators.Operator;
import Parser.Operators.PreunOp;
import Tokenizer.TokenReader;
import Compiler.*;
import Types.Type;

public class Factor extends ASTNode {
    public static ASTNode parse(CompilerState cs, SymbolTable st) throws Error {
        TokenReader tr = cs.getTr();
        if (PreunOp.isOp(tr.peek())) {
            Operator op = new PreunOp(tr.read());
            op.setRhs(Factor.parse(cs, st));
            return op;
        }
        return PostfixExpr.parse(cs, st);
    }

    public Type getNodeType(CompilerState cs) {
        return getType();
    }

    public ASTNode foldConstants() {
        return this;
    }

    public Object getValue() {
        return null;
    }

    public Location getLocation() {
        return null;
    }

    public boolean isAssignable() {
        return false;
    }

    public String getAsm(AsmLabel ifTrue, AsmLabel ifFalse, FallThrough ft) {
        return "";
    }
}
