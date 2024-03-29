package Compiler;

import Tokenizer.Tokens.Token;
import Types.Type;


public class VDI {
    private Token name;
    private String status;
    private Type type;
    private int offset;
    private VDI parent;

    public VDI() {
        this(null, "", null);
    }

    public VDI(Token name, String status, Type type) {
        this(name, status, type, null);
    }

    public VDI(Token name, String status, Type type, VDI parent) {
        this.name = name;
        this.status = status;
        this.type = type;
        this.parent = parent;
        this.offset = calcOffset(type, parent);
    }

    public Token getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public int getOffset() {
        return offset;
    }

    public VDI getParent() {
        return parent;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getAddr() {
        return offset + "($gp)";
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(name);
        str.append(" ");
        str.append(status);
        str.append(" ");

        if (type != null) {
            str.append(type);
        }
        else {
            str.append("unknown");
        }

        str.append(" ");
        str.append(offset);
        return str.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VDI) {
            return equals((VDI) obj);
        }
        return false;
    }

    public boolean equals(VDI vdi) {
        return name.equals(vdi.getName()) && status.equals(vdi.getStatus()) && type.equals(vdi.getType());
    }

    @Override
    public int hashCode() {
        return (name.hashCode() * status.hashCode() - type.hashCode()) * 27;
    }

    private int calcOffset(Type type, VDI parent) {
        int offsetX = parent == null ? 0 : parent.getOffset();
        int sizeX = parent == null ? 0 : parent.getType().getSize();
        int alignY = type.getAlignment();
        return (offsetX + sizeX + alignY - 1) / alignY * alignY;
    }
}
