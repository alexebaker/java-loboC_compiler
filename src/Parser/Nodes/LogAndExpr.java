package Parser.Nodes;

import Errors.Error;
import Parser.Operators.Operator;
import Tokenizer.TokenReader;
import Compiler.*;
import Types.Type;

public class LogAndExpr extends ASTNode {
    public static ASTNode parse(CompilerState cs, SymbolTable st) throws Error {
        TokenReader tr = cs.getTr();
        ASTNode node = EqExpr.parse(cs ,st);
        while (tr.peek().getValue().equals("&&")) {
            Operator temp = new Operator(tr.read());
            temp.setLhs(node);
            temp.setRhs(EqExpr.parse(cs ,st));
            node = temp;
        }
        return node;
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
