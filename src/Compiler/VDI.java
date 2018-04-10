package Compiler;

import Tokenizer.Tokens.Token;
import Types.Type;


public class VDI {
    private Token name;
    private String status;
    private Type type;

    public VDI(Token name, String status, Type type) {
        this.name = name;
        this.status = status;
        this.type = type;
    }

    public Token getName() {
        return name;
    }

    public Type getType() {
        return type;
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

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("");
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
}
