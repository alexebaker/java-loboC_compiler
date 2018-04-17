package Compiler;

import Tokenizer.Tokens.Token;
import Types.Type;
import Types.TypeEnum;

import java.util.HashMap;
import java.util.TreeSet;


public class SymbolTable {
    private HashMap<Token, VDI> symbolTable;
    private HashMap<String, TmpVDI> tmpTable;
    private boolean inDef;
    private SymbolTable parent;
    private Token lastAdded;
    private String lastTmp;
    private int tmpCount;

    public SymbolTable() {
        this(null);
    }

    public SymbolTable(SymbolTable parent) {
        symbolTable = new HashMap<>();
        tmpTable = new HashMap<>();
        inDef = false;
        this.parent = parent;
        this.lastAdded = parent == null ? null : parent.getLastAdded();
        this.lastTmp = "";
        this.tmpCount = parent == null ? 0 : parent.getTmpCount();
    }

    public VDI getVDI(Token name) {
        if (!symbolTable.containsKey(name)) {
            if (parent != null) {
                return parent.getVDI(name);
            }
            return null;
        }
        return symbolTable.get(name);
    }

    public Token getLastAdded() {
        return lastAdded == null ? (parent == null ? null : parent.getLastAdded()) : lastAdded;
    }

    public String getLastTmp() {
        return lastTmp.length() == 0 ? (parent == null ? "" : parent.getLastTmp()) : lastTmp;
    }

    public boolean isInDef() {
        return inDef;
    }

    public void setInDef(boolean inDef) {
        this.inDef = inDef;
    }

    public boolean addDeclaration(Token name, Type type) {
        if (!symbolTable.containsKey(name)) {
            symbolTable.put(name, new VDI(name, "unused", type, getVDI(getLastAdded())));
            lastAdded = name;
            return true;
        }
        return false;
    }

    public void removeDeclaration(Token name) {
        if (symbolTable.containsKey(name)) {
            symbolTable.remove(name);
        }
        else if (parent != null) {
            parent.removeDeclaration(name);
        }
    }

    public boolean alreadyDeclared(Token name) {
        return symbolTable.containsKey(name) || (parent != null && parent.alreadyDeclared(name));
    }

    public void setUsed(Token name) {
        if (!inDef) {
            VDI vdi;
            if (symbolTable.containsKey(name)) {
                vdi = symbolTable.get(name);
                if (vdi.getType().getTypeEnum() != TypeEnum.UNDEF) {
                    vdi.setStatus("okay");
                }
            }
            else if (parent != null && parent.alreadyDeclared(name)) {
                parent.setUsed(name);
            }
            else {
                addDeclaration(name, new Type());
                vdi = symbolTable.get(name);
                vdi.setStatus("undeclared");
            }
        }
    }

    public String getVSR(int indentDepth) {
        StringBuilder str = new StringBuilder();
        StringBuilder indentStr = new StringBuilder();
        for (int idx = 0; idx < indentDepth; idx++) {
            indentStr.append("  ");
        }
        for (Token name : new TreeSet<>(symbolTable.keySet())) {
            str.append(indentStr);
            str.append(symbolTable.get(name));
            str.append("\n");
        }
        return str.toString();
    }

    public TmpVDI getTmp(String id) {
        if (!tmpTable.containsKey(id)) {
            if (parent != null) {
                return parent.getTmp(id);
            }
            return null;
        }
        return tmpTable.get(id);
    }

    public String addTmp(Type type) {
        String tmpID = "tmp" + tmpCount;
        TmpVDI newTmp = new TmpVDI(tmpID, type, getTmp(getLastTmp()));
        tmpTable.put(tmpID, newTmp);
        lastTmp = tmpID;
        tmpCount += 1;
        return tmpID;
    }

    public int getTmpCount() {
        return tmpCount;
    }

    @Override
    public String toString() {
        return getVSR(0);
    }
}
