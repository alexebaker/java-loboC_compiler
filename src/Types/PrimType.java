package Types;


public class PrimType extends Type {
    public PrimType(TypeEnum typeEnum) {
        super(typeEnum);
    }

    public static boolean isType(Type type) {
        return type != null && PrimType.isType(type.getTypeEnum());
    }

    public static boolean isType(TypeEnum typeEnum) {
        return typeEnum != null && (typeEnum == TypeEnum.SIGNED || typeEnum == TypeEnum.UNSIGNED || typeEnum == TypeEnum.BOOL);
    }
}
