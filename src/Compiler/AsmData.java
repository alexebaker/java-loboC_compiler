package Compiler;

public class AsmData {
    private AsmLabel ifTrue;
    private AsmLabel ifFalse;
    private FallThrough ft;
    private SymbolTable st;
    private String addr;
    private int labelCounter;

    public AsmData() {
        this(null, null, null, null);
    }

    public AsmData(AsmData ad) {
        this(ad.getIfTrue(), ad.getIfFalse(), ad.getFt(), ad.getSt());
        this.labelCounter = ad.getLabelCounter();
    }

    public AsmData(AsmLabel ifTrue, AsmLabel ifFalse, FallThrough ft, SymbolTable st) {
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
        this.ft = ft;
        this.st = st;
        this.addr = "0($gp)";
        this.labelCounter = 0;
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

    public int getLabelCounter() {
        labelCounter += 1;
        return labelCounter;
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

    public void setLabelCounter(int labelCounter) {
        this.labelCounter = labelCounter;
    }
}
