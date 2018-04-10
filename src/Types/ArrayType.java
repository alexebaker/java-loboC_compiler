package Types;

public class ArrayType extends Type {
    private int size;

    public ArrayType() {
        this(0);
    }

    public ArrayType(int size) {
        super(TypeEnum.ARRAY);
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayType) {
            return super.equals(obj) && getSize() == ((ArrayType) obj).getSize();
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(super.toString());
        str.append("[");
        str.append(size);
        str.append("]");
        return str.toString();
    }

    public static boolean isType(Type type) {
        return type != null && ArrayType.isType(type.getTypeEnum());
    }

    public static boolean isType(TypeEnum typeEnum) {
        return typeEnum != null && typeEnum == TypeEnum.ARRAY;
    }
}
