package Parser.Nodes;

import Errors.SyntaxError;
import Parser.Operators.Operator;
import Tokenizer.TokenReader;
import Compiler.*;
import Types.Type;

public class LogAndExpr extends ASTNode {
    public static ASTNode parse(CompilerState cs, SymbolTable st) throws SyntaxError {
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
}
