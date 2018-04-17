package Compiler;

public class AsmData {
    private AsmLabel ifTrue;
    private AsmLabel ifFalse;
    private FallThrough ft;
    private SymbolTable st;
    private String addr;

    public AsmData() {
        this(null, null, null, null);
    }

    public AsmData(AsmData ad) {
        this(ad.getIfTrue(), ad.getIfFalse(), ad.getFt(), ad.getSt());
    }

    public AsmData(AsmLabel ifTrue, AsmLabel ifFalse, FallThrough ft, SymbolTable st) {
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
        this.ft = ft;
        this.st = st;
        this.addr = "";
    }

    public AsmLabel getIfFalse() {
        return ifFalse;
    }

    public AsmLabel getIfTrue() {
        return ifTrue;
    }

    public FallThrough getFt() {
        return ft;
    }

    public SymbolTable getSt() {
        return st;
    }

    public String getAddr() {
        return addr;
    }

    public void setFt(FallThrough ft) {
        this.ft = ft;
    }

    public void setIfFalse(AsmLabel ifFalse) {
        this.ifFalse = ifFalse;
    }

    public void setIfTrue(AsmLabel ifTrue) {
        this.ifTrue = ifTrue;
    }

    public void setSt(SymbolTable st) {
        this.st = st;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
