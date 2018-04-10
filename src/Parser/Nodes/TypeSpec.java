package Parser.Nodes;

import Errors.SyntaxError;
import Tokenizer.TokenReader;
import Compiler.*;
import Types.Type;

import java.util.Vector;

public class TypeSpec extends ASTNode {
    private ASTNode typeName;
    private Vector<ASTNode> arraySpecs;

    public TypeSpec() {
        typeName = null;
        arraySpecs = new Vector<>();
    }

    public void addArraySpec(ASTNode arraySpec) {
        arraySpecs.add(arraySpec);
    }

    public Vector<ASTNode> getArraySpecs() {
        return arraySpecs;
    }

    public void setTypeName(ASTNode typeName) {
        this.typeName = typeName;
    }

    public ASTNode getTypeName() {
        return typeName;
    }

    @Override
    public String getASTR(int indentDepth, CompilerState cs) {
        StringBuilder str = new StringBuilder("");
        if (typeName != null) {
            str.append(typeName.getASTR(indentDepth, cs));
            for (ASTNode arraySpec : arraySpecs) {
                str.append(arraySpec.getASTR(indentDepth, cs));
            }
        }
        return str.toString();
    }

    public static ASTNode parse(CompilerState cs, SymbolTable st) throws SyntaxError {
        TokenReader tr = cs.getTr();
        TypeSpec typeSpec = new TypeSpec();
        typeSpec.setTypeName(PrimType.parse(cs, st));

        while (tr.peek().getValue().equals("[")) {
            typeSpec.addArraySpec(ArraySpec.parse(cs, st));
        }
        return typeSpec;
    }

    public Type getNodeType(CompilerState cs) {
        if (getType() == null) {
            Type type = typeName.getNodeType(cs);
            Type tmp;
            for (ASTNode arraySpec : arraySpecs) {
                tmp = arraySpec.getNodeType(cs);
                tmp.setOfType(type);
                type = tmp;
            }
            setType(type);
        }
        return getType();
    }

    public ASTNode foldConstants() {
        for (int idx = 0; idx < arraySpecs.size(); idx++) {
            ASTNode arraySpec = arraySpecs.get(idx);
            arraySpecs.remove(idx);
            arraySpec = arraySpec.foldConstants();
            arraySpecs.add(idx, arraySpec);
        }
        return this;
    }

    public Object getValue() {
        return null;
    }

    public Location getLocation() {
        if (typeName != null) {
            return typeName.getLocation();
        }
        return null;
    }
}
