package Parser.Nodes;

import Errors.SyntaxError;
import Tokenizer.TokenReader;
import Tokenizer.Tokens.NumberToken;
import Tokenizer.Tokens.Token;
import Compiler.*;
import Types.PrimType;
import Types.Type;
import Types.TypeEnum;

public class Number extends ASTNode {
    private Token token;

    public Number(Token token) {
        this.token = token;
    }

    public String getASTR(int indentDepth, CompilerState cs) {
        StringBuilder str = new StringBuilder("");
        str.append(getTypePrefix(cs));
        str.append(token.getValue());
        return str.toString();
    }

    public static ASTNode parse(CompilerState cs, SymbolTable st) throws SyntaxError {
        TokenReader tr = cs.getTr();
        if (NumberToken.isToken(tr.peek())) {
            return new Number(tr.read());
        }
        else {
            throw new SyntaxError(tr.read(), "NUMBER");
        }
    }

    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            setType(new PrimType(TypeEnum.SIGNED));
        }
        return getType();
    }

    public ASTNode foldConstants() {
        return this;
    }

    public Object getValue() {
        return Integer.parseInt(token.getValue());
    }

    public Location getLocation() {
        return token.getLoc();
    }
}