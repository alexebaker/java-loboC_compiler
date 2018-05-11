package Parser.Nodes;

import Errors.Error;
import Parser.Operators.ExpoOp;
import Parser.Operators.Operator;
import Tokenizer.TokenReader;
import Compiler.*;
import Types.Type;

public class Expo extends ASTNode {
    public static ASTNode parse(CompilerState cs, SymbolTable st) throws Error {
        TokenReader tr = cs.getTr();
        ASTNode node = Factor.parse(cs, st);
        while (ExpoOp.isOp(tr.peek())) {
            Operator temp = new ExpoOp(tr.read());
            temp.setLhs(node);
            temp.setRhs(Factor.parse(cs, st));
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
