package Parser.Nodes;

import Errors.Error;
import Parser.Operators.EqOp;
import Parser.Operators.Operator;
import Tokenizer.TokenReader;
import Compiler.*;
import Types.Type;

public class EqExpr extends ASTNode {
    public static ASTNode parse(CompilerState cs, SymbolTable st) throws Error {
        TokenReader tr = cs.getTr();
        ASTNode node = RelExpr.parse(cs, st);
        while (EqOp.isOp(tr.peek())) {
            Operator temp = new EqOp(tr.read());
            temp.setLhs(node);
            temp.setRhs(RelExpr.parse(cs, st));
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

    public String getAsm(AsmData ad) {
        return "";
    }
}
