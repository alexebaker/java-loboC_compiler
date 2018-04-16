package Parser.Nodes;

import Errors.Error;
import Parser.Operators.Operator;
import Parser.Operators.TermOp;
import Tokenizer.TokenReader;
import Compiler.*;
import Types.Type;

public class SimpleExpr extends ASTNode {
    public static ASTNode parse(CompilerState cs, SymbolTable st) throws Error {
        TokenReader tr = cs.getTr();
        ASTNode node = Term.parse(cs, st);
        while (TermOp.isOp(tr.peek())) {
            Operator temp = new TermOp(tr.read());
            temp.setLhs(node);
            temp.setRhs(Term.parse(cs, st));
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
