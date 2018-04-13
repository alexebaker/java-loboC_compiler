package Types;

public class ArrayType extends Type {
    private int arraySize;

    public ArrayType() {
        this(0);
    }

    public ArrayType(int arraySize) {
        super(TypeEnum.ARRAY);
        this.arraySize = arraySize;
    }

    public int getArraySize() {
        return arraySize;
    }

    @Override
    public int getSize() {
        return arraySize * getOfType().getSize();
    }

    @Override
    public int getAlignment() {
        return getOfType().getAlignment();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayType) {
            return super.equals(obj) && getArraySize() == ((ArrayType) obj).getArraySize();
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(super.toString());
        str.append("[");
        str.append(arraySize);
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
