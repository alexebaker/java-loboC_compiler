package Parser.Nodes;

import Compiler.CompilerState;
import Compiler.Location;
import Types.Type;


public abstract class ASTNode {
    private Type type;

    public ASTNode() {
        this.type = null;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getASTR(int indentDepth, CompilerState cs) {
        StringBuilder str = new StringBuilder();
        for (int idx = 0; idx < indentDepth; idx++) {
            str.append("  ");
        }
        return str.toString();
    }

    public String getVSR(int indentDepth, CompilerState cs) {
        return getASTR(indentDepth, cs);
    }

    public String getBOTLPIF(CompilerState cs) {
        StringBuilder str = new StringBuilder();
        str.append(getVSR(0, cs));
        str.append(getASTR(0, cs));
        return str.toString();
    }

    public String getTypePrefix(CompilerState cs) {
        StringBuilder str = new StringBuilder();
        if (getNodeType(cs) != null) {
            String type = getNodeType(cs).toString();
            type = type.replaceAll("unsigned", "U");
            type = type.replaceAll("signed", "S");
            type = type.replaceAll("bool", "B");
            str.append(" ");
            str.append(type);
            str.append(":");
        }
        return str.toString();
    }

    public abstract Type getNodeType(CompilerState cs);
    public abstract ASTNode foldConstants();
    public abstract Object getValue();
    public abstract Location getLocation();
    public abstract boolean isAssignable();

    @Override
    public String toString() {
        return getASTR(0, null);
    }
}
