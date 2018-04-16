package Parser.Nodes;

import Errors.Error;
import Errors.SyntaxError;
import Tokenizer.TokenReader;
import Tokenizer.Tokens.Token;
import Compiler.*;
import Types.Type;
import Types.TypeEnum;

public class PrimType extends ASTNode {
    private Token token;

    public PrimType(Token token) {
        this.token = token;
    }

    @Override
    public String getASTR(int indentDepth, CompilerState cs) {
        return token.getValue();
    }

    public static ASTNode parse(CompilerState cs, SymbolTable st) throws Error {
        TokenReader tr = cs.getTr();
        if (PrimType.isType(tr.peek())) {
            return new PrimType(tr.read());
        }
        else {
            throw new SyntaxError(tr.read(), "bool, unsigned, or signed");
        }
    }

    public static boolean isType(Token token) {
        return PrimType.isType(token.getValue());
    }

    public static boolean isType(String str) {
        switch (str) {
            case "bool":
            case "unsigned":
            case "signed":
                return true;
            default:
                return false;
        }
    }

    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            setType(new Types.PrimType(TypeEnum.valueOf(token.getValue().toUpperCase())));
        }
        return getType();
    }

    public ASTNode foldConstants() {
        return this;
    }

    public Object getValue() {
        return null;
    }

    public Location getLocation() {
        return token.getLoc();
    }

    public boolean isAssignable() {
        return false;
    }

    public String getAsm(AsmLabel ifTrue, AsmLabel ifFalse, FallThrough ft) {
        return "";
    }
}
