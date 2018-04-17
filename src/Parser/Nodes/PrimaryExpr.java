package Parser.Nodes;

import Errors.Error;
import Errors.SyntaxError;
import Tokenizer.Tokens.IdentifierToken;
import Tokenizer.Tokens.NumberToken;
import Tokenizer.TokenReader;
import Compiler.*;
import Tokenizer.Tokens.Token;
import Types.Type;

public class PrimaryExpr extends ASTNode {
    public static ASTNode parse(CompilerState cs, SymbolTable st) throws Error {
        TokenReader tr = cs.getTr();
        if (IdentifierToken.isToken(tr.peek())) {
            return Identifier.parse(cs, st);
        }
        else if (NumberToken.isToken(tr.peek())) {
            return Number.parse(cs, st);
        }
        else if (tr.peek().getValue().equals("(")) {
            tr.read();
            ASTNode node = Expr.parse(cs, st);
            if (tr.peek().getValue().equals(")")) {
                tr.read();
                return node;
            }
            else {
                throw new SyntaxError(tr.read(), ")");
            }
        }
        else {
            throw new SyntaxError(tr.read(), "IDENTIFIER, NUMBER, or (");
        }
    }

    public static boolean beginsPrimaryExpr(Token token) {
        return PrimaryExpr.beginsPrimaryExpr(token.getValue());
    }

    public static boolean beginsPrimaryExpr(String str) {
        return IdentifierToken.isToken(str) || NumberToken.isToken(str) || str.equals("(");
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
