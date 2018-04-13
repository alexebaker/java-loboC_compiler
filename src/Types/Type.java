package Types;

public class Type {
    private TypeEnum typeEnum;
    private Type ofType;

    public Type() {
        this(TypeEnum.UNDEF);
    }

    public Type(TypeEnum typeEnum) {
        this.typeEnum = typeEnum;
        ofType = null;
    }

    public TypeEnum getTypeEnum() {
        return typeEnum;
    }

    public Type getOfType() {
        return ofType;
    }

    public void setOfType(Type ofType) {
        this.ofType = ofType;
    }

    public Type deRef() {
        if (typeEnum == TypeEnum.POINTER || typeEnum == TypeEnum.ARRAY) {
            return ofType;
        }
        return null;
    }

    public int getSize() {
        return 0;
    }

    public int getAlignment() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Type) {
            Type type = (Type) obj;
            return this.typeEnum.equals(type.getTypeEnum()) && (ofType == null || ofType.equals(type.getOfType()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (typeEnum.hashCode() + ofType.hashCode()) * 97;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if (ofType != null) {
            str.append(ofType);
        }

        if (!(typeEnum == TypeEnum.ARRAY || typeEnum == TypeEnum.POINTER)) {
            str.append(getTypeEnum().toString().toLowerCase());
        }
        return str.toString();
    }
}
