package Compiler;

import Types.Type;

public class TmpVDI extends VDI {
    private String id;

    public TmpVDI() {
        this("", null, null);
    }

    public TmpVDI(String id, Type type, VDI parent) {
        super(null, "", type, parent);

        this.id = id;
        setOffset(getOffset()+4);
    }

    public String getID() {
        return id;
    }

    @Override
    public String getAddr() {
        return "-" + super.getAddr();
    }
}
