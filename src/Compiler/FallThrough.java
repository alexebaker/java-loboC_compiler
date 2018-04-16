package Compiler;

public enum FallThrough {
    FALL_TRUE,
    FALL_FALSE,
    FALL_EITHER,
    FALL_NEITHER;

    public FallThrough opposite() {
        if (this == FALL_TRUE) {
            return FALL_FALSE;
        }
        else if (this == FALL_FALSE) {
            return FALL_TRUE;
        }
        else if (this == FALL_EITHER) {
            return FALL_NEITHER;
        }
        else if (this == FALL_NEITHER) {
            return FALL_EITHER;
        }
        return FALL_NEITHER;
    }
}
