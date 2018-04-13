package Parser.Nodes;

import Errors.Error;
import Parser.Operators.Operator;
import Parser.Operators.RelOp;
import Tokenizer.TokenReader;
import Compiler.*;
import Types.Type;

public class RelExpr extends ASTNode {
    public static ASTNode parse(CompilerState cs, SymbolTable st) throws Error {
        TokenReader tr = cs.getTr();
        ASTNode node = SimpleExpr.parse(cs, st);
        while (RelOp.isOp(tr.peek())) {
            Operator temp = new RelOp(tr.read());
            temp.setLhs(node);
            temp.setRhs(SimpleExpr.parse(cs, st));
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
}
