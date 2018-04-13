package Types;


public class PrimType extends Type {
    public PrimType(TypeEnum typeEnum) {
        super(typeEnum);
    }

    @Override
    public int getSize() {
        if (getTypeEnum() == TypeEnum.BOOL) {
            return 1;
        }
        else if (getTypeEnum() == TypeEnum.SIGNED || getTypeEnum() == TypeEnum.UNSIGNED) {
            return 4;
        }
        return 0;
    }

    @Override
    public int getAlignment() {
        if (getTypeEnum() == TypeEnum.SIGNED || getTypeEnum() == TypeEnum.UNSIGNED) {
            return 4;
        }
        return 1;
    }

    public static boolean isType(Type type) {
        return type != null && PrimType.isType(type.getTypeEnum());
    }

    public static boolean isType(TypeEnum typeEnum) {
        return typeEnum != null && (typeEnum == TypeEnum.SIGNED || typeEnum == TypeEnum.UNSIGNED || typeEnum == TypeEnum.BOOL);
    }
}
