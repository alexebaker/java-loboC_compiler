package Parser.Nodes;

import Errors.Error;
import Errors.SyntaxError;
import Tokenizer.TokenReader;
import Tokenizer.Tokens.IdentifierToken;
import Tokenizer.Tokens.Token;
import Compiler.*;
import Types.Type;

import java.util.Vector;

public class Def extends ASTNode {
    private ASTNode typeSpec;
    private Vector<ASTNode> varNames;

    public Def() {
        typeSpec = null;
        varNames = new Vector<>();
    }

    public void setTypeSpec(ASTNode typeSpec) {
        this.typeSpec = typeSpec;
    }

    public ASTNode getTypeSpec() {
        return typeSpec;
    }

    public void addVarName(ASTNode varName) {
        varNames.add(varName);
    }

    public Vector<ASTNode> getVarNames() {
        return varNames;
    }

    @Override
    public String getASTR(int indentDepth, CompilerState cs) {
        return typeSpec.getASTR(indentDepth, cs);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("");
        str.append(typeSpec);
        str.append(" ");
        str.append(varNames.get(0));
        for (int idx = 1; idx < varNames.size(); idx++) {
            str.append(",");
            str.append(varNames.get(idx));
        }
        str.append(";");
        return str.toString();
    }

    public static ASTNode parse(CompilerState cs, SymbolTable st) throws Error {
        TokenReader tr = cs.getTr();
        Def def = new Def();
        def.setTypeSpec(TypeSpec.parse(cs, st));

        if (!IdentifierToken.isToken(tr.peek())) {
            throw new SyntaxError(tr.read(), "IDENTIFIER");
        }

        while (IdentifierToken.isToken(tr.peek())) {
            Identifier varName = (Identifier) Identifier.parse(cs, st);
            def.addVarName(varName);
            if (!st.addDeclaration(varName.getToken(), def.getNodeType(cs))) {
                throw new SyntaxError(varName.getToken(), "Undeclared Variable");
            }

            if (tr.peek().getValue().equals(",")) {
                tr.read();
            }
            else {
                break;
            }
        }

        if (tr.peek().getValue().equals(";")) {
            tr.read();
        }
        else {
            for (ASTNode varName : def.getVarNames()) {
                st.removeDeclaration(((Identifier) varName).getToken());
            }
            throw new SyntaxError(tr.read(), ";");
        }
        return def;
    }

    public static boolean beginsDef(Token token) {
        return Def.beginsDef(token.getValue());
    }

    public static boolean beginsDef(String str) {
        return PrimType.isType(str);
    }

    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            setType(typeSpec.getNodeType(cs));
        }
        return getType();
    }

    public ASTNode foldConstants() {
        typeSpec = typeSpec.foldConstants();
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
