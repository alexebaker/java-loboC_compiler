package Types;

public class PointerType extends Type {
    public PointerType() {
        super(TypeEnum.POINTER);
    }

    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public int getAlignment() {
        return 4;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(super.toString());
        str.append("[]");
        return str.toString();
    }

    public static boolean isType(Type type) {
        return type != null && PointerType.isType(type.getTypeEnum());
    }

    public static boolean isType(TypeEnum typeEnum) {
        return typeEnum != null && typeEnum == TypeEnum.POINTER;
    }
}
